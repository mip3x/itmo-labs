import { formatDateISO } from "../utils";
import type { ImportOperationDto } from "../types";

type Props = {
    items: ImportOperationDto[];
    loading: boolean;
    error: string | null;
    onRefresh: () => void;
    page: number;
    totalPages: number;
    onPageChange: (page: number) => void;
    onClose: () => void;
    getDownloadUrl: (id: number) => string;
};

export default function ImportHistoryModal(props: Props) {
    const { items, loading, error, onRefresh, onClose, page, totalPages, onPageChange, getDownloadUrl } = props;

    return (
        <div
            style={{
                position: "fixed",
                inset: 0,
                background: "rgba(0,0,0,0.55)",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                zIndex: 1300,
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
                    minWidth: 400,
                    maxWidth: 900,
                    width: "100%",
                    maxHeight: "90vh",
                    overflowY: "auto",
                    boxShadow: "0 10px 30px rgba(0,0,0,0.25)",
                }}
                onClick={e => e.stopPropagation()}
            >
                    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                        <h3 style={{ margin: 0 }}>Import history</h3>
                        <div style={{ display: "flex", gap: 8, alignItems: "center" }}>
                            <button type="button" onClick={onRefresh} disabled={loading}>
                                {loading ? "Refreshing..." : "Refresh"}
                        </button>
                        <button type="button" onClick={onClose}>×</button>
                    </div>
                </div>

                {error && <div style={{ color: "red", marginTop: 8 }}>{error}</div>}

                <div style={{ marginTop: 12 }}>
                    {loading ? (
                        <div>Loading...</div>
                    ) : (
                        <table
                            border={1}
                            cellPadding={6}
                            style={{ borderCollapse: "collapse", width: "100%", fontSize: 14 }}
                        >
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Status</th>
                                    <th>Added</th>
                                    <th>Error</th>
                                    <th>Created</th>
                                    <th>File</th>
                                </tr>
                            </thead>
                            <tbody>
                                {items.map(op => (
                                    <tr key={op.id}>
                                        <td>{op.id}</td>
                                        <td>{op.status}</td>
                                        <td>{op.status === "SUCCESS" ? op.addedCount ?? 0 : "—"}</td>
                                    <td>{op.errorMessage ?? "—"}</td>
                                        <td>{formatDateISO(op.createdAt)}</td>
                                        <td>
                                        <a href={getDownloadUrl(op.id)} target="_blank" rel="noreferrer">
                                            Download
                                        </a>
                                        </td>
                                    </tr>
                                ))}
                                {items.length === 0 && (
                                    <tr>
                                        <td colSpan={6} style={{ textAlign: "center" }}>No imports yet</td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    )}
                </div>

                <div style={{ display: "flex", gap: 8, justifyContent: "center", marginTop: 10 }}>
                    <button
                        type="button"
                        onClick={() => onPageChange(0)}
                        disabled={page <= 0 || loading}
                    >
                        «
                    </button>
                    <button
                        type="button"
                        onClick={() => onPageChange(Math.max(0, page - 1))}
                        disabled={page <= 0 || loading}
                    >
                        ‹
                    </button>
                    <span>Page {page + 1} / {Math.max(1, totalPages)}</span>
                    <button
                        type="button"
                        onClick={() => onPageChange(Math.min(totalPages - 1, page + 1))}
                        disabled={page >= totalPages - 1 || loading}
                    >
                        ›
                    </button>
                    <button
                        type="button"
                        onClick={() => onPageChange(totalPages - 1)}
                        disabled={page >= totalPages - 1 || loading}
                    >
                        »
                    </button>
                </div>
            </div>
        </div>
    );
}
