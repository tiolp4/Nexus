package com.example.nexus_social_network.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.nexus_social_network.Models.UserLikeDTO;
import com.example.nexus_social_network.network.ApiClient;
import com.example.nexus_social_network.network.ApiService;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostLikeRepository {

    private final ApiService api = ApiClient.getApiService();

    public LiveData<Boolean> likePost(String token, int postId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        api.likePost("Bearer " + token, postId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                result.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(false);
            }
        });

        return result;
    }

    public LiveData<Boolean> unlikePost(String token, int postId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        api.unlikePost("Bearer " + token, postId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                result.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(false);
            }
        });

        return result;
    }

    public LiveData<List<UserLikeDTO>> getLikes(int postId) {
        MutableLiveData<List<UserLikeDTO>> result = new MutableLiveData<>();

        api.getPostLikes(postId).enqueue(new Callback<List<UserLikeDTO>>() {
            @Override
            public void onResponse(Call<List<UserLikeDTO>> call, Response<List<UserLikeDTO>> response) {
                result.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<UserLikeDTO>> call, Throwable t) {
                result.setValue(Collections.emptyList());
            }
        });

        return result;
    }
}
