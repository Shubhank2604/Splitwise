package com.splitwise.controller;

import com.splitwise.dto.UserBalanceResponse;
import com.splitwise.service.ExpenseService;
import com.splitwise.service.TransactionService;
import com.splitwise.service.UserBalanceService;
import com.splitwise.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserBalanceService userBalanceService;

    @Autowired
    private TransactionService transactionService;

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

    @GetMapping(value = "/{userId}/balance", produces = "application/json")
    public ResponseEntity<UserBalanceResponse> getUserBalance(@PathVariable Long userId) {
        return ResponseEntity.ok(userBalanceService.getUserBalance(userId));
    }

    @PostMapping("/{payerId}/settle-up/{receiverId}")
    public ResponseEntity<String> settleUp(
            @PathVariable Long payerId,
            @PathVariable Long receiverId,
            @RequestBody Map<String, Double> requestBody
    ) {
        Double amount = requestBody.get("amount");
        transactionService.settleUp(payerId, receiverId, amount);
        return ResponseEntity.ok("Settle up recorded successfully.");
    }
}
