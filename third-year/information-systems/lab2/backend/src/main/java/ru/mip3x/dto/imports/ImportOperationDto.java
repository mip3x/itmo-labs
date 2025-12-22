package ru.mip3x.dto.imports;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;
import ru.mip3x.model.ImportStatus;

@Value
@Builder
public class ImportOperationDto {
    Long id;
    ImportStatus status;
    Integer addedCount;
    String errorMessage;
    LocalDateTime createdAt;
}
