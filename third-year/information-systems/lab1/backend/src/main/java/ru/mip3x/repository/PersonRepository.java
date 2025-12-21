package ru.mip3x.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ru.mip3x.model.Color;
import ru.mip3x.model.Person;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    @EntityGraph(attributePaths = {"coordinates", "location"})
    Page<Person> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"coordinates", "location"})
    Person findPersonById(int id);

    @Query("select coalesce(sum(p.height), 0) from Person p")
    long sumHeight();

    long countByWeightLessThan(int weight);

    @EntityGraph(attributePaths = {"coordinates", "location"})
    List<Person> findByBirthdayBefore(ZonedDateTime date);

    @EntityGraph(attributePaths = {"coordinates", "location"})
    Page<Person> findByBirthdayBefore(ZonedDateTime date, Pageable pageable);

    long countByHairColor(Color hairColor);

    long countByEyeColor(Color eyeColor);
}
