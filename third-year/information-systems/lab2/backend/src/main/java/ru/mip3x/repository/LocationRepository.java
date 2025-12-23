package ru.mip3x.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.mip3x.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
