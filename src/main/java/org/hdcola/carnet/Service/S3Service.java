package org.hdcola.carnet.Service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;

@Slf4j
@Service
public class S3Service implements AutoCloseable{

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    private final S3Client s3client;

    public S3Service(S3Client s3client) {
        this.s3client = s3client;
    }

    public void uploadFile(String keyName, File file){
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();
            s3client.putObject(putObjectRequest, RequestBody.fromFile(file));
            log.debug("File uploaded successfully to S3 with key: {}", keyName);
        } catch (S3Exception e) {
            log.error("S3Exception occurred while uploading file: {}", e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            log.error("Unexpected exception occurred while uploading file: {}", e.getMessage());
        }
    }

    @Override
    public void close() {
        if (s3client != null) {
            s3client.close();
        }
    }
}
