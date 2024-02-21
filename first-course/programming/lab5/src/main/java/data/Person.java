package data;

public class Person {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private float weight; //Значение поля должно быть больше 0
    private String passportID; //Значение этого поля должно быть уникальным, Длина строки должна быть не меньше 4, Строка не может быть пустой, Поле может быть null
    private Color eyeColor; //Поле может быть null
    private Location location; //Поле может быть null
}
