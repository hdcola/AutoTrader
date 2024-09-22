package org.hdcola.carnet.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hdcola.carnet.Entity.Role;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOauthChoiceRoleDTO {
    private String email;
    @NotNull(message = "Role is required")
    private Role role;
    private MultipartFile file;

    public boolean isFileSelected() {
        return file != null && !file.isEmpty();
    }
}
