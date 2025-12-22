package ru.mip3x.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    boolean existsByNameAndBirthday(String name, ZonedDateTime birthday);

    boolean existsByNameAndBirthdayAndIdNot(String name, ZonedDateTime birthday, Integer id);

    boolean existsByCoordinatesXAndCoordinatesY(double x, Float y);

    boolean existsByCoordinatesXAndCoordinatesYAndIdNot(double x, Float y, Integer id);

    long countByHairColor(Color hairColor);

    long countByEyeColor(Color eyeColor);

    @Query("""
            select count(p) from Person p
            where (:maxWeight is null or p.weight < :maxWeight)
              and (:hairColor is null or p.hairColor = :hairColor)
              and (:eyeColor is null or p.eyeColor = :eyeColor)
            """)
    long countFiltered(@Param("maxWeight") Integer maxWeight,
                       @Param("hairColor") Color hairColor,
                       @Param("eyeColor") Color eyeColor);
}
