package org.hdcola.carnet.Service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

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

    public boolean uploadBuyerFile(Long id, MultipartFile file){
        return uploadFile("buyer" + id.toString(), file);
    }

    public void deleteBuyerFile(Long id){
        deleteFile("buyer" + id.toString());
    }

    public String getBuyerFileUrl(Long id){
        return getFileUrl("buyer" + id.toString());
    }

    public String getFileUrl(String keyName){
        return "https://" + bucketName + ".s3.amazonaws.com/" + keyName;
    }

    public boolean uploadFile(String keyName, MultipartFile file){
        try {
            // Create a temp file
            Path tempFile = Files.createTempFile(null, null);
            // Write the file to the temp file
            file.transferTo(tempFile.toFile());
            // Upload the temp file to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();
            s3client.putObject(putObjectRequest, RequestBody.fromFile(tempFile));
            Files.delete(tempFile);
            log.debug("File uploaded successfully to S3 with key: {}", keyName);
            return true;
        } catch (S3Exception e) {
            log.error("S3Exception occurred while uploading file: {}", e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            log.error("Unexpected exception occurred while uploading file: {}", e.getMessage());
        }
        return false;
    }

    public void deleteFile(String keyName){
        try {
            s3client.deleteObject(builder -> builder.bucket(bucketName).key(keyName));
            log.debug("File deleted successfully from S3 with key: {}", keyName);
        } catch (S3Exception e) {
            log.error("S3Exception occurred while deleting file: {}", e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            log.error("Unexpected exception occurred while deleting file: {}", e.getMessage());
        }
    }

    @Override
    public void close() {
        if (s3client != null) {
            s3client.close();
        }
    }
}
