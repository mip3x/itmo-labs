package ru.mip3x.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import ru.mip3x.dto.imports.ImportOperationDto;
import ru.mip3x.dto.imports.ImportRequest;
import ru.mip3x.model.ImportOperation;
import ru.mip3x.model.ImportStatus;
import ru.mip3x.model.Person;
import ru.mip3x.storage.FileStorageService;

@Slf4j
@Service
public class ImportService {
    private final PersonService personService;
    private final ImportOperationService operationService;
    private final Validator validator;
    private final TransactionTemplate transactionTemplate;
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory())
            .registerModule(new JavaTimeModule());
    private final FileStorageService storage;
    private final String minioBucket;

    public ImportService(PersonService personService,
                         ImportOperationService operationService,
                         Validator validator,
                         PlatformTransactionManager transactionManager,
                         FileStorageService storage,
                         @Value("${minio.bucket}") String minioBucket) {
        this.personService = personService;
        this.operationService = operationService;
        this.validator = validator;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.storage = storage;
        this.minioBucket = minioBucket;
    }

    public ImportOperationDto importFromFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File for import must be provided");
        }

        ImportOperation operation = operationService.start();

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            operationService.markFailed(operation.getId(), "Unable to read uploaded file");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to read uploaded file", e);
        }

        String objectKey = buildObjectKey(operation.getId(), file.getOriginalFilename());

        try {
            storage.put(objectKey,
                    new ByteArrayInputStream(bytes),
                    bytes.length,
                    file.getContentType());

            operationService.attachFile(
                    operation.getId(),
                    minioBucket,
                    objectKey,
                    file.getOriginalFilename(),
                    file.getContentType(),
                    bytes.length
            );
        } catch (RuntimeException e) {
            operationService.markFailed(operation.getId(), "MinIO upload failed: " + e.getMessage());
            throw e;
        }

        ImportRequest request;
        try {
            request = yamlMapper.readValue(bytes, ImportRequest.class);
        } catch (IOException e) {
            operationService.markFailed(operation.getId(), "Unable to parse YAML file");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to parse YAML file", e);
        }

        validate(request);

        try {
            int created = transactionTemplate.execute(status -> {
                List<Person> persons = request.getPersons();
                for (Person person : persons) {
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

    private String buildObjectKey(Long operationId, String originalFilename) {
        String safe = (originalFilename == null || originalFilename.isBlank())
                ? "import.yaml"
                : originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");

        return "imports/" + operationId + "/" + UUID.randomUUID() + "_" + safe;
    }


    public Page<ImportOperationDto> getOperations(Pageable pageable) {
        return operationService.findAll(pageable)
                .map(this::toDto);
    }

    private ImportRequest parseYaml(MultipartFile file) {
        try {
            return yamlMapper.readValue(file.getInputStream(), ImportRequest.class);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to parse YAML file", e);
        }
    }

    private void validate(ImportRequest request) {
        Set<ConstraintViolation<ImportRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
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

    public ImportOperation getOperationOrThrow(Long id) {
        return operationService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Import operation not found"));
    }
}
