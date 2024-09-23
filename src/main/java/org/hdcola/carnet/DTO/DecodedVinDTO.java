package org.hdcola.carnet.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecodedVinDTO {

    private String make;
    private String model;
    private int year;
}
