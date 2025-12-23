import type { PersonDto, SortKey } from "../types";

export default function Th(props: {
        k: keyof PersonDto;
        title: string;
        sortKey: SortKey;
        sortDir: "asc" | "desc";
        onSort: (k: keyof PersonDto) => void;
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