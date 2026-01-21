package ru.mip3x.storage;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;

@Service
public class MinioFileStorageService implements FileStorageService {

    private final MinioClient minio;
    private final String bucket;

    public MinioFileStorageService(MinioClient minio,
                                    @Value("${minio.bucket}") String bucket) {
        this.minio = minio;
        this.bucket = bucket;
    }

    @Override
    public String put(String objectKey, InputStream data, long size, String contentType) {
        try {
            minio.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .stream(data, size, -1)
                    .contentType(contentType != null ? contentType : "application/octet-stream")
                    .build()
            );

            return objectKey;

        } catch (Exception exception) {
                throw new RuntimeException("Failed to upload to MinIO: " + objectKey, exception);
        }
    }

    @Override
    public InputStream get(String objectKey) {
        try {
        return minio.getObject(
            GetObjectArgs.builder()
                .bucket(bucket)
                .object(objectKey)
                .build()
        );
        } catch (Exception e) {
        throw new RuntimeException("Failed to download from MinIO: " + objectKey, e);
        }
    }

    @Override
    public void delete(String objectKey) {
        try {
        minio.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(objectKey)
                .build()
        );
        } catch (Exception e) {
        throw new RuntimeException("Failed to delete from MinIO: " + objectKey, e);
        }
    }

    @Override
    public void copy(String sourceObjectKey, String destObjectKey) {
        try {
            minio.copyObject(
                CopyObjectArgs.builder()
                    .bucket(bucket)
                    .object(destObjectKey)
                    .source(
                        CopySource.builder()
                            .bucket(bucket)
                            .object(sourceObjectKey)
                            .build()
                    )
                    .build()
            );
        } catch (Exception exception) {
            throw new RuntimeException("MinIO copy failed: " + sourceObjectKey + " -> " + destObjectKey, exception);
        }
    }

}
