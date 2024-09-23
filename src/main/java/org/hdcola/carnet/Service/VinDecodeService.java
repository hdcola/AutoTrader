package org.hdcola.carnet.Service;

import org.springframework.stereotype.Service;

@Service
public class VinDecodeService {

    public String decodeVin(String vin) {

        return vin + " decoded";
    }
}
