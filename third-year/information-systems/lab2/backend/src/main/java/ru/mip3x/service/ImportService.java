package ru.mip3x.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import ru.mip3x.dto.imports.ImportOperationDto;
import ru.mip3x.dto.imports.ImportRequest;
import ru.mip3x.model.Person;
import ru.mip3x.model.ImportOperation;
import ru.mip3x.model.ImportStatus;

@Slf4j
@Service
public class ImportService {
    private final PersonService personService;
    private final ImportOperationService operationService;
    private final Validator validator;
    private final TransactionTemplate transactionTemplate;
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory()).findAndRegisterModules();

    public ImportService(PersonService personService,
                         ImportOperationService operationService,
                         Validator validator,
                         PlatformTransactionManager transactionManager) {
        this.personService = personService;
        this.operationService = operationService;
        this.validator = validator;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public ImportOperationDto importFromFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File for import must be provided");
        }

        ImportRequest request = parseYaml(file);
        validate(request);

        ImportOperation operation = operationService.start();
        try {
            int created = transactionTemplate.execute(status -> {
                List<Person> persons = request.getPersons();
                for (Person person : persons) {
                    sanitize(person);
                    personService.savePerson(person);
                }
                return persons.size();
            });

            operationService.markSuccess(operation.getId(), created);
            return toDto(operation.getId(), created, null, ImportStatus.SUCCESS, operation.getCreatedAt());
        } catch (RuntimeException e) {
            log.warn("Import operation {} failed", operation.getId(), e);
            operationService.markFailed(operation.getId(), e.getMessage());
            throw e;
        }
    }

    public Page<ImportOperationDto> getOperations(Pageable pageable) {
        return operationService.findAll(pageable)
                .map(this::toDto);
    }

    private ImportRequest parseYaml(MultipartFile file) {
        try {
            return yamlMapper.readValue(file.getInputStream(), ImportRequest.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to parse YAML file", e);
        }
    }

    private void validate(ImportRequest request) {
        Set<ConstraintViolation<ImportRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private void sanitize(Person person) {
        person.setId(null);
        if (person.getCoordinates() != null) {
            person.getCoordinates().setId(null);
        }
        if (person.getLocation() != null) {
            person.getLocation().setId(null);
        }
        person.setCreationDate(null);
    }

    private ImportOperationDto toDto(ImportOperation op) {
        return toDto(op.getId(), op.getAddedCount(), op.getErrorMessage(), op.getStatus(), op.getCreatedAt());
    }

    private ImportOperationDto toDto(Long id, Integer addedCount, String errorMessage,
                                     ImportStatus status, java.time.LocalDateTime createdAt) {
        return ImportOperationDto.builder()
                .id(id)
                .status(status)
                .addedCount(addedCount)
                .errorMessage(errorMessage)
                .createdAt(createdAt)
                .build();
    }
}
