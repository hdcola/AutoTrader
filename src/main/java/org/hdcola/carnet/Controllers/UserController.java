package org.hdcola.carnet.Controllers;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.hdcola.carnet.DTO.UserOauthChoiceRoleDTO;
import org.hdcola.carnet.DTO.UserRegisterDTO;
import org.hdcola.carnet.DTO.UserSettingsDTO;
import org.hdcola.carnet.Entity.Role;
import org.hdcola.carnet.Entity.User;
import org.hdcola.carnet.Service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }

    @GetMapping("/register")
    public String register(Model model) {
        UserRegisterDTO user = new UserRegisterDTO();
        model.addAttribute("user", user);
        model.addAttribute("roles", List.of(Role.BUYER, Role.SELLER));
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid UserRegisterDTO user, BindingResult result, Model model, RedirectAttributes rb) {
        if(!user.getPassword().equals(user.getPassword2())) {
            result.rejectValue("password2", "password.mismatch", "Passwords do not match");
        }

        if(!user.getRole().equals(Role.BUYER) && !user.getRole().equals(Role.SELLER)) {
            result.rejectValue("role", "role.invalid", "Role is invalid");
        }

        if(userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "email.exists", "Email is already in use");
        }

        if(result.hasErrors()) {
            log.debug("Validation errors found:{}", result);
            log.debug("User:{}", user);
            model.addAttribute("org.springframework.validation.BindingResult.user", result);
            model.addAttribute("user", user);
            return "register";
        }
        userService.register(user);
        rb.addFlashAttribute("message", "Registration successful. Please login.");
        return "redirect:/login";
    }

    @PostMapping("/register/isEmailExists")
    public HtmxResponse isEmailExists(String email, Model model) {
        if( userService.existsByEmail(email) ) {
            model.addAttribute("emailCheckMessage", "Email is already in use");
            model.addAttribute("emailCheckMessageClass", "text-danger");
        }else{
            model.addAttribute("emailCheckMessage", "Email is available");
            model.addAttribute("emailCheckMessageClass", "text-success");
        }
        return HtmxResponse.builder()
                .view("register :: email-check")
                .build();
    }

    @GetMapping("/choice-role")
    public String oauthChoiceRole(Model model, Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        UserOauthChoiceRoleDTO user = new UserOauthChoiceRoleDTO(email, Role.NONE);
        model.addAttribute("user", user);
        model.addAttribute("roles", List.of(Role.BUYER, Role.SELLER));
        return "oauthChoiceRole";
    }

    @PostMapping("/choice-role")
    public String oauthChoiceRole(@Valid UserOauthChoiceRoleDTO user, BindingResult result, Model model, RedirectAttributes rb, Authentication authentication) {
        if(!user.getRole().equals(Role.BUYER) && !user.getRole().equals(Role.SELLER)) {
            result.rejectValue("role", "role.invalid", "Role is invalid");
        }

        if(result.hasErrors()) {
            log.debug("Validation errors found:{}", result);
            log.debug("User:{}", user);
            model.addAttribute("org.springframework.validation.BindingResult.user", result);
            model.addAttribute("user", user);
            return "oauthChoiceRole";
        }

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        user.setEmail(email);

        userService.updateRole(user);
        rb.addFlashAttribute("message", "Registration successful. Please login.");
        return "redirect:/login";
    }

    @GetMapping("/settings")
    public String settings(Model model, Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        UserSettingsDTO user = userService.getUserSettingsDTO(email);
        model.addAttribute("user", user);
        model.addAttribute("roles", List.of(Role.BUYER, Role.SELLER));
        return "settings";
    }

    @PostMapping("/settings")
    public String settings(@Valid UserSettingsDTO user, BindingResult result, Model model, RedirectAttributes rb, Authentication authentication) {
        if(!user.getPassword().equals(user.getPassword2())) {
            result.rejectValue("password2", "password.mismatch", "Passwords do not match");
        }

        if(!user.getRole().equals(Role.BUYER) && !user.getRole().equals(Role.SELLER)) {
            result.rejectValue("role", "role.invalid", "Role is invalid");
        }

        if(!user.getPassword().isBlank() && (user.getPassword().length() < 6 || user.getPassword().length() > 32)) {
            result.rejectValue("password", "password.invalid", "Password must be between 6 and 32 characters");
        }

        if(result.hasErrors()) {
            log.debug("Validation errors found:{}", result);
            log.debug("User:{}", user);
            model.addAttribute("org.springframework.validation.BindingResult.user", result);
            model.addAttribute("user", user);
            return "settings";
        }

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        user.setEmail(email);

        userService.updateSettings(user);
        rb.addFlashAttribute("message", "Settings update successful.");
        return "redirect:/settings";
    }
}


