package ru.mip3x.validation;

import ru.mip3x.dto.RequestData;

public interface ValidationService {
    RequestData parseRequestBody(String requestBody);

    void validateRequestBody(RequestData requestData);

    boolean checkHit(RequestData request);
}
