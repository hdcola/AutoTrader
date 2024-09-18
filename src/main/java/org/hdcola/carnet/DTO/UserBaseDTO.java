package org.hdcola.carnet.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hdcola.carnet.Entity.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBaseDTO {
    private String email;

    private String name;

    private String password;

    private String password2;

    private Role role = Role.BUYER;
}
