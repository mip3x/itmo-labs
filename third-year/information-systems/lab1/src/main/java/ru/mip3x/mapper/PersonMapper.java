package ru.mip3x.mapper;

import ru.mip3x.dto.CoordinatesDTO;
import ru.mip3x.dto.LocationDTO;
import ru.mip3x.dto.PersonDTO;
import ru.mip3x.model.Person;

public class PersonMapper {
    public static PersonDTO toDTO(Person person) {
        if (person == null)
            return null;

        PersonDTO personDTO = new PersonDTO();
        personDTO.setId(person.getId());
        personDTO.setName(person.getName());

        if (person.getCoordinates() != null) {
            personDTO.setCoordinates(new CoordinatesDTO());
            personDTO.getCoordinates().setX(person.getCoordinates().getX());
            personDTO.getCoordinates().setY(person.getCoordinates().getY());
        }

        personDTO.setEyeColor(person.getEyeColor().name());
        personDTO.setHairColor(person.getHairColor().name());

        if (person.getLocation() != null) {
            personDTO.setLocation(new LocationDTO());
            personDTO.getLocation().setName(person.getLocation().getName());
            personDTO.getLocation().setX(person.getLocation().getX());
            personDTO.getLocation().setY(person.getLocation().getY());
        }

        personDTO.setHeight(person.getHeight());
        personDTO.setBirthday(person.getBirthday());
        personDTO.setWeight(person.getWeight());
        personDTO.setNationality(person.getNationality().name());

        return personDTO;
    }
}
