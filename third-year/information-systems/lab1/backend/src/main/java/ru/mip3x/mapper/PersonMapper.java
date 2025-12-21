package ru.mip3x.mapper;

import ru.mip3x.dto.CoordinatesDTO;
import ru.mip3x.dto.LocationDTO;
import ru.mip3x.dto.PersonDto;
import ru.mip3x.model.Person;

public class PersonMapper {
    public static PersonDto toDto(Person person) {
        if (person == null)
            return null;

        PersonDto personDTO = new PersonDto();
        personDTO.setId(person.getId());
        personDTO.setName(person.getName());

        if (person.getCoordinates() != null) {
            personDTO.setCoordinates(new CoordinatesDTO());
            personDTO.getCoordinates().setX(person.getCoordinates().getX());
            personDTO.getCoordinates().setY(person.getCoordinates().getY());
        }

        personDTO.setCreationDate(person.getCreationDate());
        personDTO.setEyeColor(person.getEyeColor().name());
        personDTO.setHairColor(person.getHairColor().name());

        if (person.getLocation() != null) {
            personDTO.setLocation(new LocationDTO(
                    person.getLocation().getX(),
                    person.getLocation().getY(),
                    person.getLocation().getName()));
        }

        personDTO.setHeight(person.getHeight());
        personDTO.setBirthday(person.getBirthday());
        personDTO.setWeight(person.getWeight());
        personDTO.setNationality(person.getNationality().name());

        return personDTO;
    }
}
