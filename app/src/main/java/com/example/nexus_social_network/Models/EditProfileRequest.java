package com.example.nexus_social_network.Models;
public class EditProfileRequest {
    private String username;
    private String description;

    public EditProfileRequest(String username, String description) {
        this.username = username;
        this.description = description;
    }
}
