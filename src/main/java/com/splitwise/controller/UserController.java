package com.splitwise.controller;

import com.splitwise.dto.BalanceResponse;
import com.splitwise.dto.UserProfileResponse;
import com.splitwise.entity.User;
import com.splitwise.service.UserBalanceService;
import com.splitwise.service.UserService;
import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me")
public class UserController {
    private final UserService userService;
    private final UserBalanceService balanceService;

    public UserController(UserService userService, UserBalanceService balanceService) {
        this.userService = userService;
        this.balanceService = balanceService;
    }

    @GetMapping
    UserProfileResponse profile(Principal principal) {
        return UserProfileResponse.from(userService.requireByUsername(principal.getName()));
    }

    @GetMapping("/balance")
    BalanceResponse balance(Principal principal) {
        User user = userService.requireByUsername(principal.getName());
        return balanceService.getBalances(user.getId());
    }
}
