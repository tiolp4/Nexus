package com.example.nexus_social_network.Models;


import com.google.gson.annotations.SerializedName;

public class UserLikeDTO {

    @SerializedName("id")
    private int id;

    @SerializedName("username")
    private String username;

    @SerializedName("userAvatar")
    private String avatarUrl;

    // Конструктор
    public UserLikeDTO(int id, String username, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }

    // Геттеры
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    // Сеттеры при необходимости
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
