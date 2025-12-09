package com.example.nexus_social_network.Models;

import com.google.gson.annotations.SerializedName;

public class MessageResponseWithChat {
    @SerializedName("message")
    public String message;

    @SerializedName("chatId")
    public Integer chatId;

    @SerializedName("messageId")
    public Integer messageId;
}