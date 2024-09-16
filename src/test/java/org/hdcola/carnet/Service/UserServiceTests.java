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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

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

        // check saved email and password
        verify(userRepository).save(argThat(user -> {
                    assertThat(user.getEmail()).isEqualTo("test@example.com");
                    assertThat(user.getPassword()).isEqualTo(passwordEncoder.encode("password"));
                    return true;
            })
        );
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegister_WhenEmailExists() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setPassword("password");
        userRegisterDTO.setPassword2("password");
        userRegisterDTO.setEmail("test@example.com");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(userRegisterDTO))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(userService.existsByEmail("test@example.com")).isTrue();

        verify(userRepository, never()).save(any(User.class));
    }

}
