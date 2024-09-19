package org.hdcola.carnet.Service;

import lombok.NoArgsConstructor;
import org.hdcola.carnet.DTO.UserOauthChoiceRoleDTO;
import org.hdcola.carnet.DTO.UserRegisterDTO;
import org.hdcola.carnet.Entity.Role;
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

    @Mock
    private  EmailService emailService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUserRegisterDTO_Success() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setPassword("password");
        userRegisterDTO.setPassword2("password");
        userRegisterDTO.setEmail("test@example.com");
        userRegisterDTO.setRole(Role.BUYER);

        userService.register(userRegisterDTO);

        // check saved email and password
        verify(userRepository).save(argThat(user -> {
                    assertThat(user.getEmail()).isEqualTo("test@example.com");
                    assertThat(user.getPassword()).isEqualTo(passwordEncoder.encode("password"));
                    assertThat(user.getRole()).isEqualTo(Role.BUYER);
                    return true;
            })
        );
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUserRegisterDTO_WhenEmailExists() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setPassword("password");
        userRegisterDTO.setPassword2("password");
        userRegisterDTO.setRole(Role.BUYER);
        userRegisterDTO.setEmail("test@example.com");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(userRegisterDTO))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(userService.existsByEmail("test@example.com")).isTrue();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_UserDoesNotExist() {
        String email = "test@example.com";
        String name = "Test User";
        String provider = "google";
        Role role = Role.BUYER;

        when(userRepository.findByEmail(email)).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userService.register(email, name, provider, role);

        verify(userRepository, times(1)).save(argThat(user ->
                user.getEmail().equals(email) &&
                        user.getName().equals(name) &&
                        user.getOauth_provider().equals(provider) &&
                        user.getRole().equals(role)
        ));
    }

    @Test
    void testRegister_UserExists() {
        String email = "test@example.com";
        String name = "Test User";
        String provider = "google";
        Role role = Role.BUYER;

        User existingUser = new User();
        existingUser.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(existingUser);

        userService.register(email, name, provider, role);

        verify(userRepository, times(1)).save(argThat(user ->
                user.getEmail().equals(email) &&
                        user.getOauth_provider().equals(provider)
        ));
    }

    @Test
    void testAddProvider_UserExists() {
        String email = "test@example.com";
        String provider = "google";

        User existingUser = new User();
        existingUser.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(existingUser);

        userService.addProvider(email, provider);

        verify(userRepository, times(1)).save(argThat(user ->
                user.getEmail().equals(email) &&
                        user.getOauth_provider().equals(provider)
        ));
    }

    @Test
    void testAddProvider_UserDoesNotExist() {
        String email = "test@example.com";
        String provider = "google";

        when(userRepository.findByEmail(email)).thenReturn(null);

        assertThatThrownBy(() -> userService.addProvider(email, provider))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");

        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void testUpdateRole_UserExists() {
        String email = "test@example.com";
        Role newRole = Role.BUYER;

        User dbUser = new User();
        dbUser.setEmail(email);
        dbUser.setRole(Role.NONE);

        when(userRepository.findByEmail(email)).thenReturn(dbUser);

        UserOauthChoiceRoleDTO userDto = new UserOauthChoiceRoleDTO();
        userDto.setEmail(email);
        userDto.setRole(newRole);

        userService.updateRole(userDto);

        verify(userRepository, times(1)).save(argThat(user ->
                user.getEmail().equals(email) &&
                        user.getRole().equals(newRole)
        ));
    }

    @Test
    void testUpdateRole_UserDoesNotExist() {
        String email = "test@example.com";
        Role newRole = Role.BUYER;

        when(userRepository.findByEmail(email)).thenReturn(null);

        UserOauthChoiceRoleDTO userDto = new UserOauthChoiceRoleDTO();
        userDto.setEmail(email);
        userDto.setRole(newRole);

        assertThatThrownBy(() -> userService.updateRole(userDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");

        verify(userRepository, never()).save(any(User.class));
    }
}
