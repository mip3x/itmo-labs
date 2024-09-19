package ru.mip3x.transfer.response;

import ru.mip3x.dto.Response;

public interface SendResponseService {
    void sendResponse(Response response, long executionStart);
}
