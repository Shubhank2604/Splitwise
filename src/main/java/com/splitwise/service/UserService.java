package com.splitwise.service;

import com.splitwise.dto.RegisterRequest;
import com.splitwise.dto.UserProfileResponse;
import com.splitwise.entity.User;
import com.splitwise.exception.ConflictException;
import com.splitwise.exception.ResourceNotFoundException;
import com.splitwise.repository.UserRepository;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserProfileResponse register(RegisterRequest request) {
        String username = request.username().trim();
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Username is already in use");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email is already in use");
        }
        User user = userRepository.save(new User(username, email, passwordEncoder.encode(request.password())));
        return UserProfileResponse.from(user);
    }

    @Transactional(readOnly = true)
    public User requireByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public User requireById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<User> requireAllById(Iterable<Long> ids, int expectedCount) {
        List<User> users = userRepository.findAllById(ids);
        if (users.size() != expectedCount) {
            throw new ResourceNotFoundException("One or more users do not exist");
        }
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPasswordHash())
            .authorities("USER")
            .build();
    }
}
