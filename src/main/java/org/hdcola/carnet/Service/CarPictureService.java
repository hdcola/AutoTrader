package org.hdcola.carnet.Service;

import org.hdcola.carnet.Entity.Car;
import org.hdcola.carnet.Entity.CarPicture;
import org.hdcola.carnet.Repository.CarPictureRepository;
import org.hdcola.carnet.Repository.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
public class CarPictureService {

    private final CarPictureRepository carPictureRepository;

    private final CarRepository carRepository;

    public CarPictureService(CarPictureRepository carPictureRepository, CarRepository carRepository) {
        this.carPictureRepository = carPictureRepository;
        this.carRepository = carRepository;
    }

    public void save(MultipartFile file, Long carId) {

        CarPicture carPicture = new CarPicture();

        Car car = carRepository.findById(carId).get();
        List<CarPicture> carPictures = carPictureRepository.findAllByCar(car);
        if (carPictures.size() >= 3) {
            throw new IllegalArgumentException("Car can have only 3 pictures");
        }
        Long order = carPictures.size() + 1L;

        carPicture.setPictureOrder(order);
        carPicture.setPictureUrl(file.getOriginalFilename());
        carPicture.setCar(car);

        carPictureRepository.save(carPicture);
    }
}
