package collection.data;

import collection.Invokable;
import exception.InvalidInputException;

import java.util.ArrayList;
import java.util.List;

public class Person implements Invokable {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private long weight; //Значение поля должно быть больше 0
    private String passportID; //Значение этого поля должно быть уникальным, Длина строки должна быть не меньше 4, Строка не может быть пустой, Поле может быть null
    private static final List<String> passportIDs = new ArrayList<>();
    private Color eyeColor; //Поле может быть null
    private Location location; //Поле может быть null

    public void setName(String name) throws InvalidInputException {
        if (name == null || name.isBlank()) throw new InvalidInputException("Поле 'имя' не должно быть пустым!");
        this.name = name;
    }

    public void setWeight(Long weight) throws InvalidInputException {
        if (weight == null) throw new InvalidInputException("Поле 'вес' не должно быть пустым!");
        if (weight <= 0) throw new InvalidInputException("Значение поля 'вес' должно быть больше нуля!");
        this.weight = weight;
    }

    public void setPassportID(String passportID) throws InvalidInputException {
        if (passportID == null || passportID.isBlank()) throw new InvalidInputException("Поле 'номер пасспорта' не должно быть пустым!");
        if (passportID.length() < 4) throw new InvalidInputException("Длина поля 'номер паспорта' должна быть не меньше 4!");
        if (passportIDs.contains(passportID)) throw new InvalidInputException("Староста с таким 'номер паспорта' уже существует! Используйте другой!");
        this.passportID = passportID;
        passportIDs.add(passportID);
    }

    public void setEyeColor(Color color) {
        this.eyeColor = color;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    
    @Override
    public String toString() {
        return "Имя: " + name + "\nВес: " + weight + "\nНомер паспорта: " + passportID + "\nЦвет глаз: " + eyeColor + "\nМестонахождение: \n" + location.toString();
    }
}
