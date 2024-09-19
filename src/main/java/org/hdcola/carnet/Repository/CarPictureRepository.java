package org.hdcola.carnet.Repository;

import org.hdcola.carnet.Entity.Car;
import org.hdcola.carnet.Entity.CarPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarPictureRepository extends JpaRepository<CarPicture, Long> {

    public List<CarPicture> findAllByCar(Car car);
}
