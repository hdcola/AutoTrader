package org.hdcola.carnet.Service;

import org.hdcola.carnet.DTO.UserRegisterDTO;
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
        String password = passwordEncoder.encode(user.getPassword());
        newUser.setPassword(password);
        userRepository.save(newUser);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
