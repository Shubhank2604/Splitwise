package com.splitwise.controller;

import com.splitwise.dto.AuthResponse;
import com.splitwise.dto.LoginRequest;
import com.splitwise.dto.RegisterRequest;
import com.splitwise.dto.UserProfileResponse;
import com.splitwise.security.JwtUtil;
import com.splitwise.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(
        UserService userService,
        AuthenticationManager authenticationManager,
        JwtUtil jwtUtil
    ) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    ResponseEntity<UserProfileResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @PostMapping("/login")
    AuthResponse login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        return new AuthResponse(jwtUtil.generateToken(request.username()), "Bearer", jwtUtil.getExpirationMs());
    }
}
