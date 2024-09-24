package org.hdcola.carnet.Controllers;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.hdcola.carnet.Configs.CustomUserDetails;
import org.hdcola.carnet.Service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(Authentication authentication, HttpSession session) {
        // if user is not authenticated
        if(authentication != null) {
            if(session.getAttribute("userEmail") == null) {
                String userEmail = getUserEmail(authentication);
                String userName  = userService.getUserName(userEmail);
                if(userEmail != null) {
                    session.setAttribute("userEmail", userEmail);
                    session.setAttribute("userName", userName);
                    log.debug("User {} authenticated {}", userEmail, userName);
                }
            }
        }

        return "index";
    }

    String getUserEmail(Authentication authentication) {
        if (authentication.getPrincipal() instanceof OAuth2User) {
            return ((OAuth2User) authentication.getPrincipal()).getAttribute("email");
        } else if (authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUsername();
        }
        return null;
    }
}
