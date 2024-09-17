package org.hdcola.carnet.Handler;

import jakarta.servlet.ServletException;
import org.hdcola.carnet.Entity.Role;
import org.hdcola.carnet.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class OAuth2LoginSuccessHandlerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testOnAuthenticationSuccess_UserExists() throws IOException, ServletException {
        // Mock OAuth2User
        OAuth2User oAuth2User = new DefaultOAuth2User(
                List.of(() -> "ROLE_USER"),
                Map.of("email", "test@example.com", "name", "Test User"),
                "email"
        );

        // Mock Authentication
        OAuth2AuthenticationToken authentication = mock(OAuth2AuthenticationToken.class);
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(authentication.getAuthorizedClientRegistrationId()).thenReturn("google");

        // Mock UserService
        when(userService.existsByEmail(anyString())).thenReturn(true);

        // Mock HttpServletRequest and HttpServletResponse
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Call the method
        oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Verify interactions and assertions
        verify(userService, times(1)).addProvider("test@example.com", "google");
        verify(userService, never()).register(anyString(), anyString(), anyString(), any(Role.class));
        assertEquals("/", response.getRedirectedUrl());
    }

    @Test
    public void testOnAuthenticationSuccess_UserDoesNotExist() throws IOException, ServletException {
        // Mock OAuth2User
        OAuth2User oAuth2User = new DefaultOAuth2User(
                List.of(() -> "ROLE_USER"),
                Map.of("email", "test@example.com", "name", "Test User"),
                "email"
        );

        // Mock Authentication
        OAuth2AuthenticationToken authentication = mock(OAuth2AuthenticationToken.class);
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(authentication.getAuthorizedClientRegistrationId()).thenReturn("google");

        // Mock UserService
        when(userService.existsByEmail(anyString())).thenReturn(false);

        // Mock HttpServletRequest and HttpServletResponse
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Call the method
        oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Verify interactions and assertions
        verify(userService, never()).addProvider(anyString(), anyString());
        verify(userService, times(1)).register("test@example.com", "Test User", "google", Role.NONE);
        assertEquals("/choice-role", response.getRedirectedUrl());
    }
}