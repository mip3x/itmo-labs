package ru.mip3x.transfer.request;

import ru.mip3x.dto.Response;

import java.io.IOException;

public interface RequestProcessService {
    Response processRequest() throws IOException;
}
