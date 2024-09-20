package org.hdcola.carnet.Controllers;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import org.hdcola.carnet.DTO.UserAdminListDTO;
import org.hdcola.carnet.Service.UserAdminService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserAdminController {
    private final UserAdminService userAdminService;

    public UserAdminController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @GetMapping("/admin/users")
    public String getUsers(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size) {
        Page<UserAdminListDTO> users = userAdminService.getUsers(page, size);
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        return "admin/users";
    }

    @HxRequest
    @GetMapping("/admin/users/{id}")
    public HtmxResponse getUser(@PathVariable Long id,Model model) {
        UserAdminListDTO user =  userAdminService.getUser(id);
        // TODO: check if user is null

        model.addAttribute("user", user);
        return HtmxResponse.builder()
                .view("admin/useritem :: useritem-edit")
                .build();
    }

    @HxRequest
    @GetMapping("/admin/users/show/{id}")
    public HtmxResponse getShowUser(@PathVariable Long id,Model model) {
        UserAdminListDTO user =  userAdminService.getUser(id);
        // TODO: check if user is null

        model.addAttribute("user", user);
        return HtmxResponse.builder()
                .view("admin/useritem :: useritem-show")
                .build();
    }
    @HxRequest
    @PutMapping("/admin/users/{id}")
    public HtmxResponse updateUser(@PathVariable Long id, UserAdminListDTO user, Model model) {
        userAdminService.updateUser(id, user);
        user =  userAdminService.getUser(id);
        model.addAttribute("user", user);
        return HtmxResponse.builder()
                .view("admin/useritem :: useritem-show")
                .build();
    }

    @ResponseBody
    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userAdminService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
