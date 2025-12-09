package com.example.nexus_social_network.Models;

public class PostCreateRequest {
    private String contentText;
    private String imageUrl;

    public PostCreateRequest(String contentText, String imageUrl) {
        this.contentText = contentText;
        this.imageUrl = imageUrl;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
