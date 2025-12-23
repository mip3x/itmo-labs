import { type FormEvent, useEffect, useState } from "react";

import {
    COLOR_VALUES,
    COUNTRY_VALUES,
    type Color,
    type Country,
    type Coordinates,
    type Location,
    type PersonFormErrors,
    type PersonFormValues,
} from "../types";

const MAX_INT = 2147483647;
const MAX_LONG = Number.MAX_SAFE_INTEGER;
const MIN_LONG = -Number.MAX_SAFE_INTEGER;

const WEIGHT_MIN = 1;
const HEIGHT_MIN = 1;
const COORD_X_MIN_EXCLUSIVE = -860;
const COORD_Y_MAX = 396;

export type PersonModalMode = "create" | "edit";

export type PersonModalProps = {
    mode: PersonModalMode;
    initialValues: PersonFormValues;
    existingCoords: Coordinates[];
    existingLocations: Location[];
    onSubmit: (values: PersonFormValues) => void;
    onCancel: () => void;
    onDelete?: () => void;
};

export default function PersonModal(props: PersonModalProps) {
    const {
        mode,
        initialValues,
        existingCoords,
        existingLocations,
        onSubmit,
        onCancel,
        onDelete,
    } = props;

    const [form, setForm] = useState<PersonFormValues>(initialValues);
    const [errors, setErrors] = useState<PersonFormErrors>({});
    const [coordsPickerOpen, setCoordsPickerOpen] = useState(false);
    const [coordsSearch, setCoordsSearch] = useState("");
    const [locPickerOpen, setLocPickerOpen] = useState(false);
    const [locSearch, setLocSearch] = useState("");

    function setField<K extends keyof PersonFormValues>(key: K, value: PersonFormValues[K]) {
        setForm(prev => {
            const next = { ...prev, [key]: value };
            setErrors(prevErrors => {
                const nextErrors: PersonFormErrors = { ...prevErrors };
                const fieldError = validateField(key, next);
                if (fieldError) {
                    nextErrors[key] = fieldError;
                } else {
                    delete nextErrors[key];
                }
                return nextErrors;
            });
            return next;
        });
    }

    function sanitizeNumericInput(key: keyof PersonFormValues, raw: string): string {
        const value = raw.replace(/,/g, ".");
        const allowSign = key === "coordX" || key === "coordY" || key === "locX" || key === "locY";
        const allowDot = key === "coordX" || key === "coordY" || key === "locX";

        let result = "";
        let hasDot = false;

        for (let i = 0; i < value.length; i++) {
            const ch = value[i];

            if (ch >= "0" && ch <= "9") {
                result += ch;
                continue;
            }

            if (ch === "-" && allowSign && i === 0) {
                result += ch;
                continue;
            }

            if (ch === "." && allowDot && !hasDot) {
                result += ch;
                hasDot = true;
            }
        }

        return result;
    }

    function handleNumericChange<K extends keyof PersonFormValues>(key: K, rawValue: PersonFormValues[K]) {
        const sanitized = sanitizeNumericInput(key, String(rawValue));
        const partial = sanitized === "" || sanitized === "-" || sanitized === "." || sanitized === "-.";

        if (!partial) {
            const num = Number(sanitized);
            if (!Number.isFinite(num)) return;

            switch (key) {
                case "weight":
                    if (num < WEIGHT_MIN || num > MAX_INT) return;
                    break;
                case "height":
                    if (num < HEIGHT_MIN || num > MAX_LONG) return;
                    break;
                case "coordX":
                    if (num <= COORD_X_MIN_EXCLUSIVE || num < MIN_LONG || num > MAX_LONG) return;
                    break;
                case "coordY":
                    if (num > COORD_Y_MAX || num < MIN_LONG || num > MAX_LONG) return;
                    break;
                case "locX":
                    if (num < MIN_LONG || num > MAX_LONG) return;
                    break;
                case "locY":
                    if (num < MIN_LONG || num > MAX_LONG) return;
                    break;
                default:
                    break;
            }
        }

        setField(key, sanitized as PersonFormValues[K]);
    }

    function validate(values: PersonFormValues): PersonFormErrors {
        const errs: PersonFormErrors = {};

        const intPattern = /^-?\d+$/;
        const floatPattern = /^-?\d+(\.\d+)?$/;

        // name
        if (!values.name.trim()) {
            errs.name = "Name should not be empty";
        }

        // weight
        if (!values.weight.trim()) {
            errs.weight = "Weight is obligatory";
        } else if (!intPattern.test(values.weight.trim())) {
            errs.weight = "Weight must contain only digits";
        } else {
            const w = Number(values.weight);
            if (!Number.isFinite(w)) {
                errs.weight = "Weight must be a valid number";
            } else if (!Number.isInteger(w)) {
                errs.weight = "Weight must be an integer number";
            } else if (w <= 0) {
                errs.weight = "Weight must be > 0";
            } else if (w > MAX_INT) {
                errs.weight = `Weight must be <= ${MAX_INT}`;
            }
        }

        // height
        if (values.height.trim()) {
            if (!intPattern.test(values.height.trim())) {
                errs.height = "Height must contain only digits";
            }

            const h = Number(values.height);
            if (!Number.isFinite(h)) {
                errs.height = "Height must be a valid number";
            } else if (!Number.isInteger(h)) {
                errs.height = "Height must be an integer number";
            } else if (h <= 0) {
                errs.height = "Height must be > 0";
            } else if (h > MAX_LONG) {
                errs.height = `Height must be <= ${MAX_LONG}`;
            }
        }

        // birthday
        if (!values.birthday.trim()) {
            errs.birthday = "Birthday is obligatory";
        } else {
            const yearStr = values.birthday.slice(0, 4);
            const monthStr = values.birthday.slice(5, 7);
            const dayStr = values.birthday.slice(8, 10);

            const year = Number(yearStr);
            const month = Number(monthStr);
            const day = Number(dayStr);
            const currentYear = new Date().getFullYear();

            if (!Number.isFinite(year) || !Number.isFinite(month) || !Number.isFinite(day)) {
                errs.birthday = "Birthday contains invalid date components";
            } else if (year < 1900) {
                errs.birthday = "Birthday year must be >= 1900";
            } else if (year > currentYear) {
                errs.birthday = "Birthday year cannot be in the future";
            } else {
                const d = new Date(values.birthday);
                if (Number.isNaN(d.getTime())) {
                    errs.birthday = "Birthday must be a valid calendar date";
                }
            }
        }

        // coords
        const cxValid = floatPattern.test(values.coordX.trim());
        const cyValid = floatPattern.test(values.coordY.trim());
        const cx = Number(values.coordX);
        const cy = Number(values.coordY);

        // x must be > -860
        if (!cxValid) {
            errs.coordX = "Coordinate X must be a number";
        } else if (!Number.isFinite(cx)) {
            errs.coordX = "Coordinate X must be a valid number";
        } else if (cx <= COORD_X_MIN_EXCLUSIVE) {
            errs.coordX = "Coordinate X must be > -860";
        } else if (cx < MIN_LONG || cx > MAX_LONG) {
            errs.coordX = "Coordinate X exceeds number limits";
        }

        // y must be <= 396 and not null
        if (!cyValid) {
            errs.coordY = "Coordinate Y must be a number";
        } else if (!Number.isFinite(cy)) {
            errs.coordY = "Coordinate Y must be a valid number";
        } else if (cy > COORD_Y_MAX) {
            errs.coordY = "Coordinate Y must be <= 396";
        } else if (cy < MIN_LONG || cy > MAX_LONG) {
            errs.coordY = "Coordinate Y exceeds number limits";
        }

        // location
        const lxValid = floatPattern.test(values.locX.trim());
        const lyValid = intPattern.test(values.locY.trim());
        const lx = Number(values.locX);
        const ly = Number(values.locY);

        if (!lxValid) {
            errs.locX = "Location X must be a number";
        } else if (!Number.isFinite(lx)) {
            errs.locX = "Location X must be a valid number";
        } else if (lx < MIN_LONG || lx > MAX_LONG) {
            errs.locX = "Location X exceeds number limits";
        }

        if (!lyValid) {
            errs.locY = "Location Y must be an integer";
        } else if (!Number.isFinite(ly)) {
            errs.locY = "Location Y must be a valid number";
        } else if (ly < MIN_LONG || ly > MAX_LONG) {
            errs.locY = "Location Y exceeds number limits";
        }

        if (!values.locName.trim()) {
            errs.locName = "Location name is obligatory";
        } else if (values.locName.length < 2) {
            errs.locName = "Location name is too short";
        } else if (values.locName.length > 255) {
            errs.locName = "Location name is too long (max 255 chars)";
        }

        return errs;
    }

    function validateField<K extends keyof PersonFormValues>(key: K, values: PersonFormValues) {
        return validate(values)[key];
    }

    function handleSubmit(event: FormEvent) {
        event.preventDefault();
        const errs = validate(form);
        if (Object.keys(errs).length > 0) {
            setErrors(errs);
            return;
        }
        onSubmit(form);
    }

    function useExistingCoords(c: Coordinates) {
        setField("coordX", String(c.x));
        setField("coordY", String(c.y));
    }

    function useExistingLocation(l: Location) {
        setField("locX", String(l.x));
        setField("locY", String(l.y));
        setField("locName", l.name);
    }

    const coordItems = existingCoords
        .filter(c => {
            const needle = coordsSearch.trim().toLowerCase();
            if (!needle) return true;
            const text = `${c.x} ${c.y}`.toLowerCase();
            return text.includes(needle);
        })
        .map(c => ({
            key: `${c.x}|${c.y}`,
            label: `(${c.x}, ${c.y})`,
            onPick: () => {
                useExistingCoords(c);
                setCoordsPickerOpen(false);
            },
        }));

    const locItems = existingLocations
        .filter(l => {
            const needle = locSearch.trim().toLowerCase();
            if (!needle) return true;
            const text = `${l.name} ${l.x} ${l.y}`.toLowerCase();
            return text.includes(needle);
        })
        .map(l => ({
            key: `${l.x}|${l.y}|${l.name}`,
            label: `${l.name} (x=${l.x}, y=${l.y})`,
            onPick: () => {
                useExistingLocation(l);
                setLocPickerOpen(false);
            },
        }));

    useEffect(() => {
        function onKeyDown(e: KeyboardEvent) {
            if (e.key === "Escape") {
                onCancel();
            }
        }

        window.addEventListener("keydown", onKeyDown);
        return () => window.removeEventListener("keydown", onKeyDown);
    }, [onCancel]);

    return (
        <>
            <div
                style={{
                    position: "fixed",
                    inset: 0,
                    background: "rgba(0,0,0,0.4)",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    zIndex: 1000,
                }}
            >
                <div
                    style={{
                        background: "#fff",
                        color: "#000",
                        padding: 16,
                        borderRadius: 8,
                        minWidth: 480,
                        maxWidth: 640,
                    }}
                >
                    <h2>{mode === "create" ? "New Person" : "Edit Person"}</h2>

                    <form
                        onSubmit={handleSubmit}
                        style={{ display: "grid", gap: 8 }}
                    >
                        {/* Name */}
                        <label style={{ textAlign: "left" }}>
                            Name:
                            <input
                                type="text"
                                value={form.name}
                                maxLength={255}
                                onChange={e => setField("name", e.target.value)}
                                style={{ width: "100%", marginTop: 4 }}
                            />
                            {errors.name && (
                                <div style={{ color: "red", fontSize: 12 }}>{errors.name}</div>
                            )}
                        </label>

                        {/* Color of eye / hair */}
                        <div style={{ display: "flex", gap: 8 }}>
                            <label style={{ flex: 1, textAlign: "left" }}>
                                Eye Color:
                                <select
                                    value={form.eyeColor}
                                    onChange={e => setField("eyeColor", e.target.value as Color)}
                                    style={{ width: "100%", marginTop: 4 }}
                                >
                                    {COLOR_VALUES.map(c => (
                                        <option key={c} value={c}>{c}</option>
                                    ))}
                                </select>
                            </label>

                            <label style={{ flex: 1, textAlign: "left" }}>
                                Hair Color:
                                <select
                                    value={form.hairColor}
                                    onChange={e => setField("hairColor", e.target.value as Color)}
                                    style={{ width: "100%", marginTop: 4 }}
                                >
                                    {COLOR_VALUES.map(c => (
                                        <option key={c} value={c}>{c}</option>
                                    ))}
                                </select>
                            </label>
                        </div>

                        {/* Nationality */}
                        <label style={{ textAlign: "left" }}>
                            Nationality:
                            <select
                                value={form.nationality}
                                onChange={e => setField("nationality", e.target.value as Country)}
                                style={{ width: "100%", marginTop: 4 }}
                            >
                                {COUNTRY_VALUES.map(c => (
                                    <option key={c} value={c}>{c}</option>
                                ))}
                            </select>
                        </label>

                        {/* Weight / Height */}
                        <div style={{ display: "flex", gap: 8 }}>
                            <label style={{ flex: 1, textAlign: "left" }}>
                                Weight (kg, &gt; 0):
                            <input
                                type="text"
                                inputMode="numeric"
                                pattern="^\d*$"
                                value={form.weight}
                                min={WEIGHT_MIN}
                                max={MAX_INT}
                                onChange={e => handleNumericChange("weight", e.target.value)}
                                style={{ width: "100%", marginTop: 4 }}
                            />
                                {errors.weight && (
                                    <div style={{ color: "red", fontSize: 12 }}>{errors.weight}</div>
                                )}
                            </label>

                            <label style={{ flex: 1, textAlign: "left" }}>
                                Height (cm, &gt; 0, could be empty):
                            <input
                                type="text"
                                inputMode="numeric"
                                pattern="^\d*$"
                                value={form.height}
                                min={HEIGHT_MIN}
                                max={MAX_LONG}
                                onChange={e => handleNumericChange("height", e.target.value)}
                                style={{ width: "100%", marginTop: 4 }}
                            />
                                {errors.height && (
                                    <div style={{ color: "red", fontSize: 12 }}>{errors.height}</div>
                                )}
                            </label>
                        </div>

                        {/* Birthday */}
                        <label style={{ textAlign: "left" }}>
                            Birthday:
                            <input
                                type="date"
                                value={form.birthday}
                                onChange={e => setField("birthday", e.target.value)}
                                style={{ width: "100%", marginTop: 4 }}
                            />
                            {errors.birthday && (
                                <div style={{ color: "red", fontSize: 12 }}>{errors.birthday}</div>
                            )}
                        </label>

                        {/* Coordinates */}
                        <fieldset style={{ border: "1px solid #ccc", padding: 8 }}>
                            <legend>Coordinates</legend>
                            <div style={{ display: "flex", gap: 8 }}>
                                <label style={{ flex: 1, textAlign: "left" }}>
                                    x (&gt; -860):
                            <input
                                type="text"
                                inputMode="decimal"
                                value={form.coordX}
                                min={COORD_X_MIN_EXCLUSIVE + 1}
                                onChange={e => handleNumericChange("coordX", e.target.value)}
                                style={{ width: "100%", marginTop: 4 }}
                            />
                                    {errors.coordX && (
                                        <div style={{ color: "red", fontSize: 12 }}>{errors.coordX}</div>
                                    )}
                                </label>
                                <label style={{ flex: 1, textAlign: "left" }}>
                                    y (≤ 396):
                            <input
                                type="text"
                                inputMode="decimal"
                                value={form.coordY}
                                max={COORD_Y_MAX}
                                onChange={e => handleNumericChange("coordY", e.target.value)}
                                style={{ width: "100%", marginTop: 4 }}
                            />
                                    {errors.coordY && (
                                        <div style={{ color: "red", fontSize: 12 }}>{errors.coordY}</div>
                                    )}
                                </label>
                            </div>

                            {existingCoords.length > 0 && (
                                <div style={{ marginTop: 8, textAlign: "left", fontSize: 12 }}>
                                    Use existing coordinates:
                                    <button
                                        type="button"
                                        style={{ marginLeft: 8, fontSize: 12 }}
                                        onClick={() => setCoordsPickerOpen(true)}
                                    >
                                        Open list
                                    </button>
                                </div>
                            )}
                        </fieldset>

                        {/* Location */}
                        <fieldset style={{ border: "1px solid #ccc", padding: 8 }}>
                            <legend>Location</legend>
                            <div style={{ display: "flex", gap: 8, marginBottom: 8 }}>
                                <label style={{ flex: 1, textAlign: "left" }}>
                                    x:
                                <input
                                    type="text"
                                    inputMode="decimal"
                                    value={form.locX}
                                    onChange={e => handleNumericChange("locX", e.target.value)}
                                    style={{ width: "100%", marginTop: 4 }}
                                />
                                    {errors.locX && (
                                        <div style={{ color: "red", fontSize: 12 }}>{errors.locX}</div>
                                    )}
                                </label>
                                <label style={{ flex: 1, textAlign: "left" }}>
                                    y:
                                <input
                                    type="text"
                                    inputMode="numeric"
                                    value={form.locY}
                                    onChange={e => handleNumericChange("locY", e.target.value)}
                                    style={{ width: "100%", marginTop: 4 }}
                                />
                                    {errors.locY && (
                                        <div style={{ color: "red", fontSize: 12 }}>{errors.locY}</div>
                                    )}
                                </label>
                            </div>

                            <label style={{ textAlign: "left" }}>
                                Name:
                                <input
                                    type="text"
                                    value={form.locName}
                                    onChange={e => setField("locName", e.target.value)}
                                    style={{ width: "100%", marginTop: 4 }}
                                />
                                {errors.locName && (
                                    <div style={{ color: "red", fontSize: 12 }}>{errors.locName}</div>
                                )}
                            </label>

                            {existingLocations.length > 0 && (
                                <div style={{ marginTop: 8, textAlign: "left", fontSize: 12 }}>
                                    Use existing location:
                                    <button
                                        type="button"
                                        style={{ marginLeft: 8, fontSize: 12 }}
                                        onClick={() => setLocPickerOpen(true)}
                                    >
                                        Open list
                                    </button>
                                </div>
                            )}
                        </fieldset>

                        {/* Buttons */}
                        <div
                            style={{
                                display: "flex",
                                justifyContent: "flex-end",
                                gap: 8,
                                marginTop: 8,
                            }}
                        >
                            {mode === "edit" && onDelete && (
                                <button
                                    type="button"
                                    onClick={onDelete}
                                    style={{ marginRight: "auto", color: "white", background: "#c00" }}
                                >
                                    Delete
                                </button>
                            )}

                            <button type="button" onClick={onCancel}>
                                Cancel
                            </button>
                            <button type="submit">
                                Save
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            {coordsPickerOpen && (
                <PickerModal
                    title="Choose coordinates"
                    search={coordsSearch}
                    onSearch={setCoordsSearch}
                    items={coordItems}
                    onClose={() => {
                        setCoordsPickerOpen(false);
                        setCoordsSearch("");
                    }}
                />
            )}
            {locPickerOpen && (
                <PickerModal
                    title="Choose location"
                    search={locSearch}
                    onSearch={setLocSearch}
                    items={locItems}
                    onClose={() => {
                        setLocPickerOpen(false);
                        setLocSearch("");
                    }}
                />
            )}
        </>
    );
}

