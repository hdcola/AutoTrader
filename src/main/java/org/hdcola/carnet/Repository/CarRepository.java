package org.hdcola.carnet.Repository;

import org.hdcola.carnet.Entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("SELECT c FROM Car c WHERE c.VIN = :VIN")
    public Car findByVIN(String VIN);

    @Query("SELECT c FROM Car c WHERE c.user.email = :email")
    public List<Car> findByUser(String email);
}
