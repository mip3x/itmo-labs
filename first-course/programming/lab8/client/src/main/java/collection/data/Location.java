package collection.data;

import exception.InvalidInputException;

import java.io.Serializable;

/**
 * Location
 */
public class Location implements Serializable {
    private Double x; //Поле не может быть null
    private double y;
    private Integer z; //Поле не может быть null
    private String name; //Поле не может быть null

    public Location() {}
    public void setX(Double x) {
        if (x == null) throw new InvalidInputException("error.sg.ga.location.x");
        this.x = x;
    }

    public Double getX() {
        return x;
    }

    public void setY(Double y) {
        if (y == null) throw new InvalidInputException("error.sg.ga.location.y");
        this.y = y;
    }

    public Double getY() {
        return y;
    }

    public void setZ(Integer z) {
        if (z == null) throw new InvalidInputException("error.sg.ga.location.y");
        this.z = z;
    }

    public Integer getZ() {
        return z;
    }

    public void setName(String name) {
//        if (name == null) throw new InvalidInputException("Поле 'Имя' не должно быть пустым!");
        if (name == null) throw new InvalidInputException("Field 'Name' should not be empty!");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
//        return "X: " + x + "; Y: " + y + "; Z: " + z + "\nНазвание локации: " + name;
        return "X: " + x + "; Y: " + y + "; Z: " + z + "\nLocation name: " + name;
    }
}
