package com.example.nexus_social_network.viewModel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nexus_social_network.Models.PostDTO;
import com.example.nexus_social_network.repository.PostsRepository;

import java.util.List;

public class FeedViewModel extends ViewModel {

    private final PostsRepository repository;
    private final MutableLiveData<List<PostDTO>> postsLiveData = new MutableLiveData<>();

    public FeedViewModel() {
        repository = new PostsRepository();
    }

    public LiveData<List<PostDTO>> getPostsLiveData() {
        return postsLiveData;
    }

    public void loadPosts(String token) {
        repository.getPosts(token).observeForever(postsLiveData::setValue);
    }
}
