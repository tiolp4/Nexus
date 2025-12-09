package com.example.nexus_social_network.responce;

public class UserResponse {
    public int id;
    public String username;
    public String description;
    public String avatarUrl;

    public UserResponse(int id, String username, String description, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.description = description;
        this.avatarUrl = avatarUrl;
    }
}
