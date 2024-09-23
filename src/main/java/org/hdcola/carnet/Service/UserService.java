package org.hdcola.carnet.Service;

import org.hdcola.carnet.Configs.CustomUserDetails;
import org.hdcola.carnet.DTO.UserOauthChoiceRoleDTO;
import org.hdcola.carnet.DTO.UserRegisterDTO;
import org.hdcola.carnet.DTO.UserSettingsDTO;
import org.hdcola.carnet.Entity.Role;
import org.hdcola.carnet.Entity.User;
import org.hdcola.carnet.Repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private final S3Service s3Service;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, EmailService emailService, S3Service s3Service) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.s3Service = s3Service;
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
        newUser.setName(user.getName());
        userRepository.save(newUser);

        emailService.sendWelcomeEmail(user.getEmail(), user.getName(), user.getRole());
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
            user.setPassword("*");
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

        if(user.isFileSelected()){
            String keyName = "buyer" + dbUser.getId().toString();
            if(s3Service.uploadBuyerFile(dbUser.getId(), user.getFile())) {
                dbUser.setHasApplied(true);
            }
        }

        dbUser.setRole(user.getRole());
        userRepository.save(dbUser);

        emailService.sendWelcomeEmail(dbUser.getEmail(), dbUser.getName(), dbUser.getRole());
    }

    public UserSettingsDTO getUserSettingsDTO(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }
        UserSettingsDTO userSettingsDTO = new UserSettingsDTO();
        userSettingsDTO.setEmail(user.getEmail());
        userSettingsDTO.setName(user.getName());
        userSettingsDTO.setRole(user.getRole());
        userSettingsDTO.setVerified(user.isVerified());
        if(user.isHasApplied()){
            userSettingsDTO.setCredentialUrl(s3Service.getBuyerFileUrl(user.getId()));
        }
        return userSettingsDTO;
    }

    public void updateSettings(UserSettingsDTO user) {
        User dbUser = userRepository.findByEmail(user.getEmail());
        if(dbUser == null) {
            throw new IllegalArgumentException("User not found");
        }
        dbUser.setName(user.getName());
        dbUser.setRole(user.getRole());
        if(user.getPassword() != null && !user.getPassword().isBlank()) {
            String password = passwordEncoder.encode(user.getPassword());
            dbUser.setPassword(password);
        }
        if(user.isFileSelected()){
            String keyName = "buyer" + dbUser.getId().toString();
            if(s3Service.uploadBuyerFile(dbUser.getId(), user.getFile())) {
                dbUser.setHasApplied(true);
            }
        }
        userRepository.save(dbUser);
    }

    public Role getRole(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user.getRole();
    }
}
