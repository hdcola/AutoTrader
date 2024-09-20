package org.hdcola.carnet.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Data
@Table(name = "car_pictures")
public class CarPicture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pictureUrl;

    private Long pictureOrder;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;


}
