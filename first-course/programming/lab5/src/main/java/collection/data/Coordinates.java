package collection.data;

import exception.InvalidInputException;

public class Coordinates {
    private long x; //Максимальное значение поля: 224
    private Double y; //Поле не может быть null
    
    public boolean setX(long x) throws InvalidInputException {
        if (x > 224) throw new InvalidInputException("Координата 'x' должна быть меньше 225!");
        this.x = x;
        return true;
    }

    public boolean setY(Double y) throws InvalidInputException {
        if (y == null) throw new InvalidInputException("Координата 'Y' не должна быть пустой!");
        this.y = y;
        return true;
    }

    @Override
    public String toString() {
        return "\tx: " + x + "; y: " + y;
    }
}
