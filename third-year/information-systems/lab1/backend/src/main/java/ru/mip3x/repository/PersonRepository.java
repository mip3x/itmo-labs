package ru.mip3x.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.mip3x.model.Person;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    @EntityGraph(attributePaths = {"coordinates", "location"})
    List<Person> findAll();

    @EntityGraph(attributePaths = {"coordinates", "location"})
    Person findPersonById(int id);
}
