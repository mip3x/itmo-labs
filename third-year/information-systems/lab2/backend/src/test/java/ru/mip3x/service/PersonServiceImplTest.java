package ru.mip3x.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
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
import ru.mip3x.service.impl.BasicPersonService;

@ExtendWith(MockitoExtension.class)
class PersonServiceImplUnitTest {

    @Mock
    PersonRepository personRepository;
    @Mock
    CoordinatesRepository coordinatesRepository;
    @Mock
    LocationRepository locationRepository;

    @InjectMocks
    BasicPersonService service;

    private Person prototype;

    @BeforeEach
    void setUp() {
        Coordinates coordinates = new Coordinates();
        coordinates.setX(10.5);
        coordinates.setY(200f);

        Location location = new Location();
        location.setX(15.2f);
        location.setY(42);
        location.setName("SPb");

        prototype = new Person();
        prototype.setName("Alex");
        prototype.setCoordinates(coordinates);
        prototype.setLocation(location);
        prototype.setEyeColor(Color.BLUE);
        prototype.setHairColor(Color.BLACK);
        prototype.setBirthday(ZonedDateTime.parse("1973-11-29T21:33:09Z"));
        prototype.setWeight(70);
        prototype.setNationality(Country.RUSSIA);
        prototype.setHeight(180L);
    }

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

    @Test
    void savePerson_whenNameAndBirthdayNotUnique_shouldThrow() {
        Person person = copyPrototype();

        when(coordinatesRepository.save(any())).thenAnswer(inv -> {
            person.getCoordinates().setId(10L);
            return person.getCoordinates();
        });
        when(locationRepository.save(any())).thenAnswer(inv -> {
            person.getLocation().setId(20L);
            return person.getLocation();
        });
        when(personRepository.existsByNameAndBirthday(person.getName(), person.getBirthday()))
                .thenReturn(true);

        assertThatThrownBy(() -> service.savePerson(person))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name and birthday");
    }

    @Test
    void savePerson_whenCoordinatesNotUnique_shouldThrow() {
        Person person = copyPrototype();

        when(coordinatesRepository.save(any())).thenAnswer(inv -> {
            person.getCoordinates().setId(10L);
            return person.getCoordinates();
        });
        when(locationRepository.save(any())).thenAnswer(inv -> {
            person.getLocation().setId(20L);
            return person.getLocation();
        });
        when(personRepository.existsByNameAndBirthday(person.getName(), person.getBirthday()))
                .thenReturn(false);
        when(personRepository.existsByCoordinatesXAndCoordinatesY(person.getCoordinates().getX(),
                                                                  person.getCoordinates().getY()))
                .thenReturn(true);

        assertThatThrownBy(() -> service.savePerson(person))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Coordinates");
    }

    private Person copyPrototype() {
        Coordinates coordinates = new Coordinates();
        coordinates.setX(prototype.getCoordinates().getX());
        coordinates.setY(prototype.getCoordinates().getY());

        Location location = new Location();
        location.setX(prototype.getLocation().getX());
        location.setY(prototype.getLocation().getY());
        location.setName(prototype.getLocation().getName());

        Person copy = new Person();
        copy.setName(prototype.getName());
        copy.setCoordinates(coordinates);
        copy.setLocation(location);
        copy.setEyeColor(prototype.getEyeColor());
        copy.setHairColor(prototype.getHairColor());
        copy.setBirthday(prototype.getBirthday());
        copy.setWeight(prototype.getWeight());
        copy.setNationality(prototype.getNationality());
        copy.setHeight(prototype.getHeight());
        return copy;
    }
}
