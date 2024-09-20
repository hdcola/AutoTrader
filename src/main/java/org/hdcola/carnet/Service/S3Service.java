package org.hdcola.carnet.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class S3Service {

    private AmazonS3 s3client;

    public void uploadFile(String keyName, File file) {
        try {
            String carnetpictures = "carnetpictures";
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    carnetpictures, keyName, file);
            s3client.putObject(putObjectRequest);
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        }
    }
}
