package com.splitwise.dto;

public record AuthResponse(String token, String tokenType, long expiresInMs) {
}
