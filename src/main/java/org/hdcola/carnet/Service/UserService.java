package org.hdcola.carnet.Service;

import org.hdcola.carnet.DTO.UserRegisterDTO;
import org.hdcola.carnet.Entity.User;
import org.hdcola.carnet.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class UserService {
    private UserRepository userRepository;

    public void register(UserRegisterDTO user, BindingResult result) {

        if(!user.getPassword().equals(user.getPassword2())) {
            result.rejectValue("password2", null, "Passwords do not match");
        }

        if(result.hasErrors()) {
            return;
        }

        User newUser = new User();
        newUser.setEmail(user.getUsername());
        newUser.setPassword(user.getPassword());
        userRepository.save(newUser);
    }
}
