package org.hdcola.carnet.Service;

import lombok.NoArgsConstructor;
import org.hdcola.carnet.DTO.UserRegisterDTO;
import org.hdcola.carnet.Entity.User;
import org.hdcola.carnet.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setPassword("password");
        userRegisterDTO.setPassword2("password");
        userRegisterDTO.setEmail("test@example.com");

        userService.register(userRegisterDTO);

        verify(userRepository).save(any(User.class));
    }

}
