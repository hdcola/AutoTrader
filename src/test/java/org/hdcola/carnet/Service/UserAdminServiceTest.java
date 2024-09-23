package org.hdcola.carnet.Service;

import org.hdcola.carnet.DTO.UserAdminListDTO;
import org.hdcola.carnet.Entity.Role;
import org.hdcola.carnet.Entity.User;
import org.hdcola.carnet.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserAdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAdminService userAdminService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        User user = new User();
        Page<User> page = new PageImpl<>(Collections.singletonList(user));
        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<UserAdminListDTO> result = userAdminService.getUsers(0, 10, false);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testDeleteUser() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        userAdminService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testGetUser() {
        Long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserAdminListDTO result = userAdminService.getUser(userId);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testUpdateUser() {
        Long userId = 1L;
        User user = new User();
        user.setName("New Name");
        user.setRole(Role.BUYER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserAdminListDTO userDTO = new UserAdminListDTO(user);

        userAdminService.updateUser(userId, userDTO);

        verify(userRepository, times(1)).save(user);
    }
}