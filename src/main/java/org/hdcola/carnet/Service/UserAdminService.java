package org.hdcola.carnet.Service;

import org.hdcola.carnet.DTO.UserAdminListDTO;
import org.hdcola.carnet.Entity.User;
import org.hdcola.carnet.Repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserAdminService {
    private final UserRepository userRepository;
    private final S3Service s3Service;

    public UserAdminService(UserRepository userRepository, S3Service s3Service) {
        this.userRepository = userRepository;
        this.s3Service = s3Service;
    }

    public Page<UserAdminListDTO> getUsers(int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return userRepository.findAll(pageable).map(UserAdminListDTO::new);
    }

    public void deleteUser(Long id) {
        if(userRepository.existsById(id)) {
            userRepository.findById(id).ifPresent(u ->{
                if(u.isHasApplied()){
                    s3Service.deleteBuyerFile(id);
                }
            });
            userRepository.deleteById(id);
        }
    }

    public UserAdminListDTO getUser(Long id) {
        return userRepository.findById(id).map(UserAdminListDTO::new).orElse(null);
    }

    public void updateUser(Long id, UserAdminListDTO user) {
        userRepository.findById(id).ifPresent(u -> {
            u.setName(user.getName());
            u.setRole(user.getRole());
            userRepository.save(u);
        });
    }
}
