package ru.mip3x.exception;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import ru.mip3x.api.ErrorResponse;

// https://medium.com/@elouadinouhaila566/exception-handling-and-request-validation-in-spring-boot-9d00a4c579b2
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 400 Valid error during validation (@Valid in Controller)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBodyValidation(MethodArgumentNotValidException exception) {
        List<ErrorResponse.FieldError> details = exception.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorResponse.FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "Request validation failed", details);
    }

    // 400 Hibernate level validation error
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
        var details = exception.getConstraintViolations().stream()
                .map(cv -> new ErrorResponse.FieldError(
                        cv.getPropertyPath().toString(),
                        cv.getMessage()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "Entity validation failed", details);
    }

    // 400 Problems in JSON parsing
    // example: incorrect enum value or incorrect provided type
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBadJson(HttpMessageNotReadableException exception) {
        String message = "Malformed JSON";
        List<ErrorResponse.FieldError> details = new ArrayList<>();

        Throwable cause = exception.getCause();
        if (cause instanceof InvalidFormatException ife) {
            var path = ife.getPath();
            String field = path.isEmpty() ? null : path.get(path.size() - 1).getFieldName();

            if (ife.getTargetType().isEnum()) {
                var allowed = Arrays.stream(ife.getTargetType().getEnumConstants())
                                    .map(Object::toString).toList();

                message = "Invalid enum value";

                details.add(new ErrorResponse.FieldError(field, "Allowed values: " + allowed));
            } else {
                message = "Invalid value for field '" + field + "'";
                details.add(new ErrorResponse.FieldError(field, "Expected type: " + ife.getTargetType().getSimpleName()));
            }
        }

        return build(HttpStatus.BAD_REQUEST, "BAD_JSON", message, details.isEmpty() ? null : details);
    }

    // 400 Wrong data type in query
    // `person/2i` instead of `person/2`
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        var detail = new ErrorResponse.FieldError(
                exception.getName(),
                "Expected type: " + (exception.getRequiredType() != null ? exception.getRequiredType().getSimpleName() : "unknown"));
        return build(HttpStatus.BAD_REQUEST, "TYPE_MISMATCH", "Invalid parameter", List.of(detail));
    }

    // 404 Resource Not Found
    // example: Person with provided `id` doesn't exist
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException exception) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", exception.getMessage(), null);
    }

    // 500 Database / lower-layer technical errors
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccess(DataAccessException exception) {
        log.error("Database error", exception);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Unexpected error", null);
    }

    // 400 Wrong data in request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException exception) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", exception.getMessage(), null);
    }

    // 500 Unexpected Internal Server Errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
        log.error("Unexpected error", exception);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Unexpected error", null);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status,
                                                String code,
                                                String message,
                                                List<ErrorResponse.FieldError> details) {
        return ResponseEntity.status(status).body(
                new ErrorResponse(code, message, details, OffsetDateTime.now()));
    }
}
