import { type FormEvent, useState } from "react";

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

    function validate(values: PersonFormValues): PersonFormErrors {
        const errs: PersonFormErrors = {};

        const MAX_INT = 2147483647;
        const MIN_INT = -2147483648;
        const MAX_LONG = 9223372036854775807;
        const MIN_LONG = -9223372036854775808;
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
        } else if (cx <= -860) {
            errs.coordX = "Coordinate X must be > -860";
        } else if (cx < MIN_LONG || cx > MAX_LONG) {
            errs.coordX = "Coordinate X exceeds number limits";
        }

        // y must be <= 396 and not null
        if (!cyValid) {
            errs.coordY = "Coordinate Y must be a number";
        } else if (!Number.isFinite(cy)) {
            errs.coordY = "Coordinate Y must be a valid number";
        } else if (cy > 396) {
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

    return (
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
                                type="number"
                                value={form.weight}
                                onChange={e => setField("weight", e.target.value)}
                                style={{ width: "100%", marginTop: 4 }}
                            />
                            {errors.weight && (
                                <div style={{ color: "red", fontSize: 12 }}>{errors.weight}</div>
                            )}
                        </label>

                        <label style={{ flex: 1, textAlign: "left" }}>
                            Height (cm, &gt; 0, could be empty):
                            <input
                                type="number"
                                value={form.height}
                                onChange={e => setField("height", e.target.value)}
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
                                    type="number"
                                    value={form.coordX}
                                    onChange={e => setField("coordX", e.target.value)}
                                    style={{ width: "100%", marginTop: 4 }}
                                />
                                {errors.coordX && (
                                    <div style={{ color: "red", fontSize: 12 }}>{errors.coordX}</div>
                                )}
                            </label>
                            <label style={{ flex: 1, textAlign: "left" }}>
                                y (≤ 396):
                                <input
                                    type="number"
                                    value={form.coordY}
                                    onChange={e => setField("coordY", e.target.value)}
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
                                <div
                                    style={{
                                        display: "flex",
                                        flexWrap: "wrap",
                                        gap: 4,
                                        marginTop: 4,
                                    }}
                                >
                                    {existingCoords.map(c => (
                                        <button
                                            type="button"
                                            key={`${c.x}|${c.y}`}
                                            onClick={() => useExistingCoords(c)}
                                            style={{ fontSize: 12, padding: "2px 6px" }}
                                        >
                                            ({c.x}, {c.y})
                                        </button>
                                    ))}
                                </div>
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
                                    type="number"
                                    value={form.locX}
                                    onChange={e => setField("locX", e.target.value)}
                                    style={{ width: "100%", marginTop: 4 }}
                                />
                                {errors.locX && (
                                    <div style={{ color: "red", fontSize: 12 }}>{errors.locX}</div>
                                )}
                            </label>
                            <label style={{ flex: 1, textAlign: "left" }}>
                                y:
                                <input
                                    type="number"
                                    value={form.locY}
                                    onChange={e => setField("locY", e.target.value)}
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
                                <div
                                    style={{
                                        display: "flex",
                                        flexDirection: "column",
                                        gap: 4,
                                        marginTop: 4,
                                        maxHeight: 120,
                                        overflowY: "auto",
                                    }}
                                >
                                    {existingLocations.map(l => (
                                        <button
                                            type="button"
                                            key={`${l.x}|${l.y}|${l.name}`}
                                            onClick={() => useExistingLocation(l)}
                                            style={{
                                                fontSize: 12,
                                                padding: "2px 6px",
                                                textAlign: "left",
                                            }}
                                        >
                                            {l.name} (x={l.x}, y={l.y})
                                        </button>
                                    ))}
                                </div>
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
    );
}
