import { useEffect, useMemo, useState } from "react";

import "./App.css";
import Th from "./components/Th";
import { comparePersons, countryFlag, formatDateISO, parseNumber, searchableString, makeFieldPredicate, formatLocationCell } from "./utils";
import type { PersonDTO, SortKey } from "./types";

const API_BASE = "http://localhost:8080/api/v1/persons";

export default function App() {
    const [persons, setPerson] = useState<PersonDTO[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const [query, setQuery] = useState("");

    const [sortKey, setSortKey] = useState<SortKey>("");

    const [sortDirection, setSortDirection] = useState<"asc" | "desc">("asc");

    const [page, setPage] = useState(1);
    const pageSize = 10;

    const [showHelp, setShowHelp] = useState(false);

    async function load() {
        setLoading(true);
        setError(null);
        try {
            const result = await fetch(`${API_BASE}`);
            if (!result.ok) throw new Error(result.statusText);
            setPerson(await result.json());
        } catch (exception: any) {
            setError(exception.message ?? "Error");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        load();
    }, []);

    const filtered = useMemo(() => {
        const q = query.trim().toLowerCase();
        if (!q) return persons;

        const tokens = q.split(/\s+/);

        const positivePreds: ((p: PersonDTO) => boolean)[] = [];
        const negativePreds: ((p: PersonDTO) => boolean)[] = [];
        const positiveText: string[] = [];
        const negativeText: string[] = [];

        for (const tRaw of tokens) {
            if (!tRaw) continue;
            const isNeg = tRaw.startsWith("-");
            const t = isNeg ? tRaw.slice(1) : tRaw;

            if (t.includes(":")) {
                const pred = makeFieldPredicate(t);
                if (pred) (isNeg ? negativePreds : positivePreds).push(pred);
                continue;
            }

            if (/^\d{4}$/.test(t)) {
                const year = Number(t);
                const pred = (p: PersonDTO) => new Date(p.birthday).getFullYear() === year;
                (isNeg ? negativePreds : positivePreds).push(pred);
                continue;
            }

            const num = parseNumber(t);
            if (num != null) {
                const EPS = 1e-6;
                const pred = (p: PersonDTO) => {
                    const values = [
                        p.height ?? null,
                        p.weight ?? null,
                        p.coordinates?.x ?? null,
                        p.coordinates?.y ?? null,
                        p.location?.x ?? null,
                        p.location?.y ?? null,
                    ];
                    return values.some(v => v != null && Math.abs(Number(v) - num) < EPS);
                };

                (isNeg ? negativePreds : positivePreds).push(pred);
                continue;
            }

            (isNeg ? negativeText : positiveText).push(t);
        }

        return persons.filter((person) => {
            const text = searchableString(person);

            for (const pred of positivePreds) if (!pred(person)) return false;
            for (const pred of negativePreds) if (pred(person)) return false;

            for (const tt of positiveText) if (!text.includes(tt)) return false;
            for (const tt of negativeText) if (text.includes(tt)) return false;

            return true;
        });
    }, [persons, query]);

    function toggleSort(k: SortKey) {
        if (sortKey !== k) {
            setSortKey(k);
            setSortDirection("asc");
        } else {
            setSortDirection(d => (d === "asc" ? "desc" : "asc"));
        }
        setPage(1);
    }

    const sorted = useMemo(() => {
        if (!sortKey) return filtered;

        const arr = [...filtered];
        arr.sort((a, b) => comparePersons(a, b, sortKey, sortDirection));

        return arr;
    }, [filtered, sortKey, sortDirection]);

    const totalPages = Math.max(1, Math.ceil(sorted.length / pageSize));

    const pageData = useMemo(() => {
        const start = (page - 1) * pageSize;
        return sorted.slice(start, start + pageSize);
    }, [sorted, page]);

    return (
        <div style={{ padding: 16 }}>
        <h1>INTERPOL ADMINISTRATION PANEL</h1>

        <div style={{ display: "flex", gap: 8, alignItems: "center" }}>
            <input
                placeholder="Search: name, location, eye/hair color, country, weight/height/year"
                value={query}
                onChange={(e) => { setQuery(e.target.value); setPage(1); }}
                style={{ padding: "6px 10px", flex: 1 }}
            />

            <button onClick={() => setShowHelp(v => !v)} title="Show InterpolQL syntax help">?</button>

            <button onClick={load} disabled={loading} style={{ marginLeft: "auto" }}>
                Update
            </button>
        </div>

        {showHelp && (
            <div
                style={{
                marginTop: 8,
                padding: 8,
                border: "1px dashed #888",
                borderRadius: 6,
                background: "#fafafa",
                fontSize: 14,
                textAlign: "left",
                }}
            >
                <b>InterpolQL Syntax:</b>
                <ul style={{ margin: "6px 0 0 16px", padding: 0, lineHeight: 1.4 }}>
                <li><code>oleg blue</code> — all words must appear (AND)</li>
                <li><code>-spain</code> — exclude matches</li>
                <li><code>name:anna</code>, <code>loc:dubai</code>, <code>eye:blue</code>, <code>hair:black</code>, <code>nat:russia</code></li>
                <li><code>height:&gt;175</code>, <code>weight:&lt;=80</code>, <code>height:170..190</code></li>
                <li><code>birthday:&lt;1995-01-01</code>, <code>birthday:1990-01-01..2000-12-31</code></li>
                <li><code>1993</code> — matches by birth year; <code>180</code> — matches height or weight = 180</li>
                <li><code>cx:&gt;10</code>, <code>cy:0..100</code> — person coordinates (x/y)</li>
                <li><code>lx:&lt;20</code>, <code>ly:&gt;=50</code> — location coordinates (x/y)</li>
                </ul>
            </div>
        )}

        {error && <div style={{ color: "red", marginTop: 8 }}>{error}</div>}

        {loading ? (
            <div style={{ marginTop: 16 }}>Loading...</div>
        ) : (
            <>
            <table border={1} cellPadding={6} style={{ borderCollapse: "collapse", marginTop: 16, width: "100%" }}>
                <thead>
                <tr>
                    <Th k="id" title="ID" sortKey={sortKey} sortDir={sortDirection} onSort={toggleSort} />
                    <Th k="name" title="Name" sortKey={sortKey} sortDir={sortDirection} onSort={toggleSort} />
                    <Th k="eyeColor" title="Eye Color" sortKey={sortKey} sortDir={sortDirection} onSort={toggleSort} />
                    <Th k="hairColor" title="Hair Color" sortKey={sortKey} sortDir={sortDirection} onSort={toggleSort} />
                    <Th k="coordXY" title="Coordinates" sortKey={sortKey} sortDir={sortDirection} onSort={toggleSort} />
                    <Th k="locXY" title="Location" sortKey={sortKey} sortDir={sortDirection} onSort={toggleSort} />
                    <Th k="weight" title="Weight" sortKey={sortKey} sortDir={sortDirection} onSort={toggleSort} />
                    <Th k="height" title="Height" sortKey={sortKey} sortDir={sortDirection} onSort={toggleSort} />
                    <Th k="birthday" title="Birthday" sortKey={sortKey} sortDir={sortDirection} onSort={toggleSort} />
                    <Th k="nationality" title="Nationality" sortKey={sortKey} sortDir={sortDirection} onSort={toggleSort} />
                </tr>
                </thead>
                <tbody>
                {pageData.map((p) => (
                    <tr key={p.id}>
                    <td>{p.id}</td>
                    <td>{p.name}</td>
                    <td>{p.eyeColor}</td>
                    <td>{p.hairColor}</td>
                    <td>({p.coordinates?.x ?? "—"}, {p.coordinates?.y ?? "—"})</td>
                    <td title={p.location ? `x=${p.location.x ?? "-"}, y=${p.location.y ?? "-"}` : ""}>{formatLocationCell(p.location)}</td>
                    <td>{p.weight}</td>
                    <td>{p.height ?? "-"}</td>
                    <td>{formatDateISO(p.birthday)}</td>
                    <td title={p.nationality}>{countryFlag(p.nationality)}</td>
                    </tr>
                ))}
                {pageData.length === 0 && (
                    <tr><td colSpan={10} style={{ textAlign: "center", padding: 16 }}>Ничего не найдено</td></tr>
                )}
                </tbody>
            </table>

            <div style={{ display: "flex", gap: 8, justifyContent: "center", marginTop: 12 }}>
                <button onClick={() => setPage(1)} disabled={page <= 1}>«</button>
                <button onClick={() => setPage(p => p - 1)} disabled={page <= 1}>‹</button>
                <span>Стр. {page} / {totalPages}</span>
                <button onClick={() => setPage(p => p + 1)} disabled={page >= totalPages}>›</button>
                <button onClick={() => setPage(totalPages)} disabled={page >= totalPages}>»</button>
            </div>
            </>
        )}
        </div>
    );
}
