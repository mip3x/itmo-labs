package collection.data;

public class Coordinates {
    private long x; //Максимальное значение поля: 224
    private Double y; //Поле не может быть null
    
    public void setX(long x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "\tx: " + x + "; y: " + y;
    }
}
