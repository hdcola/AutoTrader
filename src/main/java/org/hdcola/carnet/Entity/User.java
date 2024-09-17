package org.hdcola.carnet.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true, length = 64)
    private String email;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max=64, message = "Name should be between 2 and 64 characters")
    @Column(nullable = false, length = 64)
    private String name;


    @Column( length = 256)
    private String password;

    @Column(length = 64)
    private String oauth_provider;

    private Role role;
}

