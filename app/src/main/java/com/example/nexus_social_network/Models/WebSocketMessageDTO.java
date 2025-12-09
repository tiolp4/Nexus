package com.example.nexus_social_network.Models;

import com.google.gson.annotations.SerializedName;

public class WebSocketMessageDTO {
    @SerializedName("type")
    public String type;

    @SerializedName("chatId")
    public Integer chatId;

    @SerializedName("content")
    public String content;

    @SerializedName("receiverId")
    public Integer receiverId;

    @SerializedName("data")
    public WebSocketDataDTO data;
}
