import type { SortKey } from "../types";

export default function Th(props: {
        k: SortKey;
        title: string;
        sortKey: SortKey;
        sortDir: "asc" | "desc";
        onSort: (k: SortKey) => void;
    }) {
        const { k, title, sortKey, sortDir, onSort } = props;
        const isActive = sortKey === k;

        return (
            <th
                onClick={() => onSort(k)}
                style={{ cursor: "pointer", userSelect: "none" }}
                title="Sort"
            >
                {title} {isActive ? (sortDir === "asc" ? "▲" : "▼") : ""}
            </th>
    );
}