package org.hdcola.carnet.Controllers;
import org.hdcola.carnet.Configs.WebSecurityConfig;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;



@WebMvcTest
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InMemoryUserDetailsManager userDetailsService;

    @Test
    public void testIndex_Success() throws Exception {
        // TODO: if Security is enabled, you need to mock the userDetailsService
        mockMvc.perform( get("/") )
                .andExpect(status().is4xxClientError());

    }

}
