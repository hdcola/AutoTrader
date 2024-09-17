package org.hdcola.carnet.Controllers;

import org.hdcola.carnet.Configs.CustomDaoAuthenticationProvider;
import org.hdcola.carnet.Configs.WebSecurityConfig;
import org.hdcola.carnet.DTO.UserOauthChoiceRoleDTO;
import org.hdcola.carnet.Entity.Role;
import org.hdcola.carnet.Entity.User;
import org.hdcola.carnet.Handler.OAuth2LoginSuccessHandler;
import org.hdcola.carnet.Repository.UserRepository;
import org.hdcola.carnet.Service.CustomUserDetailsService;
import org.hdcola.carnet.Service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;


@WebMvcTest
@Import({WebSecurityConfig.class, OAuth2LoginSuccessHandler.class, CustomDaoAuthenticationProvider.class})
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void testGetLogin_ShouldReturnLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testGetRegister_ShouldReturnRegistrationPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("roles"));
    }

    @Test
    void testGetOauthChoiceRole_shouldReturnOauthChoiceRolePage() throws Exception {
        // Create a mock OAuth2User
        OAuth2User oAuth2User = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_BUYER")),
                Map.of("email", "test@example.com"),
                "email"
        );

        // Create an Authentication object with the mock OAuth2User
        Authentication authentication = new UsernamePasswordAuthenticationToken(oAuth2User, null, oAuth2User.getAuthorities());

        // Perform the GET request with the authentication
        mockMvc.perform(get("/choice-role")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(view().name("oauthChoiceRole"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("roles"));
    }

    @Test
    void testPostOauthChoiceRole_ShouldUpdateRoleAndRedirect() throws Exception {
        // Create a mock OAuth2User
        OAuth2User oAuth2User = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                Map.of("email", "test@example.com"),
                "email"
        );

        // Create an Authentication object with the mock OAuth2User
        Authentication authentication = new UsernamePasswordAuthenticationToken(oAuth2User, null, oAuth2User.getAuthorities());

        // Mock the userService.updateRole method
        doNothing().when(userService).updateRole(any(UserOauthChoiceRoleDTO.class));

        // Perform the POST request with the authentication
        mockMvc.perform(post("/choice-role")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", "test@example.com")
                        .param("role", "BUYER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("message", "Registration successful. Please login."));
    }

    @Test
    void testRegister_WhenUserAlreadyExists_ShouldReturnRegistrationPageWithError() throws Exception {
        when(userService.existsByEmail("abc@abc.com")).thenReturn(true);

        mockMvc.perform( post("/register")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .param("email", "abc@abc.com")
                    .param("role", "BUYER")
                    .param("name","user")
                    .param("password", "password")
                    .param("password2", "password"))
                .andExpect( status().isOk() )
                .andExpect( view().name("register"))
                .andExpect(model().attributeHasFieldErrorCode("user", "email", "email.exists"));
    }

    @Test
    void testRegister_WhenNameIsEmpty_ShouldReturnRegistrationPageWithError() throws Exception {
        when(userService.existsByEmail("abc@abc.com")).thenReturn(false);

        mockMvc.perform(post("/register")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", "a@a.com")
                        .param("role", "BUYER")
                        .param("password", "password")
                        .param("password2", "password"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrorCode("user", "name", "NotBlank"));
    }

    @Test
    void testRegister_WhenRoleIsNotBuyerOrSeller_ShouldReturnRegistrationPageWithError() throws Exception {
        when(userService.existsByEmail("abc@abc.com")).thenReturn(false);

        mockMvc.perform(post("/register")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", "a@a.com")
                        .param("name", "user")
                        .param("role", "ADMIN")
                        .param("password", "password")
                        .param("password2", "password"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrorCode("user", "role", "role.invalid"));
    }

    @Test
    void testRegister_WhenPasswordsDoNotMatchAndEmailIsNotInvalid_ShouldReturnRegistrationPageWithError() throws Exception {
        when(userService.existsByEmail("abc@abc.com")).thenReturn(false);

        mockMvc.perform(post("/register")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", "abc")
                        .param("role", "ADMIN")
                        .param("password", "password")
                        .param("password2", "password2"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrorCode("user", "role", "role.invalid"))
                .andExpect(model().attributeHasFieldErrorCode("user", "email", "Email"))
                .andExpect(model().attributeHasFieldErrorCode("user", "password2", "password.mismatch"));
    }

    @Test
    void testRegister_Success() throws Exception {
        when(userService.existsByEmail("abc@abc.com")).thenReturn(false);

        mockMvc.perform(post("/register")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", "abc@abc.com")
                        .param("name", "user")
                        .param("role", "BUYER")
                        .param("password", "password")
                        .param("password2", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("message", "Registration successful. Please login."));
    }

    @Test
    void testLogin_Success() throws Exception {

        when(userRepository.findByEmail("a@a.com")).thenReturn(
                User.builder()
                        .email("a@a.com")
                        .password(passwordEncoder.encode("password"))
                        .role(Role.BUYER)
                        .build());


        mockMvc.perform(formLogin()
                .user("a@a.com")
                .password("password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withUsername("a@a.com"))
                .andExpect(authenticated().withRoles("BUYER"))
                .andExpect(redirectedUrl("/"));

    }

    @Test
    void testIsEmailExists_WhenEmailExists_ShouldReturnErrorMessage() throws Exception {
        when(userService.existsByEmail("abc@abc.com")).thenReturn(true);

        mockMvc.perform(post("/register/isEmailExists")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", "abc@abc.com"))
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("emailCheckMessage", "Email is already in use"))
                        .andExpect(model().attribute("emailCheckMessageClass", "text-danger"));
    }

    @Test
    void testIsEmailExists_WhenEmailDoesNotExist_ShouldReturnSuccessMessage() throws Exception {
        when(userService.existsByEmail("abc@abc.com")).thenReturn(false);

        mockMvc.perform(post("/register/isEmailExists")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", "abc@abc.com"))
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("emailCheckMessage", "Email is available"))
                        .andExpect(model().attribute("emailCheckMessageClass", "text-success"));
    }

    @Test
    void testLogin_WhenEmailIsNotValid_ShouldReturnLoginAndShowErrorMessage() throws Exception{

        when(userRepository.findByEmail("test@mail.com")).thenReturn(
                User.builder()
                        .email("test@mail.com")
                        .password(passwordEncoder.encode("password"))
                        .role(Role.BUYER)
                        .build());

        mockMvc.perform(formLogin()
                .user("a@a.com")
                .password("password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));

    }

    @Test
    void testLogin_WhenPasswordIsNotValid_ShouldReturnLoginAndShowErrorMessage() throws Exception{

        when(userRepository.findByEmail("test@mail.com")).thenReturn(
                User.builder()
                        .email("test@mail.com")
                        .password(passwordEncoder.encode("password"))
                        .role(Role.BUYER)
                        .build());

        mockMvc.perform(formLogin()
                .user("test@mail.com")
                .password("password2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }
}
