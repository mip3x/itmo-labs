import type { PersonDTO, CmpOp, SortKey } from "./types";

export function countryFlag(country: string): string {
    const map: Record<string, string> = {
        RUSSIA: "🇷🇺",
        SPAIN: "🇪🇸",
        THAILAND: "🇹🇭",
    };
    return map[country] || "🏳️";
}

const cmpNum = (a: number | null | undefined, b: number | null | undefined) => {
    if (a == null && b == null) return 0;
    if (a == null) return 1;
    if (b == null) return -1;
    return a - b;
};

const applyDirection = (res: number, dir: "asc" | "desc") => (dir === "asc" ? res : -res);

export function comparePersons(
    a: PersonDTO,
    b: PersonDTO,
    sortKey: SortKey,
    sortDir: "asc" | "desc"
    ): number {
        if (sortKey === "coordXY") {
            const ax = a.coordinates?.x, ay = a.coordinates?.y;
            const bx = b.coordinates?.x, by = b.coordinates?.y;
            const r1 = cmpNum(ax, bx);

            if (r1 !== 0) return applyDirection(r1, sortDir);
            return applyDirection(cmpNum(ay, by), sortDir);
        }

        if (sortKey === "locXY") {
            const ax = a.location?.x, ay = a.location?.y;
            const bx = b.location?.x, by = b.location?.y;
            const r1 = cmpNum(ax, bx);

            if (r1 !== 0) return applyDirection(r1, sortDir);
            return applyDirection(cmpNum(ay, by), sortDir);
        }

        if (!sortKey) return 0;

        const A: any = (a as any)[sortKey];
        const B: any = (b as any)[sortKey];

        if (A == null && B == null) return 0;
        if (A == null) return applyDirection(1, sortDir);
        if (B == null) return applyDirection(-1, sortDir);

        if (sortKey === "height" || sortKey === "weight") {
            return applyDirection(Number(A) - Number(B), sortDir);
        }

        if (sortKey === "birthday") {
            const tA = new Date(A).getTime();
            const tB = new Date(B).getTime();
            return applyDirection(tA - tB, sortDir);
        }

        const sA = String(A).toLowerCase();
        const sB = String(B).toLowerCase();
        const r = sA.localeCompare(sB, undefined, { numeric: true });
        return applyDirection(r, sortDir);
}

function formatXY(x?: number | null, y?: number | null) {
    if (x == null || y == null) return "-";
    return `(${x}, ${y})`;
}

export function formatLocationCell(loc?: { name?: string; x?: number | null; y?: number | null }) {
    if (!loc) return "-";

    const name = loc.name ?? "-";
    const xy = (loc.x == null || loc.y == null) ? "" : ` ${formatXY(loc.x, loc.y)}`;

    return `${name}${xy}`;
}


export function formatDateISO(d: string): string {
    const t = new Date(d);
    return isNaN(t.getTime()) ? "-" : t.toLocaleDateString("ru-RU");
}

export function parseNumber(s: string): number | null {
    if (s == null) return null;
    const str = String(s).trim().replace(",", ".");

    if (str === "") return null;
    const n = Number(str);

    return Number.isFinite(n) ? n : null;
}

function parseDate(s: string): Date | null {
    const d = new Date(s);
    return isNaN(d.getTime()) ? null : d;
}

function cmp(a: number, op: CmpOp, b: number) {
    switch (op) {
        case ">":
            return a > b;
        case "<":
            return a < b;
        case ">=":
            return a >= b;
        case "<=":
            return a <= b;
        case "=":
            return a === b;
        default:
            return false;
    }
}

export function searchableString(p: PersonDTO): string {
    return [
        p.name,
        p.location?.name,
        p.eyeColor,
        p.hairColor,
        p.coordinates?.x, p.coordinates?.y,
        p.location?.x, p.location?.y,
        p.nationality,
        p.height ?? "",
        p.weight ?? "",
        new Date(p.birthday).toLocaleDateString("ru-RU"),
        new Date(p.birthday).getFullYear().toString(),
    ].join(" ").toLowerCase();
}

