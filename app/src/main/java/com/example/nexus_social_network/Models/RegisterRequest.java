package com.example.nexus_social_network.Models;


public class RegisterRequest {
    private String email;
    private String passwordHash;
    private String username;

    public RegisterRequest(String email, String passwordHash, String username) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.username = username;
    }

    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getUsername() { return username; }
}
