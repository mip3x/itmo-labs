package collection.data;

import exception.InvalidInputException;

import javax.xml.bind.annotation.XmlElement;

public class Coordinates {
    private long x; //Максимальное значение поля: 224
    private Double y; //Поле не может быть null

    public Coordinates() {}

    public void setX(Long x) {
        if (x == null) throw new InvalidInputException("Поле 'координата X' не должно быть пустым!");
        if (x > 224) throw new InvalidInputException("Координата 'X' должна быть меньше либо равна 224!");
        this.x = x;
    }

    @XmlElement
    public Long getX() {
        return x;
    }

    public void setY(Double y) {
        if (y == null) throw new InvalidInputException("Поле 'координата Y' не должно быть пустым!");
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
