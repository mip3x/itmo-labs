package collection.data;

import exception.InvalidInputException;

import javax.xml.bind.annotation.XmlElement;

/**
 * Coordinates
 */
public class Coordinates {
    private long x; //Максимальное значение поля: 224
    private Double y; //Поле не может быть null

    public Coordinates() {}

    public void setX(Long x) {
//        if (x == null) throw new InvalidInputException("Поле 'Координата X' не должно быть пустым!");
        if (x == null) throw new InvalidInputException("Field 'Coordinate X' should not be empty!");
//        if (x > 224) throw new InvalidInputException("Значение поля 'Координата X' должна быть меньше либо равна 224!");
        if (x > 224) throw new InvalidInputException("Value of field 'Coordinate X' should be less or equal 224!");
        this.x = x;
    }

    @XmlElement
    public Long getX() {
        return x;
    }

    public void setY(Double y) {
//        if (y == null) throw new InvalidInputException("Поле 'Координата Y' не должно быть пустым!");
        if (y == null) throw new InvalidInputException("Field 'Coordinate Y' should not be empty!");
        this.y = y;
    }

    @XmlElement
    public Double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "X: " + x + "; Y: " + y;
    }
}
