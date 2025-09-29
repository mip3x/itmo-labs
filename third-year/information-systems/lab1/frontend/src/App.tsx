import { useEffect, useState } from "react";
import "./App.css";

type Color = "BLACK" | "BLUE" | "YELLOW" | "ORANGE" | "WHITE";
type Country = "RUSSIA" | "SPAIN" | "THAILAND";

type Coordinates = {
    id?: number;
    x: number;
    y: number;
};

type Location = {
    id?: number;
    x: number;
    y: number;
    name: string;
};

type PersonDTO = {
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

const API_BASE = "http://localhost:8080/api/v1/persons";

function App() {
    const [persons, setPerson] = useState<PersonDTO[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    async function load() {
        setLoading(true);
        setError(null);

        try {
            const result = await fetch(`${API_BASE}`);
            if (!result.ok) throw new Error(result.statusText);
            setPerson(await result.json());
        } catch (exception: any) {
            setError(exception.message ?? "Error");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        load();
    }, []);

    return (
        <div style={{ padding: 16 }}>
            <h1>Persons</h1>
            <button onClick={load} disabled={loading}>
                Update
            </button>
            {error && <div style={{ color: "red" }}>{error}</div>}
            {loading ? (
                <div>Loading...</div>
            ) : (
                <table border={1} cellPadding={6} style={{ borderCollapse: "collapse", marginTop: 16 }}>
                <thead>
                    <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Eye Color</th>
                    <th>Hair Color</th>
                    <th>Location</th>
                    <th>Weight</th>
                    <th>Height</th>
                    <th>Birthday</th>
                    <th>Nationality</th>
                    </tr>
                </thead>
                <tbody>
                    {persons.map((p) => (
                    <tr key={p.id}>
                        <td>{p.id}</td>
                        <td>{p.name}</td>
                        <td>{p.eyeColor}</td>
                        <td>{p.hairColor}</td>
                        <td>{p.location?.name}</td>
                        <td>{p.weight}</td>
                        <td>{p.height ?? "-"}</td>
                        <td>{new Date(p.birthday).toLocaleDateString()}</td>
                        <td>{p.nationality}</td>
                    </tr>
                    ))}
                </tbody>
                </table>
            )}
        </div>
    );
}

export default App;
