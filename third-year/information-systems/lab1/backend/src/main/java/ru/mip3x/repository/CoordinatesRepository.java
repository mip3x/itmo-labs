package ru.mip3x.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.mip3x.model.Coordinates;

public interface CoordinatesRepository extends JpaRepository<Coordinates, Long> {
}
