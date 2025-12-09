package com.example.nexus_social_network.Models;

import com.google.gson.annotations.SerializedName;

public class SendMessageDTO {
    @SerializedName("content")
    public String content;

    @SerializedName("receiverId")
    public int receiverId;

    public SendMessageDTO(String content, int receiverId) {
        this.content = content;
        this.receiverId = receiverId;
    }
}