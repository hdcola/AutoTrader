package org.hdcola.carnet.Controllers;
import org.hdcola.carnet.Configs.WebSecurityConfig;
import org.hdcola.carnet.Repository.UserRepository;
import org.hdcola.carnet.Service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;



@WebMvcTest
@Import(WebSecurityConfig.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testIndex_Success() throws Exception {
        mockMvc.perform( get("/") )
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
}
