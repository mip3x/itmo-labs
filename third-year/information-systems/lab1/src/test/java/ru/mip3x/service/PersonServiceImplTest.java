package ru.mip3x.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.mip3x.exception.ResourceNotFoundException;
import ru.mip3x.model.Color;
import ru.mip3x.model.Coordinates;
import ru.mip3x.model.Country;
import ru.mip3x.model.Location;
import ru.mip3x.model.Person;
import ru.mip3x.repository.CoordinatesRepository;
import ru.mip3x.repository.LocationRepository;
import ru.mip3x.repository.PersonRepository;
import ru.mip3x.service.impl.PersonServiceImpl;

@ExtendWith(MockitoExtension.class)
class PersonServiceImplUnitTest {

    @Mock
    PersonRepository personRepository;
    @Mock
    CoordinatesRepository coordinatesRepository;
    @Mock
    LocationRepository locationRepository;

    @InjectMocks
    PersonServiceImpl service;

    @Test
    void deletePerson_whenNotExists_shouldThrow404() {
        int id = 999;

        when(personRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> service.deletePerson(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Person " + id + " not found");
    }

    @Test
    void savePerson_withoutCoordinates_shouldThrow() {
        Person person = new Person();
        person.setName("Ivan");
        person.setLocation(new Location());

        assertThatThrownBy(() -> service.savePerson(person))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Coordinates");
    }

    @Test
    void savePerson_withNewCoordinatesAndLocation_shouldSaveChildren() {
        // Arrange [valid data]
        Coordinates coordinates = new Coordinates();
        coordinates.setX(10.5);
        coordinates.setY(200f);

        Location location = new Location();
        location.setX(15.2f);
        location.setY(42);
        location.setName("SPb");

        Person person = new Person();
        person.setName("Alex");
        person.setCoordinates(coordinates);
        person.setLocation(location);
        person.setEyeColor(Color.BLUE);
        person.setHairColor(Color.BLACK);
        person.setBirthday(ZonedDateTime.parse("1973-11-29T21:33:09Z"));
        person.setWeight(70);
        person.setNationality(Country.RUSSIA);

        when(coordinatesRepository.save(any())).thenAnswer(inv -> {
            coordinates.setId(1L);
            return coordinates;
        });
        when(locationRepository.save(any())).thenAnswer(inv -> {
            location.setId(2L);
            return location;
        });
        when(personRepository.save(any())).thenAnswer(inv -> {
            person.setId(100);
            return person;
        });

        // Act
        Person saved = service.savePerson(person);

        // Assert
        assertThat(saved.getId()).isEqualTo(100);
        assertThat(saved.getCoordinates().getId()).isEqualTo(1L);
        assertThat(saved.getLocation().getId()).isEqualTo(2L);
    }
}
