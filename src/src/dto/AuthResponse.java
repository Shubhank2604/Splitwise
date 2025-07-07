package com.splitwise.dto;

public class AuthResponse {
    private String token;
    private String message;

    public AuthResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }

    // Getters only (or add setters if needed)
    public String getToken() {
        return token;
    }
    public String getMessage() {
        return message;
    }
}
