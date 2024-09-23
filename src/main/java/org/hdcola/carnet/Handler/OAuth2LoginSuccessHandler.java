package org.hdcola.carnet.Handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.hdcola.carnet.DTO.UserRegisterDTO;
import org.hdcola.carnet.Entity.Role;
import org.hdcola.carnet.Service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final UserService userService;

    public OAuth2LoginSuccessHandler(UserService userService) {
        this.userService = userService;
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String provider = oauthToken.getAuthorizedClientRegistrationId();

        HttpSession session = request.getSession();
        session.setAttribute("userEmail", email);
        session.setAttribute("userName", name);

        if ( userService.existsByEmail(email) ) {
            userService.addProvider(email, provider);

            // get role from database and set it to security context
            Role role = userService.getRole(email);
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
            Authentication newAuth = new OAuth2AuthenticationToken(oAuth2User, authorities, provider);
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            response.sendRedirect("/");
        }else{
            userService.register(email, name, provider, Role.NONE);
            response.sendRedirect("/choice-role");
        }
    }
}
