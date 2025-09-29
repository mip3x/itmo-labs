package ru.mip3x.api;

import java.time.OffsetDateTime;
import java.util.List;

public record ErrorResponse(
    String code,
    String message,
    List<FieldError> details,
    OffsetDateTime timeStamp
) {
    public record FieldError(String field, String error) {}
}
