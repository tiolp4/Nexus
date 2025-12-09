package com.example.nexus_social_network.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.nexus_social_network.Models.PostFeedItemDTO;
import com.example.nexus_social_network.Models.UserProfileDTO;
import com.example.nexus_social_network.network.ApiClient;
import com.example.nexus_social_network.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchRepository {

    private final ApiService api = ApiClient.getApiService();

    public LiveData<UserProfileDTO> searchUser(String username, String token) {
        MutableLiveData<UserProfileDTO> data = new MutableLiveData<>();

        api.getUserByUsername(username, "Bearer " + token)
                .enqueue(new Callback<UserProfileDTO>() {
                    @Override
                    public void onResponse(Call<UserProfileDTO> call, Response<UserProfileDTO> response) {
                        if (response.isSuccessful()) {
                            data.setValue(response.body());
                        } else {
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserProfileDTO> call, Throwable t) {
                        data.setValue(null);
                    }
                });

        return data;
    }

    public LiveData<List<PostFeedItemDTO>> searchPosts(String query, String token) {
        MutableLiveData<List<PostFeedItemDTO>> data = new MutableLiveData<>();

        api.searchPosts(query, "Bearer " + token)
                .enqueue(new Callback<List<PostFeedItemDTO>>() {
                    @Override
                    public void onResponse(Call<List<PostFeedItemDTO>> call, Response<List<PostFeedItemDTO>> response) {
                        if (response.isSuccessful()) {
                            data.setValue(response.body());
                        } else {
                            data.setValue(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PostFeedItemDTO>> call, Throwable t) {
                        data.setValue(new ArrayList<>());
                    }
                });

        return data;
    }
}
