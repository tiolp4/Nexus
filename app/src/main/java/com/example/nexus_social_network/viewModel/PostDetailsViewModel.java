package com.example.nexus_social_network.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nexus_social_network.Models.CommentDTO;
import com.example.nexus_social_network.Models.PostDTO;
import com.example.nexus_social_network.repository.PostDetailsRepository;

import java.util.List;

public class PostDetailsViewModel extends ViewModel {

    private final PostDetailsRepository repo;

    private final MutableLiveData<PostDTO> post = new MutableLiveData<>();
    private final MutableLiveData<List<CommentDTO>> comments = new MutableLiveData<>();

    public PostDetailsViewModel() {
        repo = new PostDetailsRepository();
    }

    public LiveData<PostDTO> getPost() { return post; }
    public LiveData<List<CommentDTO>> getComments() { return comments; }

    public void loadPost(String token, int postId) {
        repo.loadPost(token, postId).observeForever(post::setValue);
        repo.loadComments(postId).observeForever(comments::setValue);
    }

    public void addComment(String token, int postId, String text) {
        repo.addComment(token, postId, text).observeForever(success -> {
            if (success) loadPost(token, postId);
        });
    }
}
