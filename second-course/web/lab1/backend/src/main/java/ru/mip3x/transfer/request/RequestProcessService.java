package ru.mip3x.transfer.request;

import java.io.IOException;

public interface RequestProcessService {
    void processRequest(long executionStart) throws IOException;
}
