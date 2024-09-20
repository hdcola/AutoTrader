package org.hdcola.carnet.Service;

import org.hdcola.carnet.DTO.UserAdminListDTO;
import org.hdcola.carnet.Repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserAdminService {
    private final UserRepository userRepository;

    public UserAdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserAdminListDTO> getUsers(int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return userRepository.findAll(pageable).map(UserAdminListDTO::new);
    }

    public void deleteUser(Long id) {
        if(userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
    }
}
