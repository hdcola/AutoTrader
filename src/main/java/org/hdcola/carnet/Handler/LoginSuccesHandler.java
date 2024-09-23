package org.hdcola.carnet.Handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.hdcola.carnet.Configs.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
public class LoginSuccesHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        log.debug("Login Success {} ", userDetails.getUsername());
        userDetails.getAuthorities().forEach(role -> log.debug("Role: {}", role.getAuthority()));
        // set user information in session
        HttpSession session = request.getSession();
        session.setAttribute("userEmail", userDetails.getUsername());
        session.setAttribute("userName", userDetails.getName());
    }
}
