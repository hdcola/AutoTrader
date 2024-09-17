package org.hdcola.carnet.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hdcola.carnet.Entity.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max=32, message = "Password must be between 6 and 32 characters")
    private String password;

    private String password2;

    private Role role = Role.BUYER;
}
