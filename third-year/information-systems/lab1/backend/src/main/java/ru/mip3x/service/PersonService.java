package ru.mip3x.service;

import java.util.List;
import java.time.ZonedDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ru.mip3x.model.Color;
import ru.mip3x.model.Person;

public interface PersonService {
    Page<Person> findAllPersons(Pageable pageable);
    Person savePerson(Person person);
    Person findById(int id);
    Person updatePerson(int id, Person person);
    void deletePerson(int id);
    long sumHeight();
    long countWeightLessThan(int weight);
    long countAll();
    long countByHairColor(Color hairColor);
    long countByEyeColor(Color eyeColor);
    List<Person> findBirthdayBefore(ZonedDateTime date);
    Page<Person> findBirthdayBefore(ZonedDateTime date, Pageable pageable);
    double hairColorShare(Color hairColor);
    double eyeColorShare(Color eyeColor);
}
