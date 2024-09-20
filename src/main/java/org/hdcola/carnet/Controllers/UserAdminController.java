package org.hdcola.carnet.Controllers;

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
    public String getUsers(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<UserAdminListDTO> users = userAdminService.getUsers(page, size);
        model.addAttribute("users", users);
        return "admin/users";
    }

    @ResponseBody
    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
//        userAdminService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