export function makeFieldPredicate(token: string): ((p: PersonDTO) => boolean) | null {
    const i = token.indexOf(":");
    if (i === -1)return null;

    const rawField = token.slice(0, i).trim().toLowerCase();
    const expr = token.slice(i + 1).trim();

    if (expr === "") return null;

    const field =
        rawField === "eye" ? "eyeColor" :
        rawField === "hair" ? "hairColor" :
        rawField === "nat" ? "nationality" :
        rawField === "loc" ? "location" :
        rawField === "w" ? "weight" :
        rawField === "h" ? "height" :
        rawField;

    const numericCoordField: null | ((p: PersonDTO) => number | null) =
        field === "coordx" || field === "cx" ? (p) => (p.coordinates?.x ?? null) :
        field === "coordy" || field === "cy" ? (p) => (p.coordinates?.y ?? null) :
        field === "locx"   || field === "lx" ? (p) => (p.location?.x ?? null) :
        field === "locy"   || field === "ly" ? (p) => (p.location?.y ?? null) :
        null;

    if (expr.includes("..")) {
        const [left, right] = expr.split("..").map(s => s.trim());

        if (field === "birthday") {
            const d1 = parseDate(left), d2 = parseDate(right);
            if (!d1 || !d2) return null;

            return (p) => {
                const bd = parseDate(p.birthday);
                return !!bd && bd >= d1 && bd <= d2;
            };
        }

        if (field === "height" || field === "weight") {
            const n1 = parseNumber(left), n2 = parseNumber(right);
            if (n1 == null || n2 == null) return null;

            return (p) => {
                const v = field === "height" ? (p.height ?? null) : p.weight;
                return v != null && v >= n1 && v <= n2;
            };
        }

        if (numericCoordField) {
            const n1 = parseNumber(left), n2 = parseNumber(right);
            if (n1 == null || n2 == null) return null;

            return (p) => {
                const v = numericCoordField(p);
                return v != null && v >= n1 && v <= n2;
            };
        }

        return null;
    }

    const m = expr.match(/^(>=|<=|>|<|=)\s*(.+)$/);
    if (m) {
        const op = m[1] as CmpOp;
        const val = m[2].trim();

        if (field === "birthday") {
            const d = parseDate(val);
            if (!d) return null;

            return (p) => {
                const bd = parseDate(p.birthday);
                return !!bd && cmp(bd.getTime(), op, d.getTime());
            };
        }

        if (field === "height" || field === "weight") {
            const n = parseNumber(val);
            if (n == null) return null;

            return (p) => {
                const v = field === "height" ? (p.height ?? null) : p.weight;
                return v != null && cmp(v, op, n);
            };
        }

        if (numericCoordField) {
            const n = parseNumber(val);
            if (n == null) return null;

            return (p) => {
                const v = numericCoordField(p);
                return v != null && cmp(v, op, n);
            };
        }
    }

     const asNum = parseNumber(expr);

    if (numericCoordField && asNum != null) {
        return (p) => {
            const v = numericCoordField(p);
            return v != null && v === asNum;
        };
    }

    if ((field === "height" || field === "weight") && asNum != null) {
        return (p) => {
            const v = field === "height" ? (p.height ?? null) : p.weight;
            return v != null && v === asNum;
        };
    }

    const asDate = parseDate(expr);
    if (field === "birthday" && asDate) {
        return (p) => {
        const bd = parseDate(p.birthday);
        return !!bd &&
            bd.getFullYear() === asDate.getFullYear() &&
            bd.getMonth() === asDate.getMonth() &&
            bd.getDate() === asDate.getDate();
        };
    }

    if (field === "birthday" && /^\d{4}$/.test(expr)) {
        const year = Number(expr);
        return (p) => new Date(p.birthday).getFullYear() === year;
    }

    if (field === "eyeColor" || field === "hairColor" || field === "nationality") {
        const needle = expr.toLowerCase();
        return (p) => String((p as any)[field]).toLowerCase().includes(needle);
    }

    if (field === "location") {
        const needle = expr.toLowerCase();
        return (p) => (p.location?.name ?? "").toLowerCase().includes(needle);
    }

    if (field === "name") {
        const needle = expr.toLowerCase();
        return (p) => p.name.toLowerCase().includes(needle);
    }

    return null;
}