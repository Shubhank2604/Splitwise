package com.splitwise.controller;

import com.splitwise.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Example: Get user details (protected endpoint)
    @GetMapping("/{username}")
    public ResponseEntity<Object> getUser(@PathVariable String username, Authentication authentication) {
        // Get authenticated user's username
        String loggedInUsername = authentication.getName();

        // Check if the logged-in user is trying to access their own data
        if (!loggedInUsername.equals(username)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access any other user's data.");
        }

        UserDetails user = userService.loadUserByUsername(username);
        return ResponseEntity.ok(user);
    }
}
