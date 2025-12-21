package ru.mip3x.mapper;

import ru.mip3x.dto.CoordinatesDTO;
import ru.mip3x.dto.LocationDTO;
import ru.mip3x.dto.PersonDTO;
import ru.mip3x.model.Person;

public class PersonMapper {
    public static PersonDTO toDto(Person person) {
        if (person == null)
            return null;

        CoordinatesDTO coordinatesDTO = null;
        if (person.getCoordinates() != null) {
            coordinatesDTO = new CoordinatesDTO(person.getCoordinates().getX(),
                                                person.getCoordinates().getY());
        }

        LocationDTO locationDTO = null;
        if (person.getLocation() != null) {
            locationDTO = new LocationDTO(
                    person.getLocation().getX(),
                    person.getLocation().getY(),
                    person.getLocation().getName());
        }

        return new PersonDTO(
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
