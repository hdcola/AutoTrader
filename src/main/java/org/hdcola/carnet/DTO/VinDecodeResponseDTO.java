package org.hdcola.carnet.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VinDecodeResponseDTO {

    private List<Result> Results;

    @Data
    public static class Result {
        private String Value;
        private String Variable;
    }
}
