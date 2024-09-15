package org.hdcola.carnet.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String username;
    @NotBlank(message = "Password is required")
    @Size(min = 6, max=32, message = "Password must be between 6 and 32 characters")
    private String password;
    private String password2;
}
