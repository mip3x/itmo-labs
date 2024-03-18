package collection.data;

import collection.Invokable;
import exception.InvalidInputException;

public class Location implements Invokable {
    private Double x; //Поле не может быть null
    private double y;
    private Integer z; //Поле не может быть null
    private String name; //Поле не может быть null

    public Location() {}
    public void setX(Double x) throws InvalidInputException {
        if (x == null) throw new InvalidInputException("Поле 'координата X' не должно быть пустым!");
        this.x = x;
    }

    public void setY(Double y) throws InvalidInputException {
        if (y == null) throw new InvalidInputException("Поле 'координата Y' не должно быть пустым!");
        this.y = y;
    }

    public void setZ(Integer z) throws InvalidInputException {
        if (z == null) throw new InvalidInputException("Поле 'координата Z' не должно быть пустым!");
        this.z = z;
    }

    public void setName(String name) throws InvalidInputException {
        if (name == null) throw new InvalidInputException("Поле 'имя' не должно быть пустым!");
        this.name = name;
    }

    @Override
    public String toString() {
        return "X: " + x + "; Y: " + y + "; Z: " + z + "\nname: " + name;
    }
}
