package ru.mip3x.storage;

import java.io.InputStream;

public interface FileStorageService {
    void copy(String sourceObjectKey, String destObjectKey);
    String put(String objectKey, InputStream data, long size, String contentType);
    InputStream get(String objectKey);
    void delete(String objectKey);
}
