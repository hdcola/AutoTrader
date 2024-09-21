package org.hdcola.carnet.Controllers;

import org.hdcola.carnet.Service.CarPictureService;
import org.hdcola.carnet.Service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
public class FileController {

    private final S3Service s3Service;

    private final CarPictureService carPictureService;

    public FileController(S3Service s3Service, CarPictureService carPictureService) {
        this.s3Service = s3Service;
        this.carPictureService = carPictureService;
    }

    @PostMapping("/Seller/{carId}/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long carId) {
        try {
            File convertedFile = convertMultiPartFileToFile(file);

            carPictureService.save(file, carId);
            s3Service.uploadFile(file.getOriginalFilename(), convertedFile);
            convertedFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("File uploaded successfully");
    }

    private File convertMultiPartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }
}
