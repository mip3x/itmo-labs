package ru.mip3x.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import ru.mip3x.dto.imports.ImportOperationDto;
import ru.mip3x.model.ImportOperation;

@ExtendWith(MockitoExtension.class)
class ImportServiceTest {

    @Mock
    private PersonService personService;

    @Mock
    private ImportOperationService operationService;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private ImportService underTest;

    @BeforeEach
    void setUp() {
        underTest = new ImportService(personService, operationService, validator, new NoOpTxManager());
    }

    @Test
    void importFromFile_whenValid_shouldPersistAllAndMarkSuccess() {
        // Given
        String yaml = """
                persons:
                  - name: Alice
                    coordinates:
                      x: 1.0
                      y: 10
                    location:
                      x: 2
                      y: 3
                      name: Home
                    height: 170
                    birthday: 2000-01-01T00:00:00Z
                    weight: 60
                    eyeColor: BLUE
                    hairColor: BLACK
                    nationality: RUSSIA
                  - name: Bob
                    coordinates:
                      x: 5.5
                      y: 20
                    location:
                      x: 7
                      y: 8
                      name: Office
                    height: 180
                    birthday: 1995-05-05T00:00:00Z
                    weight: 80
                    eyeColor: BLACK
                    hairColor: BLACK
                    nationality: SPAIN
                """;
        MockMultipartFile file = new MockMultipartFile("file", "people.yaml", "application/x-yaml", yaml.getBytes(StandardCharsets.UTF_8));

        ImportOperation op = new ImportOperation();
        op.setId(1L);
        op.setCreatedAt(LocalDateTime.now());
        when(operationService.start()).thenReturn(op);
        when(personService.savePerson(any())).thenAnswer(inv -> inv.getArgument(0));

        // When
        ImportOperationDto response = underTest.importFromFile(file);

        // Then
        verify(personService, org.mockito.Mockito.times(2)).savePerson(any());
        verify(operationService).markSuccess(op.getId(), 2);
        assertThat(response.getStatus().name()).isEqualTo("SUCCESS");
    }

    @Test
    void importFromFile_whenValidationFails_shouldNotStartOperation() {
        // Given
        String yaml = """
                persons:
                  - coordinates:
                      x: 1.0
                      y: 10
                    location:
                      x: 2
                      y: 3
                      name: Home
                    birthday: 2000-01-01T00:00:00Z
                    weight: 60
                    eyeColor: BLUE
                    hairColor: BLACK
                    nationality: RUSSIA
                """;
        MockMultipartFile file = new MockMultipartFile("file", "invalid.yaml", "application/x-yaml", yaml.getBytes(StandardCharsets.UTF_8));

        // When // Then
        assertThatThrownBy(() -> underTest.importFromFile(file))
                .isInstanceOf(Exception.class);

        verify(operationService, never()).start();
    }

    @Test
    void importFromFile_whenPersonServiceFails_shouldMarkFailed() {
        // Given
        String yaml = """
                persons:
                  - name: Alice
                    coordinates:
                      x: 1.0
                      y: 10
                    location:
                      x: 2
                      y: 3
                      name: Home
                    height: 170
                    birthday: 2000-01-01T00:00:00Z
                    weight: 60
                    eyeColor: BLUE
                    hairColor: BLACK
                    nationality: RUSSIA
                """;
        MockMultipartFile file = new MockMultipartFile("file", "people.yaml", "application/x-yaml", yaml.getBytes(StandardCharsets.UTF_8));

        ImportOperation op = new ImportOperation();
        op.setId(42L);
        op.setCreatedAt(LocalDateTime.now());
        when(operationService.start()).thenReturn(op);
        when(personService.savePerson(any())).thenThrow(new IllegalArgumentException("boom"));

        // When // Then
        assertThatThrownBy(() -> underTest.importFromFile(file))
                .isInstanceOf(IllegalArgumentException.class);

        verify(operationService).markFailed(eq(op.getId()), any());
    }

    private static class NoOpTxManager implements PlatformTransactionManager {
        @Override
        public TransactionStatus getTransaction(TransactionDefinition definition) {
            return new SimpleTransactionStatus();
        }

        @Override
        public void commit(TransactionStatus status) {
            // no-op
        }

        @Override
        public void rollback(TransactionStatus status) {
            // no-op
        }
    }
}
