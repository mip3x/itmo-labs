package collection.data;

public class Location {
    private Double x; //Поле не может быть null
    private double y;
    private Integer z; //Поле не может быть null
    private String name; //Поле не может быть null
    
    @Override
    public String toString() {
        return "\tx: " + x + "; y: " + y + "; z: " + z + "\nname: " + name;
    }
}
