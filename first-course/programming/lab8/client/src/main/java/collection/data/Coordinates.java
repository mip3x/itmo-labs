package collection.data;

import exception.InvalidInputException;

import java.io.Serializable;

/**
 * Coordinates
 */
public class Coordinates implements Serializable {
    private long x; //Максимальное значение поля: 224
    private Double y; //Поле не может быть null

    public Coordinates() {}

    public void setX(Long x) {
        if (x == null) throw new InvalidInputException("error.sg.x.empty");
        if (x > 224) throw new InvalidInputException("error.sg.x.wrong_value");
        this.x = x;
    }

    public Long getX() {
        return x;
    }

    public void setY(Double y) {
        if (y == null) throw new InvalidInputException("error.sg.y.empty");
        this.y = y;
    }

    public Double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "X: " + x + "; Y: " + y;
    }
}
