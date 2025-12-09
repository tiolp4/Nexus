package com.example.nexus_social_network.responce;

import com.example.nexus_social_network.Models.ChatMessageDTO;
import com.example.nexus_social_network.Models.WebSocketDataDTO;
import com.google.gson.annotations.SerializedName;

public class WebSocketResponseDTO {
    @SerializedName("type")
    public String type;

    @SerializedName("success")
    public Boolean success;

    @SerializedName("message")
    public String message;

    @SerializedName("error")
    public String error;

    @SerializedName("chatMessage")
    public ChatMessageDTO chatMessage;

    @SerializedName("data")
    public WebSocketDataDTO data;
}