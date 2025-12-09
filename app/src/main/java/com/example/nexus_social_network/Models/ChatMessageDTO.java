package com.example.nexus_social_network.Models;

import com.google.gson.annotations.SerializedName;

public class ChatMessageDTO {
    @SerializedName("id")
    public int id;

    @SerializedName("chatId")
    public int chatId;

    @SerializedName("senderId")
    public int senderId;

    @SerializedName("receiverId")
    public int receiverId;

    @SerializedName("content")
    public String content;

    @SerializedName("isRead")
    public boolean isRead;

    @SerializedName("createdAt")
    public String createdAt;

    @SerializedName("updatedAt")
    public String updatedAt;
}