package collection.data;

import exception.InvalidInputException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Person
 */
public class Person implements Serializable {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private long weight; //Значение поля должно быть больше 0
    private String passportID; //Значение этого поля должно быть уникальным, Длина строки должна быть не меньше 4, Строка не может быть пустой, Поле может быть null
    private static final List<String> passportIDs = new ArrayList<>();
    private Color eyeColor; //Поле может быть null
    private Location location; //Поле может быть null

    public void setName(String name) {
//        if (name == null || name.isBlank()) throw new InvalidInputException("Поле 'Имя' не должно быть пустым!");
        if (name == null || name.isBlank()) throw new InvalidInputException("Field 'Name' should not be empty!");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setWeight(Long weight) {
//        if (weight == null) throw new InvalidInputException("Поле 'Вес' не должно быть пустым!");
        if (weight == null) throw new InvalidInputException("Field 'Weight' should not be empty!");
//        if (weight <= 0) throw new InvalidInputException("Значение поля 'Вес' должно быть больше нуля!");
        if (weight <= 0) throw new InvalidInputException("Value of field 'Weight' should be more than 0!");
        this.weight = weight;
    }

    public Long getWeight() {
        return weight;
    }

    public void setPassportID(String passportID) {
//        if (passportID == null || passportID.isBlank()) throw new InvalidInputException("Поле 'Номер пасспорта' не должно быть пустым!");
        if (passportID == null || passportID.isBlank()) throw new InvalidInputException("Field 'Passport ID' should not be empty!");
//        if (passportID.length() < 4) throw new InvalidInputException("Длина значения поля 'Номер паспорта' должна быть не меньше 4!");
        if (passportID.length() < 4) throw new InvalidInputException("Length of value of field 'Passport ID' should not be less than 4!");
//        if (passportIDs.contains(passportID)) throw new InvalidInputException("Староста с таким значением поля 'Номер паспорта' уже существует! Используйте другое!");
        if (passportIDs.contains(passportID)) throw new InvalidInputException("Group admin with such value of field 'Passport ID' already existst! Use another one!");
        this.passportID = passportID;
        passportIDs.add(passportID);
    }

    public String getPassportID() {
        return passportID;
    }

    public void setEyeColor(Color color) {
        this.eyeColor = color;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        if (location == null) return "Name: " + name + "\nWeight: " + weight + "\nPassport ID: " + passportID + "\nEye color: " + eyeColor + "\nLocation: null";
//        return "Имя: " + name + "\nВес: " + weight + "\nНомер паспорта: " + passportID + "\nЦвет глаз: " + eyeColor + "\nМестонахождение: \n" + location.toString();
        return "Name: " + name + "\nWeight: " + weight + "\nPassport ID: " + passportID + "\nEye color: " + eyeColor + "\nLocation: \n" + location.toString();
    }
}
