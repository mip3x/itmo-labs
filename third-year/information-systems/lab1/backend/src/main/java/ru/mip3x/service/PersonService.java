package ru.mip3x.service;

import java.util.List;
import java.time.ZonedDateTime;

import ru.mip3x.model.Color;
import ru.mip3x.model.Person;

public interface PersonService {
    List<Person> findAllPersons();
    Person savePerson(Person person);
    Person findById(int id);
    Person updatePerson(int id, Person person);
    void deletePerson(int id);
    long sumHeight();
    long countWeightLessThan(int weight);
    List<Person> findBirthdayBefore(ZonedDateTime date);
    double hairColorShare(Color hairColor);
    double eyeColorShare(Color eyeColor);
}
