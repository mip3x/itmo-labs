export type Color = "BLACK" | "BLUE" | "YELLOW" | "ORANGE" | "WHITE";
export type Country = "RUSSIA" | "SPAIN" | "THAILAND";

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