import { useEffect, useMemo, useState } from "react";

import "./App.css";
import Th from "./components/Th";
import PersonModal from "./components/PersonModal";
import SpecialOpsModal from "./components/SpecialOpsModal";

import {
    comparePersons,
    countryFlag,
    formatDateISO,
    parseNumber,
    searchableString,
    makeFieldPredicate,
    formatLocationCell
} from "./utils";

import type {
    Color,
    Coordinates,
    Location,
    PersonDto,
    PersonFormValues,
    SortKey
} from "./types";

import { COLOR_VALUES } from "./types";

const API_BASE = "http://localhost:8080/api/v1/people";
const MAX_INT = 2147483647;

const EMPTY_PERSON_FORM: PersonFormValues = {
    name: "",
    eyeColor: "BLUE",
    hairColor: "BLACK",
    nationality: "RUSSIA",

    weight: "",
    height: "",

    birthday: "",

    coordX: "",
    coordY: "",

    locX: "",
    locY: "",
    locName: "",
};

export default function App() {
    const [persons, setPerson] = useState<PersonDto[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [useServerPaging, setUseServerPaging] = useState(true);
    const [serverTotalPages, setServerTotalPages] = useState(1);

    const [query, setQuery] = useState("");

    const [sortKey, setSortKey] = useState<SortKey>("");
    const [sortDirection, setSortDirection] = useState<"asc" | "desc">("asc");

    const [page, setPage] = useState(1);
    const pageSize = 10;

    const [showHelp, setShowHelp] = useState(false);

    const [activeModalMode, setActiveModalMode] = useState<"create" | "edit" | null>(null);
    const [editingPerson, setEditingPerson] = useState<PersonDto | null>(null);
    const [weightLimit, setWeightLimit] = useState("");
    const [birthdayBefore, setBirthdayBefore] = useState("");
    const [hairColorShareColor, setHairColorShareColor] = useState<Color>(COLOR_VALUES[0]);
    const [eyeColorShareColor, setEyeColorShareColor] = useState<Color>(COLOR_VALUES[0]);
    const [heightSum, setHeightSum] = useState<number | null>(null);
    const [weightLessResult, setWeightLessResult] = useState<number | null>(null);
    const [birthdayBeforeList, setBirthdayBeforeList] = useState<PersonDto[]>([]);
    const [hairShare, setHairShare] = useState<number | null>(null);
    const [eyeShare, setEyeShare] = useState<number | null>(null);
    const [specialError, setSpecialError] = useState<string | null>(null);
    const [weightLimitError, setWeightLimitError] = useState<string | null>(null);
    const [birthdayBeforeError, setBirthdayBeforeError] = useState<string | null>(null);
    const [loadingOps, setLoadingOps] = useState({
        height: false,
        weight: false,
        birthday: false,
        hair: false,
        eye: false,
    });
    const [showSpecialOps, setShowSpecialOps] = useState(false);

    const serverPaging = useServerPaging && query.trim() === "";

    async function load() {
        setLoading(true);
        setError(null);
        try {
            if (serverPaging) {
                try {
                    await loadPage(page, sortKey, sortDirection);
                    setError(null);
                    return;
                } catch (err: any) {
                    setUseServerPaging(false); // fallback to client paging
                    setPage(1);
                    setServerTotalPages(1);
                    // swallow error, fallback to loadAll below
                }
            }

            await loadAll();
        } catch (exception: any) {
            setError(exception.message ?? "Error");
        } finally {
            setLoading(false);
        }
    }

    async function loadPage(pageNumber: number, sort: SortKey, dir: "asc" | "desc") {
        const params = new URLSearchParams();
        params.set("page", String(Math.max(0, pageNumber - 1)));
        params.set("size", String(pageSize));
        if (sort && sort !== "coordXY" && sort !== "locXY") {
            params.set("sort", `${sort},${dir}`);
        }

        const result = await fetch(`${API_BASE}/paged?${params.toString()}`);
        if (!result.ok) throw new Error(result.statusText);
        const data = await result.json();
        setPerson(data.content ?? []);
        setServerTotalPages(data.totalPages ?? 1);
    }

    async function loadAll() {
        const result = await fetch(`${API_BASE}`);
        if (!result.ok) throw new Error(result.statusText);
        setPerson(await result.json());
    }

    useEffect(() => {
        if (serverPaging) {
            load();
        }
    }, [serverPaging, page, sortKey, sortDirection]);

    useEffect(() => {
        if (!serverPaging) {
            load();
        }
    }, [serverPaging]);


    const filtered = useMemo(() => {
        if (serverPaging) return persons;

        const q = query.trim().toLowerCase();
        if (!q) return persons;

        const tokens = q.split(/\s+/);

        const positivePreds: ((p: PersonDto) => boolean)[] = [];
        const negativePreds: ((p: PersonDto) => boolean)[] = [];
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
                const currentYear = new Date().getFullYear();
                if (year >= 1900 && year <= currentYear + 5) {
                    const pred = (p: PersonDto) => new Date(p.birthday).getFullYear() === year;
                    (isNeg ? negativePreds : positivePreds).push(pred);
                    continue;
                }
            }

            const num = parseNumber(t);
            if (num != null) {
                const EPS = 1e-6;
                const needle = t.toLowerCase();
                const pred = (p: PersonDto) => {
                    const values = [
                        p.height ?? null,
                        p.weight ?? null,
                        p.coordinates?.x ?? null,
                        p.coordinates?.y ?? null,
                        p.location?.x ?? null,
                        p.location?.y ?? null,
                    ];

                    const exactMatch = values.some(v => v != null && Math.abs(Number(v) - num) < EPS);
                    if (exactMatch) return true;

                    const text = searchableString(p);
                    return text.includes(needle);
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
    }, [persons, query, serverPaging]);

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
        if (serverPaging) return filtered;
        if (!sortKey) return filtered;

        const arr = [...filtered];
        arr.sort((a, b) => comparePersons(a, b, sortKey, sortDirection));

        return arr;
    }, [filtered, sortKey, sortDirection, serverPaging]);

    const totalPages = serverPaging ? serverTotalPages : Math.max(1, Math.ceil(sorted.length / pageSize));

    const pageData = useMemo(() => {
        if (serverPaging) return sorted;
        const start = (page - 1) * pageSize;
        return sorted.slice(start, start + pageSize);
    }, [sorted, page, serverPaging]);

    const [knownCoords, setKnownCoords] = useState<Coordinates[]>([]);
    const [knownLocations, setKnownLocations] = useState<Location[]>([]);

    useEffect(() => {
        setKnownCoords(prev => {
            const map = new Map<string, Coordinates>();
            for (const c of prev) {
                map.set(`${c.x}|${c.y}`, c);
            }
            for (const person of persons) {
                if (!person.coordinates) continue;
                map.set(`${person.coordinates.x}|${person.coordinates.y}`, person.coordinates);
            }
            return Array.from(map.values());
        });

        setKnownLocations(prev => {
            const map = new Map<string, Location>();
            for (const l of prev) {
                map.set(`${l.x}|${l.y}|${l.name}`, l);
            }
            for (const person of persons) {
                if (!person.location) continue;
                map.set(`${person.location.x}|${person.location.y}|${person.location.name}`, person.location);
            }
            return Array.from(map.values());
        });
    }, [persons]);

    function openCreateModal() {
        setEditingPerson(null);
        setActiveModalMode("create");
    }

    function openEditModal(person: PersonDto) {
        setEditingPerson(person);
        setActiveModalMode("edit");
    }

    function closeModal() {
        setActiveModalMode(null);
        setEditingPerson(null);
    }

    function personToFormValues(p: PersonDto): PersonFormValues {
        return {
            name: p.name ?? "",
            eyeColor: p.eyeColor,
            hairColor: p.hairColor,
            nationality: p.nationality,
            weight: p.weight != null ? String(p.weight) : "",
            height: p.height != null ? String(p.height) : "",
            birthday: p.birthday ? p.birthday.slice(0, 10) : "",

            coordX: p.coordinates ? String(p.coordinates.x) : "",
            coordY: p.coordinates ? String(p.coordinates.y) : "",

            locX: p.location ? String(p.location.x) : "",
            locY: p.location ? String(p.location.y) : "",
            locName: p.location?.name ?? "",
        };
    }

    async function handlePersonModalSubmit(values: PersonFormValues) {
        setError(null);

        const datePart = values.birthday.trim();
        let birthdayIso: string;

        if (activeModalMode === "edit" && editingPerson && editingPerson.birthday) {
            const tIndex = editingPerson.birthday.indexOf("T");

            if (tIndex !== -1) {
                const timeAndOffset = editingPerson.birthday.substring(tIndex);
                birthdayIso = `${datePart}${timeAndOffset}`;
            } else {
                birthdayIso = `${datePart}T00:00:00Z`;
            }
        } else {
            birthdayIso = `${datePart}T00:00:00Z`;
        }

        const payload = {
            name: values.name.trim(),
            eyeColor: values.eyeColor,
            hairColor: values.hairColor,
            nationality: values.nationality,
            weight: Number(values.weight),
            height: values.height.trim() === "" ? null : Number(values.height),
            birthday: birthdayIso,

            coordinates: {
                x: Number(values.coordX),
                y: Number(values.coordY),
            },
            location: {
                x: Number(values.locX),
                y: Number(values.locY),
                name: values.locName.trim(),
            },
        };

        const jsonBody = JSON.stringify(payload);
        console.log("Sending payload to server:", jsonBody);

        try {
            let response: Response;

            if (activeModalMode === "create") {
                response = await fetch(API_BASE, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload),
                });
            } else if (activeModalMode === "edit" && editingPerson) {
                response = await fetch(`${API_BASE}/${editingPerson.id}`, {
                    method: "PATCH",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload),
                });
            } else {
                return;
            }

            if (!response.ok) {
                const text = await response.text();
                throw new Error(text || `Request error: ${response.status}`);
            }

            const saved: PersonDto = await response.json();

            if (serverPaging) {
                await loadPage(page, sortKey, sortDirection);
            } else {
                setPerson(prev => {
                    const index = prev.findIndex(p => p.id === saved.id);
                    if (index === -1) {
                        return [...prev, saved]; // creating
                    } else {
                        const copy = [...prev];
                        copy[index] = saved; // updating
                        return copy;
                    }
                });
            }

            closeModal();
        } catch (e: any) {
            setError(e?.message ?? "Error saving object");
        }
    }

    async function handleDeletePerson(id: number) {
        if (!window.confirm(`Delete object #${id}?`)) return;

        setError(null);

        try {
            const response = await fetch(`${API_BASE}/${id}`, { method: "DELETE" });

            if (!response.ok) {
                const text = await response.text();
                throw new Error(text || `Request error: ${response.status}`);
            }

            if (serverPaging) {
                await loadPage(page, sortKey, sortDirection);
            } else {
                setPerson(prev => prev.filter(p => p.id !== id));
            }
            closeModal();
        } catch (e: any) {
            setError(e?.message ?? "Error deleting object");
        }
    }

    function setOpLoading<K extends keyof typeof loadingOps>(key: K, value: boolean) {
        setLoadingOps(prev => ({ ...prev, [key]: value }));
    }

    function sanitizeIntInput(value: string): string {
        const digits = value.replace(/\D+/g, "");
        if (!digits) return "";
        const num = Number(digits);
        if (!Number.isFinite(num)) return "";
        return String(Math.min(num, MAX_INT));
    }

    function handleWeightLimitChange(v: string) {
        setSpecialError(null);
        setWeightLimitError(null);
        const sanitized = sanitizeIntInput(v);
        if (sanitized === "") {
            setWeightLimit("");
            return;
        }
        const num = Number(sanitized);
        if (num < 1 || num > MAX_INT) {
            setWeightLimitError("Enter integer 1.." + MAX_INT);
            return;
        }
        setWeightLimit(sanitized);
    }

    function handleBirthdayBeforeChange(v: string) {
        setSpecialError(null);
        setBirthdayBeforeError(null);
        setBirthdayBefore(v);
    }

    function isValidDateInput(dateStr: string) {
        if (!dateStr || dateStr.length < 10) return false;

        const yearStr = dateStr.slice(0, 4);
        const monthStr = dateStr.slice(5, 7);
        const dayStr = dateStr.slice(8, 10);

        const year = Number(yearStr);
        const month = Number(monthStr);
        const day = Number(dayStr);
        const currentYear = new Date().getFullYear();

        if (!Number.isFinite(year) || !Number.isFinite(month) || !Number.isFinite(day)) return false;
        if (year < 1900 || year > currentYear) return false;

        const d = new Date(dateStr);
        if (Number.isNaN(d.getTime())) return false;
        if (d.getFullYear() !== year || d.getMonth() + 1 !== month || d.getDate() !== day) return false;

        return true;
    }

    async function runHeightSum() {
        setSpecialError(null);
        setOpLoading("height", true);
        try {
            const response = await fetch(`${API_BASE}/stats/height/sum`);
            if (!response.ok) throw new Error((await response.text()) || response.statusText);
            const val = await response.json();
            setHeightSum(val);
        } catch (e: any) {
            setSpecialError(e?.message ?? "Failed to calculate height sum");
        } finally {
            setOpLoading("height", false);
        }
    }

    async function runWeightLess() {
        setSpecialError(null);
        setWeightLimitError(null);
        const limit = Number(weightLimit);
        if (!Number.isFinite(limit) || limit < 1 || limit > MAX_INT) {
            setWeightLimitError("Enter integer 1.." + MAX_INT);
            return;
        }
        setOpLoading("weight", true);
        try {
            const response = await fetch(`${API_BASE}/stats/weight/less-than?value=${limit}`);
            if (!response.ok) throw new Error((await response.text()) || response.statusText);
            const val = await response.json();
            setWeightLessResult(val);
        } catch (e: any) {
            setSpecialError(e?.message ?? "Failed to count weight");
        } finally {
            setOpLoading("weight", false);
        }
    }

    async function runBirthdayBefore() {
        setSpecialError(null);
        setBirthdayBeforeError(null);
        const dateStr = birthdayBefore.trim();
        if (!dateStr) {
            setBirthdayBeforeError("Enter a valid date");
            return;
        }
        if (!isValidDateInput(dateStr)) {
            setBirthdayBeforeError("Invalid date: use calendar picker or YYYY-MM-DD");
            return;
        }

        const iso = `${dateStr}T00:00:00Z`;
        setOpLoading("birthday", true);
        try {
            const response = await fetch(`${API_BASE}/stats/birthday/before?date=${encodeURIComponent(iso)}`);
            if (!response.ok) throw new Error((await response.text()) || response.statusText);
            const list: PersonDto[] = await response.json();
            setBirthdayBeforeList(list);
        } catch (e: any) {
            setSpecialError(e?.message ?? "Failed to fetch birthday list");
        } finally {
            setOpLoading("birthday", false);
        }
    }

    async function runHairShare() {
        setSpecialError(null);
        setOpLoading("hair", true);
        try {
            const response = await fetch(`${API_BASE}/stats/hair/share?color=${hairColorShareColor}`);
            if (!response.ok) throw new Error((await response.text()) || response.statusText);
            const val = await response.json();
            setHairShare(val);
        } catch (e: any) {
            setSpecialError(e?.message ?? "Failed to calc hair share");
        } finally {
            setOpLoading("hair", false);
        }
    }

    async function runEyeShare() {
        setSpecialError(null);
        setOpLoading("eye", true);
        try {
            const response = await fetch(`${API_BASE}/stats/eye/share?color=${eyeColorShareColor}`);
            if (!response.ok) throw new Error((await response.text()) || response.statusText);
            const val = await response.json();
            setEyeShare(val);
        } catch (e: any) {
            setSpecialError(e?.message ?? "Failed to calc eye share");
        } finally {
            setOpLoading("eye", false);
        }
    }

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

            <button onClick={openCreateModal}>+ Add</button>
            <button onClick={() => setShowSpecialOps(true)}>Special Ops</button>

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

        {showSpecialOps && (
            <SpecialOpsModal
                onClose={() => setShowSpecialOps(false)}
                error={specialError}
                loading={loadingOps}
                heightSum={heightSum}
                onHeight={runHeightSum}
                weightLimit={weightLimit}
                setWeightLimit={handleWeightLimitChange}
                weightLimitError={weightLimitError}
                weightLessResult={weightLessResult}
                onWeightLess={runWeightLess}
                birthdayBefore={birthdayBefore}
                setBirthdayBefore={handleBirthdayBeforeChange}
                birthdayBeforeError={birthdayBeforeError}
                birthdayBeforeList={birthdayBeforeList}
                onBirthdayBefore={runBirthdayBefore}
                hairColor={hairColorShareColor}
                setHairColor={setHairColorShareColor}
                hairShare={hairShare}
                onHairShare={runHairShare}
                eyeColor={eyeColorShareColor}
                setEyeColor={setEyeColorShareColor}
                eyeShare={eyeShare}
                onEyeShare={runEyeShare}
            />
        )}

        {activeModalMode && (
            <PersonModal
                mode={activeModalMode}
                initialValues={
                    activeModalMode === "create" || !editingPerson
                        ? EMPTY_PERSON_FORM
                        : personToFormValues(editingPerson)
                }
                existingCoords={knownCoords}
                existingLocations={knownLocations}
                onCancel={closeModal}
                onSubmit={handlePersonModalSubmit}
                onDelete={activeModalMode === "edit" && editingPerson
                    ? () => handleDeletePerson(editingPerson.id)
                    : undefined}
            />
        )}

        {error && <div style={{ color: "red", marginTop: 8 }}>{error}</div>}

        {loading ? (
            <div style={{ marginTop: 16 }}>Loading...</div>
        ) : (
            <>
            <table
                border={1}
                cellPadding={6}
                style={{ borderCollapse: "collapse", marginTop: 16, width: "100%" }}
            >
                <thead>
                    <tr>
                        <Th
                            k="id"
                            title="ID"
                            sortKey={sortKey}
                            sortDir={sortDirection}
                            onSort={toggleSort}
                        />
                        <Th
                            k="name"
                            title="Name"
                            sortKey={sortKey}
                            sortDir={sortDirection}
                            onSort={toggleSort}
                        />
                        <Th
                            k="eyeColor"
                            title="Eye Color"
                            sortKey={sortKey}
                            sortDir={sortDirection}
                            onSort={toggleSort}
                        />
                        <Th
                            k="hairColor"
                            title="Hair Color"
                            sortKey={sortKey}
                            sortDir={sortDirection}
                            onSort={toggleSort}
                        />
                        <Th
                            k="coordXY"
                            title="Coordinates"
                            sortKey={sortKey}
                            sortDir={sortDirection}
                            onSort={toggleSort}
                        />
                        <Th
                            k="locXY"
                            title="Location"
                            sortKey={sortKey}
                            sortDir={sortDirection}
                            onSort={toggleSort}
                        />
                        <Th
                            k="weight"
                            title="Weight"
                            sortKey={sortKey}
                            sortDir={sortDirection}
                            onSort={toggleSort}
                        />
                        <Th
                            k="height"
                            title="Height"
                            sortKey={sortKey}
                            sortDir={sortDirection}
                            onSort={toggleSort}
                        />
                        <Th
                            k="birthday"
                            title="Birthday"
                            sortKey={sortKey}
                            sortDir={sortDirection}
                            onSort={toggleSort}
                        />
                        <Th
                            k="nationality"
                            title="Nationality"
                            sortKey={sortKey}
                            sortDir={sortDirection}
                            onSort={toggleSort}
                        />
                        <th>Actions</th>
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
                            <td
                                title={
                                    p.location
                                        ? `x=${p.location.x ?? "-"}, y=${p.location.y ?? "-"}`
                                        : ""
                                }
                            >
                                {formatLocationCell(p.location)}
                            </td>
                            <td>{p.weight}</td>
                            <td>{p.height ?? "-"}</td>
                            <td>{formatDateISO(p.birthday)}</td>
                            <td title={p.nationality}>{countryFlag(p.nationality)}</td>
                            <td>
                                <button onClick={() => openEditModal(p)}>
                                    Edit
                                </button>
                            </td>
                        </tr>
                    ))}
                    {pageData.length === 0 && (
                        <tr>
                            <td colSpan={11} style={{ textAlign: "center", padding: 16 }}>
                                Nothing found
                            </td>
                        </tr>
                    )}
                </tbody>
            </table>

            <div style={{ display: "flex", gap: 8, justifyContent: "center", marginTop: 12 }}>
                <button onClick={() => setPage(1)} disabled={page <= 1}>«</button>
                <button onClick={() => setPage(p => p - 1)} disabled={page <= 1}>‹</button>
                <span>Page {page} / {totalPages}</span>
                <button onClick={() => setPage(p => p + 1)} disabled={page >= totalPages}>›</button>
                <button onClick={() => setPage(totalPages)} disabled={page >= totalPages}>»</button>
            </div>
            </>
        )}
        </div>
    );
}
