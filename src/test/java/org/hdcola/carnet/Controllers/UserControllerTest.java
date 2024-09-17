package org.hdcola.carnet.Controllers;

import org.hdcola.carnet.Configs.CustomDaoAuthenticationProvider;
import org.hdcola.carnet.Configs.WebSecurityConfig;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
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
    void testRegister_WhenUserAlreadyExists_ShouldReturnRegistrationPageWithError() throws Exception {
        when(userService.existsByEmail("abc@abc.com")).thenReturn(true);

        mockMvc.perform( post("/register")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .param("email", "abc@abc.com")
                    .param("role", "BUYER")
                    .param("password", "password")
                    .param("password2", "password"))
                .andExpect( status().isOk() )
                .andExpect( view().name("register"))
                .andExpect(model().attributeHasFieldErrorCode("user", "email", "email.exists"));
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
}
