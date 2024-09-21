package org.hdcola.carnet.Controllers;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import org.hdcola.carnet.Configs.CustomDaoAuthenticationProviderConfig;
import org.hdcola.carnet.Configs.WebSecurityConfig;
import org.hdcola.carnet.DTO.UserAdminListDTO;
import org.hdcola.carnet.Handler.OAuth2LoginSuccessHandler;
import org.hdcola.carnet.Repository.UserRepository;
import org.hdcola.carnet.Service.UserAdminService;
import org.hdcola.carnet.Service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserAdminController.class)
@Import({WebSecurityConfig.class, OAuth2LoginSuccessHandler.class, CustomDaoAuthenticationProviderConfig.class})
public class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAdminService userAdminService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @Test
    public void testGetUsers() throws Exception {
        UserAdminListDTO user = new UserAdminListDTO();
        Mockito.when(userAdminService.getUsers(0, 3)).thenReturn(
                new org.springframework.data.domain.PageImpl<>(java.util.List.of(user))
        );

        mockMvc.perform(get("/admin/users")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    public void testGetUser() throws Exception {
        UserAdminListDTO user = new UserAdminListDTO();
        Mockito.when(userAdminService.getUser(1L)).thenReturn(user);

        mockMvc.perform(get("/admin/users/1")
                        .header("HX-Request", "true")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Save</button>")))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void testGetShowUser() throws Exception {
        UserAdminListDTO user = new UserAdminListDTO();
        Mockito.when(userAdminService.getUser(1L)).thenReturn(user);

        mockMvc.perform(get("/admin/users/show/1")
                        .header("HX-Request", "true")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(">Edit</button>")))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        UserAdminListDTO user = new UserAdminListDTO();
        Mockito.when(userAdminService.getUser(1L)).thenReturn(user);

        mockMvc.perform(put("/admin/users/1")
                        .header("HX-Request", "true")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "John Doe")
                        .param("role", "ADMIN")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(">Edit</button>")))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/admin/users/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }
}