package com.example.nexus_social_network.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nexus_social_network.responce.PostResponseDTO;
import com.example.nexus_social_network.repository.PostRepository;

import java.io.File;

public class PostViewModel extends ViewModel {

    private PostRepository repository;
    private MutableLiveData<PostResponseDTO> postResponse;

    public PostViewModel(PostRepository repository) {
        this.repository = repository;
        this.postResponse = new MutableLiveData<>();
    }

    public LiveData<PostResponseDTO> getPostResponse() {
        return postResponse;
    }

    public void createPost(String token, String contentText, File imageFile) {
        postResponse = repository.createPost(token, contentText, imageFile);
    }
}
