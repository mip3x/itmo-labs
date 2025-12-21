package ru.mip3x.mapper;

import java.util.Objects;

import ru.mip3x.dto.CoordinatesResponse;
import ru.mip3x.dto.LocationResponse;
import ru.mip3x.dto.PersonResponse;
import ru.mip3x.model.Person;

public class PersonMapper {
    public static PersonResponse toDTO(Person person) {
        Objects.requireNonNull(person, "person is required");
        Objects.requireNonNull(person.getCoordinates(), "coordinates is required");
        Objects.requireNonNull(person.getLocation(), "location is required");
        Objects.requireNonNull(person.getEyeColor(), "eyeColor is required");
        Objects.requireNonNull(person.getHairColor(), "hairColor is required");
        Objects.requireNonNull(person.getBirthday(), "birthday is required");
        Objects.requireNonNull(person.getWeight(), "weight is required");
        Objects.requireNonNull(person.getNationality(), "nationality is required");
        Objects.requireNonNull(person.getCreationDate(), "creationDate is required");

        CoordinatesResponse coordinatesDTO = new CoordinatesResponse(
                person.getCoordinates().getX(),
                person.getCoordinates().getY());

        LocationResponse locationDTO = new LocationResponse(
                person.getLocation().getX(),
                person.getLocation().getY(),
                person.getLocation().getName());

        return new PersonResponse(
                person.getId(),
                person.getName(),
                coordinatesDTO,
                person.getCreationDate(),
                person.getEyeColor().name(),
                person.getHairColor().name(),
                locationDTO,
                person.getHeight(),
                person.getBirthday(),
                person.getWeight(),
                person.getNationality().name());
    }
}
