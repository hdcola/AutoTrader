package org.hdcola.carnet.Controllers;

import org.hdcola.carnet.Configs.WebSecurityConfig;
import org.hdcola.carnet.DTO.UserRegisterDTO;
import org.hdcola.carnet.Entity.User;
import org.hdcola.carnet.Repository.UserRepository;
import org.hdcola.carnet.Service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@Import(WebSecurityConfig.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testRegister_WhenUserAlreadyExists_ShouldReturnRegistrationPageWithError() throws Exception {
        when(userService.existsByEmail("abc@abc.com")).thenReturn(true);

        mockMvc.perform( post("/register")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .param("email", "abc@abc.com")
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
                        .param("password", "password")
                        .param("password2", "password2"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrorCode("user", "email", "Email"))
                .andExpect(model().attributeHasFieldErrorCode("user", "password2", "password.mismatch"));
    }

    @Test
    void testRegister_Success() throws Exception {
        when(userService.existsByEmail("abc@abc.com")).thenReturn(false);

        mockMvc.perform(post("/register")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", "abc@abc.com")
                        .param("password", "password")
                        .param("password2", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("message", "Registration successful. Please login."));
    }
}
