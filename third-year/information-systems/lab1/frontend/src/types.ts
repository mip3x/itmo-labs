export const COLOR_VALUES = ["BLACK", "BLUE", "YELLOW", "ORANGE", "WHITE"] as const;
export type Color = (typeof COLOR_VALUES)[number];

export const COUNTRY_VALUES = ["RUSSIA", "SPAIN", "THAILAND"] as const;
export type Country = (typeof COUNTRY_VALUES)[number];

export type Coordinates = {
    id?: number;
    x: number;
    y: number;
};

export type Location = {
    id?: number;
    x: number;
    y: number;
    name: string;
};

export type PersonDTO = {
    id: number;
    name: string;
    coordinates: Coordinates;
    creationDate: string;
    eyeColor: Color;
    hairColor: Color;
    location: Location;
    height?: number | null;
    birthday: string;
    weight: number;
    nationality: Country;
};

export type PersonFormValues = {
    name: string;
    eyeColor: Color;
    hairColor: Color;
    nationality: Country;

    weight: string;
    height: string;

    birthday: string;

    coordX: string;
    coordY: string;

    locX: string;
    locY: string;
    locName: string;
};

export type PersonFormErrors = Partial<Record<keyof PersonFormValues, string>>;

export type CmpOp = ">" | "<" | ">=" | "<=" | "=";

export type SortKey = keyof PersonDTO | "coordXY" | "locXY" | "";