package ru.mip3x.validation;

import ru.mip3x.dto.ValidatedData;

public interface ValidationService {
    ValidatedData validateRequestBody(String requestBody);

    boolean checkHit(ValidatedData request);
}
