package ru.mip3x.service.validation;

import ru.mip3x.dto.Request;

public interface ValidationService {
    Request parseRequestBody(String x, String y, String radius);

    void validateRequestBody(Request requestData);

    boolean checkHit(Request requestData);
}
