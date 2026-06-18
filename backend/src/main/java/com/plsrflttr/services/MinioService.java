package com.plsrflttr.services;

import com.plsrflttr.configs.MinioProperties;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public String uploadSvg(
            MultipartFile file,
            String objectKey
    ) {

        try {

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(properties.getBucketName())
                            .object(objectKey)
                            .stream(
                                    file.getInputStream(),
                                    file.getSize(),
                                    -1
                            )
                            .contentType("image/svg+xml")
                            .build()
            );

            return objectKey;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to upload SVG",
                    e
            );
        }
    }

    public String getPresignedUrl(
            String objectKey
    ) {

        try {

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(properties.getBucketName())
                            .object(objectKey)
                            .method(Method.GET)
                            .expiry(60 * 60)
                            .build()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate URL",
                    e
            );
        }
    }

    public void delete(
            String objectKey
    ) {

        try {

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(properties.getBucketName())
                            .object(objectKey)
                            .build()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to delete SVG",
                    e
            );
        }
    }
}