package org.hdcola.carnet.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hdcola.carnet.Entity.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOauthChoiceRoleDTO {
    private String email;
    private Role role;
}
