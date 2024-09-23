package org.hdcola.carnet.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hdcola.carnet.DTO.VinDecodeResponseDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class VinDecodeService {

    private final String API_URL = "https://vpic.nhtsa.dot.gov/api/vehicles/DecodeVin/{vin}?format=json";
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public VinDecodeService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://vpic.nhtsa.dot.gov").build();
    }

    public String decodeVin(String vin) {
        try {
            String response = webClient.get()
                    .uri(API_URL, vin)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println(response);
            JsonNode jsonResponse = objectMapper.readTree(response);

            return jsonResponse.toString();
        } catch (Exception e) {
            return "Error decoding VIN";
        }
    }
}
