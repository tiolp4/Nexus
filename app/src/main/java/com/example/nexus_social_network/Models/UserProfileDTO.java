package com.example.nexus_social_network.Models;

import com.google.gson.annotations.SerializedName;

public class UserProfileDTO {
    public int id;
    public String username;

    @SerializedName("avatarUrl")
    public String avatarUrl;

    @SerializedName("description") // API возвращает "description", а не "bio"
    public String bio;

    // Геттеры для совместимости
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getBio() { return bio; }
    public String getDescription() { return bio; } // Алиас для удобства
}