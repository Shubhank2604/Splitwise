package com.splitwise.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public class LoginRequest {

    @JsonProperty("username") // or "email" if you want to use email
    private String username;

    @JsonProperty("password")
    private String password;

    // Constructors
    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
