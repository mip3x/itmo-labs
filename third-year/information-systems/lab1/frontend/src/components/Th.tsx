import type { PersonDTO } from "../types";

export default function Th(props: {
        k: keyof PersonDTO;
        title: string;
        sortKey: keyof PersonDTO | "";
        sortDir: "asc" | "desc";
        onSort: (k: keyof PersonDTO) => void;
    }) {
        const { k, title, sortKey, sortDir, onSort } = props;
        const isActive = sortKey === k;

        return (
            <th
                onClick={() => onSort(k)}
                style={{ cursor: "pointer", userSelect: "none" }}
                title="Сортировать"
            >
                {title} {isActive ? (sortDir === "asc" ? "▲" : "▼") : ""}
            </th>
    );
}