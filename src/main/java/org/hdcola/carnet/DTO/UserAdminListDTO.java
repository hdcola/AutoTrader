package org.hdcola.carnet.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hdcola.carnet.Entity.Role;
import org.hdcola.carnet.Entity.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAdminListDTO {
    private Long id;
    private String email;
    private String name;
    private Role role;
    private String oauth_provider;

    public UserAdminListDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.role = user.getRole();
        this.oauth_provider = user.getOauth_provider();
    }
}
