package org.hdcola.carnet.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class VinDecodeService {

    private final String API_URL = "https://vpic.nhtsa.dot.gov/api/vehicles/DecodeVin/{vin}?format=json";
    private final WebClient webClient;

    public VinDecodeService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://vpic.nhtsa.dot.gov").build();
    }

    public String decodeVin(String vin) {
        String response = webClient.get()
                .uri(API_URL, vin)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return response;
    }
}
