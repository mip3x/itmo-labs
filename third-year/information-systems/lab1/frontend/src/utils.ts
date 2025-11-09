import type { PersonDTO } from "./types";

export function countryFlag(country: string): string {
    const map: Record<string, string> = {
        RUSSIA: "🇷🇺",
        SPAIN: "🇪🇸",
        THAILAND: "🇹🇭",
    };
    return map[country] || "🏳️";
}

export function formatDateISO(d: string): string {
    const t = new Date(d);
    return isNaN(t.getTime()) ? "-" : t.toLocaleDateString("ru-RU");
}

export function personToSearchText(p: PersonDTO): string {
    return [
        p.name,
        p.location?.name,
        p.eyeColor,
        p.hairColor,
        p.nationality,
        p.height ?? "",
        p.weight ?? "",
        new Date(p.birthday).toLocaleDateString("ru-RU"),
        new Date(p.birthday).getFullYear().toString(),
    ].join(" ").toLowerCase();
}
