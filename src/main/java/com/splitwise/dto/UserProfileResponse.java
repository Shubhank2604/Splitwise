package com.splitwise.dto;

import com.splitwise.entity.User;

public record UserProfileResponse(Long id, String username, String email) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}
