package com.example.nexus_social_network.Models;

import com.google.gson.annotations.SerializedName;

public class MarkAsReadDTO {
    @SerializedName("messageIds")
    public int[] messageIds;

    public MarkAsReadDTO(int[] messageIds) {
        this.messageIds = messageIds;
    }
}