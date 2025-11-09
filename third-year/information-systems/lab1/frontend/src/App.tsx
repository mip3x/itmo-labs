import { useEffect, useMemo, useState } from "react";

import "./App.css";
import Th from "./components/Th";
import { countryFlag, formatDateISO, personToSearchText } from "./utils";
import type { PersonDTO } from "./types";

const API_BASE = "http://localhost:8080/api/v1/persons";

export default function App() {
    const [persons, setPerson] = useState<PersonDTO[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const [query, setQuery] = useState("");
    const [sortKey, setSortKey] = useState<keyof PersonDTO | "">("");
    const [sortDir, setSortDir] = useState<"asc" | "desc">("asc");

    const [page, setPage] = useState(1);
    const pageSize = 10;

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
        const inputQuery = query.trim().toLowerCase();
        if (!inputQuery) return persons;
        return persons.filter(p => personToSearchText(p).includes(inputQuery));
    }, [persons, query]);

    function toggleSort(k: keyof PersonDTO) {
        if (sortKey !== k) {
            setSortKey(k);
            setSortDir("asc");
        } else {
            setSortDir(d => (d === "asc" ? "desc" : "asc"));
        }
        setPage(1);
    }

    const sorted = useMemo(() => {
        if (!sortKey) return filtered;

        const arr = [...filtered];
        arr.sort((a: any, b: any) => {
            const A = a[sortKey];
            const B = b[sortKey];

            if (A == null && B == null) return 0;
            if (A == null) return 1;
            if (B == null) return -1;

            if (sortKey === "height" || sortKey === "weight") {
                const nA = Number(A), nB = Number(B);
                return (sortDir === "asc" ? nA - nB : nB - nA);
            }

            if (sortKey === "birthday") {
                const tA = new Date(A).getTime();
                const tB = new Date(B).getTime();
                return (sortDir === "asc" ? tA - tB : tB - tA);
            }

            const sA = String(A).toLowerCase();
            const sB = String(B).toLowerCase();
            const r = sA.localeCompare(sB, undefined, { numeric: true });

            return sortDir === "asc" ? r : -r;
        });

        return arr;
    }, [filtered, sortKey, sortDir]);

    const totalPages = Math.max(1, Math.ceil(sorted.length / pageSize));

    const pageData = useMemo(() => {
        const start = (page - 1) * pageSize;
        return sorted.slice(start, start + pageSize);
    }, [sorted, page]);

    return (
        <div style={{ padding: 16 }}>
        <h1>ИНТЕРПОЛ. ПАНЕЛЬ АДМИНИСТРИРОВАНИЯ</h1>

        <div style={{ display: "flex", gap: 8, alignItems: "center" }}>
            <input
                placeholder="Поиск: имя, локация, цвет глаз/волос, страна, вес/рост/год"
                value={query}
                onChange={(e) => { setQuery(e.target.value); setPage(1); }}
                style={{ padding: "6px 10px", minWidth: 360 }}
            />
            <button onClick={load} disabled={loading}>Update</button>
        </div>

        {error && <div style={{ color: "red", marginTop: 8 }}>{error}</div>}

        {loading ? (
            <div style={{ marginTop: 16 }}>Loading...</div>
        ) : (
            <>
            <table border={1} cellPadding={6} style={{ borderCollapse: "collapse", marginTop: 16, width: "100%" }}>
                <thead>
                <tr>
                    <Th k="id" title="ID" sortKey={sortKey} sortDir={sortDir} onSort={toggleSort} />
                    <Th k="name" title="Name" sortKey={sortKey} sortDir={sortDir} onSort={toggleSort} />
                    <Th k="eyeColor" title="Eye Color" sortKey={sortKey} sortDir={sortDir} onSort={toggleSort} />
                    <Th k="hairColor" title="Hair Color" sortKey={sortKey} sortDir={sortDir} onSort={toggleSort} />
                    <th>Location</th>
                    <Th k="weight" title="Weight" sortKey={sortKey} sortDir={sortDir} onSort={toggleSort} />
                    <Th k="height" title="Height" sortKey={sortKey} sortDir={sortDir} onSort={toggleSort} />
                    <Th k="birthday" title="Birthday" sortKey={sortKey} sortDir={sortDir} onSort={toggleSort} />
                    <Th k="nationality" title="Nationality" sortKey={sortKey} sortDir={sortDir} onSort={toggleSort} />
                </tr>
                </thead>
                <tbody>
                {pageData.map((p) => (
                    <tr key={p.id}>
                    <td>{p.id}</td>
                    <td>{p.name}</td>
                    <td>{p.eyeColor}</td>
                    <td>{p.hairColor}</td>
                    <td>{p.location?.name}</td>
                    <td>{p.weight}</td>
                    <td>{p.height ?? "-"}</td>
                    <td>{formatDateISO(p.birthday)}</td>
                    <td title={p.nationality}>{countryFlag(p.nationality)}</td>
                    </tr>
                ))}
                {pageData.length === 0 && (
                    <tr><td colSpan={9} style={{ textAlign: "center", padding: 16 }}>Ничего не найдено</td></tr>
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
