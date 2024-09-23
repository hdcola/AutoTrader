package org.hdcola.carnet.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSettingsDTO extends UserBaseDTO{
    @NotBlank(message = "Name is required")
    private String name;
    private boolean isVerified;
    private String credentialUrl;
    private MultipartFile file;

    public boolean isFileSelected() {
        return file != null && !file.isEmpty();
    }
}
