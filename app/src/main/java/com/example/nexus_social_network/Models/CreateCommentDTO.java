package com.example.nexus_social_network.Models;

public class CreateCommentDTO {
    public String contentText;
    public Integer parentCommentId;

    public CreateCommentDTO(String contentText) {
        this.contentText = contentText;
        this.parentCommentId = null;
    }
}
