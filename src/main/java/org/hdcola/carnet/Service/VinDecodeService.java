package org.hdcola.carnet.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hdcola.carnet.DTO.DecodedVinDTO;
import org.hdcola.carnet.Repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class VinDecodeService {

    private final String API_URL = "https://vpic.nhtsa.dot.gov/api/vehicles/DecodeVin/{vin}?format=json";
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CarRepository carRepo;

    public VinDecodeService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://vpic.nhtsa.dot.gov").build();
    }

    public DecodedVinDTO decodeVin(String vin) {
        try{
            String response = webClient.get()
                    .uri(API_URL, vin)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println(response);
            JsonNode jsonResponse = objectMapper.readTree(response);

            JsonNode array = jsonResponse.get("Results");
            String make = "";
            String model = "";
            String year = "";
            for (int i = 0; i < array.size(); i++) {
                JsonNode node = array.get(i);
                String var = node.get("Variable").asText();

                if (var.equals("Make")) {
                    make = node.get("Value").asText();
                } else if (var.equals("Model")) {
                    model = node.get("Value").asText();
                } else if (var.equals("Model Year")) {
                    year = node.get("Value").asText();
                }
            }
            return new DecodedVinDTO(make, model, Integer.parseInt(year));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
