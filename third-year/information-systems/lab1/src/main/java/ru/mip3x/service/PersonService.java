package ru.mip3x.service;

import java.util.List;

import ru.mip3x.model.Person;

public interface PersonService {
    List<Person> findAllPersons();
    Person savePerson(Person person);
    Person findById(int id);
    Person updatePerson(int id, Person person);
    void deletePerson(int id);
}
