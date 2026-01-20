package ru.mip3x.storage;

import java.io.InputStream;

public interface FileStorageService {
    String put(String objectKey, InputStream data, long size, String contentType);
    InputStream get(String objectKey);
    void delete(String objectKey);
}
