package org.hdcola.carnet.Controllers;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.hdcola.carnet.DTO.UserRegisterDTO;
import org.hdcola.carnet.Entity.Role;
import org.hdcola.carnet.Entity.User;
import org.hdcola.carnet.Service.UserService;
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
}


