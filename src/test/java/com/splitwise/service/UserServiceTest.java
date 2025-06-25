package com.splitwise.service;

import com.splitwise.entity.User;
import com.splitwise.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        var userDetails = userService.loadUserByUsername("testuser");

        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("notfound")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("notfound"));
    }

    @Test
    void saveUser_CallsRepositorySave() {
        User user = new User();
        userService.saveUser(user);
        verify(userRepository, times(1)).save(user);
    }
}