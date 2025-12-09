package com.example.nexus_social_network.Models;

import com.google.gson.annotations.SerializedName;

public class UpdateMessageDTO {
    @SerializedName("content")
    public String content;

    public UpdateMessageDTO(String content) {
        this.content = content;
    }
}