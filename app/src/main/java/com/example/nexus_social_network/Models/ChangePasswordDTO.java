package com.example.nexus_social_network.Models;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordDTO {
    @SerializedName("currentPassword")
    private String currentPassword;

    @SerializedName("newPassword")
    private String newPassword;

    public ChangePasswordDTO(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}