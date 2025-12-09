package com.example.nexus_social_network.responce;

public class UserProfileResponse {
    private String username;
    private int id;
    private String description;
    private String avatarUrl; // url или null

    public UserProfileResponse(int id, String username, String description, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.description = description;
        this.avatarUrl = avatarUrl;
    }
    public int getId() { return id; }

    public String getUsername() { return username; }
    public String getDescription() { return description; }
    public String getAvatarUrl() { return avatarUrl; }
}