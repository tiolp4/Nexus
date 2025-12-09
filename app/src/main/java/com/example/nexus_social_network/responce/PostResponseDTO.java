package com.example.nexus_social_network.responce;

public class PostResponseDTO {
    private String message;
    private int postId;

    public PostResponseDTO(String message, int postId) {
        this.message = message;
        this.postId = postId;
    }

    public String getMessage() {
        return message;
    }

    public int getPostId() {
        return postId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}
