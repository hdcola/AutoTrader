package org.hdcola.carnet.Service;

import org.hdcola.carnet.DTO.UserOauthChoiceRoleDTO;
import org.hdcola.carnet.DTO.UserRegisterDTO;
import org.hdcola.carnet.Entity.Role;
import org.hdcola.carnet.Entity.User;
import org.hdcola.carnet.Repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(UserRegisterDTO user) {
        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setName(user.getName());
        String password = passwordEncoder.encode(user.getPassword());
        newUser.setPassword(password);
        newUser.setRole(user.getRole());
        userRepository.save(newUser);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void register(String email, String name, String provider, Role role) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setOauth_provider(provider);
            user.setRole(role);
            userRepository.save(user);
        }else{
            user.setOauth_provider(provider);
            userRepository.save(user);
        }
    }

    public void addProvider(String email, String provider) {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }
        user.setOauth_provider(provider);
        userRepository.save(user);
    }

    public void updateRole(UserOauthChoiceRoleDTO user) {
        User dbUser = userRepository.findByEmail(user.getEmail());
        if(dbUser == null) {
            throw new IllegalArgumentException("User not found");
        }
        dbUser.setRole(user.getRole());
        userRepository.save(dbUser);
    }
}
