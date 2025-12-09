package com.example.nexus_social_network.responce;

public class UserCardResponse {
    private String username;
    private String avatarUrl;
    private String description;
    private String onlineStatus;

    // Конструкторы
    public UserCardResponse() {}

    public UserCardResponse(String username, String avatarUrl,
                            String description, String onlineStatus) {
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.description = description;
        this.onlineStatus = onlineStatus;
    }

    // Геттеры и сеттеры
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOnlineStatus() { return onlineStatus; }
    public void setOnlineStatus(String onlineStatus) { this.onlineStatus = onlineStatus; }

    public boolean isOnline() {
        return "Online".equalsIgnoreCase(onlineStatus);
    }
}