package com.example.nexus_social_network.Models;

import com.google.gson.annotations.SerializedName;

public class CreateChatDTO {
    @SerializedName("userId")
    public int userId;

    public CreateChatDTO(int userId) {
        this.userId = userId;
    }
}