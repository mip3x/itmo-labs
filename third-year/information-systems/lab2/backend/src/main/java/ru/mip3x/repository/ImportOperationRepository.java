package ru.mip3x.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.mip3x.model.ImportOperation;

public interface ImportOperationRepository extends JpaRepository<ImportOperation, Long> {
}