function PickerModal<T extends { key: string; label: string; onPick: () => void }>(props: {
    title: string;
    search: string;
    onSearch: (v: string) => void;
    items: T[];
    onClose: () => void;
}) {
    const { title, search, onSearch, items, onClose } = props;

    return (
        <div
            style={{
                position: "fixed",
                inset: 0,
                background: "rgba(0,0,0,0.35)",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                zIndex: 1100,
            }}
            onClick={onClose}
        >
            <div
                style={{
                    background: "#fff",
                    color: "#000",
                    padding: 12,
                    borderRadius: 8,
                    minWidth: 320,
                    maxWidth: 520,
                    maxHeight: "80vh",
                    display: "flex",
                    flexDirection: "column",
                    gap: 8,
                }}
                onClick={e => e.stopPropagation()}
            >
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                    <strong>{title}</strong>
                    <button type="button" onClick={onClose}>×</button>
                </div>
                <input
                    type="text"
                    placeholder="Search..."
                    value={search}
                    onChange={e => onSearch(e.target.value)}
                    style={{ padding: "6px 8px" }}
                />
                <div style={{ overflowY: "auto", flex: 1, border: "1px solid #ddd", borderRadius: 4 }}>
                    {items.length === 0 ? (
                        <div style={{ padding: 8, fontSize: 12 }}>Nothing found</div>
                    ) : (
                        items.map(item => (
                            <button
                                type="button"
                                key={item.key}
                                onClick={item.onPick}
                                style={{
                                    display: "block",
                                    width: "100%",
                                    textAlign: "left",
                                    padding: "6px 8px",
                                    borderBottom: "1px solid #eee",
                                    background: "transparent",
                                }}
                            >
                                {item.label}
                            </button>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
}
