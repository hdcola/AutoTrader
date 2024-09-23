package org.hdcola.carnet.Service;

import lombok.NoArgsConstructor;
import org.hdcola.carnet.DTO.UserOauthChoiceRoleDTO;
import org.hdcola.carnet.DTO.UserRegisterDTO;
import org.hdcola.carnet.DTO.UserSettingsDTO;
import org.hdcola.carnet.Entity.Role;
import org.hdcola.carnet.Entity.User;
import org.hdcola.carnet.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

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

    @Mock
    private S3Service s3Service;

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

    @Test
    void testGetUserSettingsDTO_UserExists() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setName("Test User");
        user.setRole(Role.BUYER);
        user.setVerified(true);
        user.setHasApplied(true);
        user.setId(1L);

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(s3Service.getBuyerFileUrl(user.getId())).thenReturn("https://s3.amazonaws.com/bucket/buyer1");

        UserSettingsDTO userSettingsDTO = userService.getUserSettingsDTO(email);

        assertThat(userSettingsDTO.getEmail()).isEqualTo(email);
        assertThat(userSettingsDTO.getName()).isEqualTo("Test User");
        assertThat(userSettingsDTO.getRole()).isEqualTo(Role.BUYER);
        assertThat(userSettingsDTO.isVerified()).isTrue();
        assertThat(userSettingsDTO.getCredentialUrl()).isEqualTo("https://s3.amazonaws.com/bucket/buyer1");
    }

    @Test
    void testGetUserSettingsDTO_UserDoesNotExist() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        assertThatThrownBy(() -> userService.getUserSettingsDTO(email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }

    @Test
    void testUpdateSettings_WithFile() {
        String email = "test@example.com";
        User dbUser = new User();
        dbUser.setEmail(email);
        dbUser.setRole(Role.BUYER);
        dbUser.setId(1L);

        UserSettingsDTO userSettingsDTO = new UserSettingsDTO();
        userSettingsDTO.setEmail(email);
        userSettingsDTO.setName("Updated Name");
        userSettingsDTO.setRole(Role.SELLER);
        userSettingsDTO.setPassword("newPassword");
        userSettingsDTO.setPassword2("newPassword");
        userSettingsDTO.setFile(mock(MultipartFile.class));

        when(userRepository.findByEmail(email)).thenReturn(dbUser);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");
        when(s3Service.uploadBuyerFile(dbUser.getId(), userSettingsDTO.getFile())).thenReturn(true);

        userService.updateSettings(userSettingsDTO);

        verify(userRepository, times(1)).save(argThat(user ->
                user.getEmail().equals(email) &&
                        user.getName().equals("Updated Name") &&
                        user.getRole().equals(Role.SELLER) &&
                        user.getPassword().equals("encodedPassword") &&
                        user.isHasApplied()
        ));
    }
}
