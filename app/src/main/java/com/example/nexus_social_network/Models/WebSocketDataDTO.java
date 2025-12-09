package com.example.nexus_social_network.Models;

import com.google.gson.annotations.SerializedName;

public class WebSocketDataDTO {
    @SerializedName("messageIds")
    public int[] messageIds;

    @SerializedName("isTyping")
    public Boolean isTyping;

    @SerializedName("timestamp")
    public Long timestamp;
}