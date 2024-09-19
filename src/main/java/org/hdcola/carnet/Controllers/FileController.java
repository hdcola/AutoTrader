package org.hdcola.carnet.Controllers;

import com.google.api.client.util.Value;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.hdcola.carnet.Entity.CarPicture;
import org.hdcola.carnet.Service.CarPictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class FileController {

    private Storage storage;

    private CarPictureService carPictureService;

    @PostMapping("/addPicture/{carId}")
    public String post(@RequestParam("file") MultipartFile file, @PathVariable Long carId) throws IOException {
        BlobId blobId = BlobId.of("carnetpictures", "test.txt");
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        carPictureService.save(file, carId);

        byte[] content = file.getBytes();
        storage.create(blobInfo, content);
        return "File posted to gcp";
    }

    @GetMapping("/read")
    public String read() {
        return "Reading file";
    }
}
