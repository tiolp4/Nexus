package com.example.nexus_social_network.Models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nexus_social_network.repository.SearchRepository;

import java.util.List;

public class SearchViewModel extends ViewModel {

    private final SearchRepository repository = new SearchRepository();

    private final MutableLiveData<UserProfileDTO> userResult = new MutableLiveData<>();
    private final MutableLiveData<List<PostFeedItemDTO>> postResults = new MutableLiveData<>();

    public LiveData<UserProfileDTO> getUserResult() { return userResult; }
    public LiveData<List<PostFeedItemDTO>> getPostResults() { return postResults; }

    public void search(String query, String token) {
        if (query.length() < 3) return;

        repository.searchUser(query, token).observeForever(userResult::setValue);
        repository.searchPosts(query, token).observeForever(postResults::setValue);
    }
}
