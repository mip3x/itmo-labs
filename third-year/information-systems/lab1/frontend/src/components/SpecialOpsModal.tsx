import { COLOR_VALUES, type Color, type PersonDTO } from "../types";

type LoadingOps = {
    height: boolean;
    weight: boolean;
    birthday: boolean;
    hair: boolean;
    eye: boolean;
};

type SpecialOpsModalProps = {
    onClose: () => void;
    error: string | null;
    loading: LoadingOps;

    heightSum: number | null;
    onHeight: () => void;

    weightLimit: string;
    setWeightLimit: (v: string) => void;
    weightLimitError: string | null;
    weightLessResult: number | null;
    onWeightLess: () => void;

    birthdayBefore: string;
    setBirthdayBefore: (v: string) => void;
    birthdayBeforeError: string | null;
    birthdayBeforeList: PersonDTO[];
    onBirthdayBefore: () => void;

    hairColor: Color;
    setHairColor: (c: Color) => void;
    hairShare: number | null;
    onHairShare: () => void;

    eyeColor: Color;
    setEyeColor: (c: Color) => void;
    eyeShare: number | null;
    onEyeShare: () => void;
};

export default function SpecialOpsModal(props: SpecialOpsModalProps) {
    const {
        onClose,
        error,
        loading,
        heightSum,
        onHeight,
        weightLimit,
        setWeightLimit,
        weightLimitError,
        weightLessResult,
        onWeightLess,
        birthdayBefore,
        setBirthdayBefore,
        birthdayBeforeError,
        birthdayBeforeList,
        onBirthdayBefore,
        hairColor,
        setHairColor,
        hairShare,
        onHairShare,
        eyeColor,
        setEyeColor,
        eyeShare,
        onEyeShare,
    } = props;

    return (
        <div
            style={{
                position: "fixed",
                inset: 0,
                background: "rgba(0,0,0,0.5)",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                zIndex: 1200,
                padding: 16,
            }}
            onClick={onClose}
        >
            <div
                style={{
                    background: "#fff",
                    color: "#000",
                    padding: 16,
                    borderRadius: 10,
                    minWidth: 320,
                    maxWidth: 900,
                    width: "100%",
                    maxHeight: "90vh",
                    overflowY: "auto",
                    boxShadow: "0 10px 30px rgba(0,0,0,0.25)",
                    display: "grid",
                    gap: 12,
                }}
                onClick={e => e.stopPropagation()}
            >
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                    <h3 style={{ margin: 0 }}>Special operations</h3>
                    <button type="button" onClick={onClose}>×</button>
                </div>

                {error && <div style={{ color: "red", fontSize: 14 }}>{error}</div>}

                <div
                    style={{
                        display: "grid",
                        gap: 12,
                        gridTemplateColumns: "repeat(auto-fit, minmax(260px, 1fr))",
                        alignItems: "stretch",
                    }}
                >
                    <div style={{ border: "1px solid #ddd", borderRadius: 6, padding: 10 }}>
                        <div style={{ fontWeight: 600 }}>Sum of height</div>
                        <button
                            type="button"
                            onClick={onHeight}
                            disabled={loading.height}
                            style={{ marginTop: 8, width: "100%" }}
                        >
                            {loading.height ? "Calculating..." : "Calculate"}
                        </button>
                        <div style={{ marginTop: 8, fontSize: 14, textAlign: "center" }}>
                            {heightSum == null ? "—" : heightSum}
                        </div>
                    </div>

                    <div style={{ border: "1px solid #ddd", borderRadius: 6, padding: 10 }}>
                    <div style={{ fontWeight: 600 }}>Weight &lt; value</div>
                    <input
                        type="text"
                        inputMode="numeric"
                        value={weightLimit}
                        onChange={e => setWeightLimit(e.target.value)}
                        style={{ width: "100%", marginTop: 8 }}
                    />
                    {weightLimitError && (
                        <div style={{ color: "red", fontSize: 12, marginTop: 4 }}>{weightLimitError}</div>
                    )}
                    <button
                        type="button"
                        onClick={onWeightLess}
                        disabled={loading.weight}
                        style={{ marginTop: 8, width: "100%" }}
                        >
                            {loading.weight ? "Calculating..." : "Calculate"}
                        </button>
                        <div style={{ marginTop: 6, fontSize: 14, textAlign: "center" }}>
                            {weightLessResult == null ? "—" : `Count: ${weightLessResult}`}
                        </div>
                    </div>

                    <div style={{ border: "1px solid #ddd", borderRadius: 6, padding: 10 }}>
                        <div style={{ fontWeight: 600 }}>Birthday before date</div>
                    <input
                        type="date"
                        value={birthdayBefore}
                        onChange={e => setBirthdayBefore(e.target.value)}
                        style={{ width: "100%", marginTop: 8 }}
                    />
                    {birthdayBeforeError && (
                        <div style={{ color: "red", fontSize: 12, marginTop: 4 }}>{birthdayBeforeError}</div>
                    )}
                    <button
                        type="button"
                        onClick={onBirthdayBefore}
                        disabled={loading.birthday}
                        style={{ marginTop: 8, width: "100%" }}
                    >
                        {loading.birthday ? "Searching..." : "Search"}
                    </button>
                    <div style={{ marginTop: 6, fontSize: 14, textAlign: "center" }}>
                        {birthdayBeforeList.length === 0
                            ? "No matches"
                            : `Found: ${birthdayBeforeList.length}`}
                    </div>
                    {birthdayBeforeList.length > 0 && (
                        <div style={{ marginTop: 6, maxHeight: 150, overflowY: "auto", fontSize: 12 }}>
                            {birthdayBeforeList.map(p => (
                                <div key={p.id}>
                                    #{p.id} — {p.name} ({p.birthday.slice(0, 10)})
                                </div>
                            ))}
                        </div>
                    )}
                </div>

                    <div style={{ border: "1px solid #ddd", borderRadius: 6, padding: 10 }}>
                        <div style={{ fontWeight: 600 }}>Hair color share</div>
                        <select
                            value={hairColor}
                            onChange={e => setHairColor(e.target.value as Color)}
                            style={{ width: "100%", marginTop: 8 }}
                        >
                            {COLOR_VALUES.map(c => <option key={c} value={c}>{c}</option>)}
                        </select>
                        <button
                            type="button"
                            onClick={onHairShare}
                            disabled={loading.hair}
                            style={{ marginTop: 8, width: "100%" }}
                        >
                            {loading.hair ? "Calculating..." : "Calculate"}
                        </button>
                        <div style={{ marginTop: 6, fontSize: 14, textAlign: "center" }}>
                            {hairShare == null ? "No data" : `${hairShare.toFixed(1)}%`}
                        </div>
                    </div>

                    <div style={{ border: "1px solid #ddd", borderRadius: 6, padding: 10 }}>
                        <div style={{ fontWeight: 600 }}>Eye color share</div>
                        <select
                            value={eyeColor}
                            onChange={e => setEyeColor(e.target.value as Color)}
                            style={{ width: "100%", marginTop: 8 }}
                        >
                            {COLOR_VALUES.map(c => <option key={c} value={c}>{c}</option>)}
                        </select>
                        <button
                            type="button"
                            onClick={onEyeShare}
                            disabled={loading.eye}
                            style={{ marginTop: 8, width: "100%" }}
                        >
                            {loading.eye ? "Calculating..." : "Calculate"}
                        </button>
                        <div style={{ marginTop: 6, fontSize: 14, textAlign: "center" }}>
                            {eyeShare == null ? "No data" : `${eyeShare.toFixed(1)}%`}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
