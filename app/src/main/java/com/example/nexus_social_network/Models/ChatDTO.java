package com.example.nexus_social_network.Models;

import com.google.gson.annotations.SerializedName;

public class ChatDTO {
    @SerializedName("id")
    public int id;

    @SerializedName("userId")
    public int userId;

    @SerializedName("otherUserId")
    public int otherUserId;

    @SerializedName("user1Id")
    public int user1Id;

    @SerializedName("user2Id")
    public int user2Id;

    @SerializedName("user1Name")
    public String user1Name;

    @SerializedName("user2Name")
    public String user2Name;

    @SerializedName("user1Avatar")
    public String user1Avatar;

    @SerializedName("user2Avatar")
    public String user2Avatar;

    @SerializedName("lastMessage")
    public ChatMessageDTO lastMessage;

    @SerializedName("unreadCount")
    public int unreadCount;

    @SerializedName("createdAt")
    public String createdAt;
}