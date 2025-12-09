package com.example.nexus_social_network.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.nexus_social_network.Models.PostDTO;
import com.example.nexus_social_network.network.ApiService;
import com.example.nexus_social_network.network.ApiClient;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsRepository {

    private final ApiService api;

    public PostsRepository() {
        api = ApiClient.getRetrofit().create(ApiService.class);
    }

    public LiveData<List<PostDTO>> getPosts(String token) {

        MutableLiveData<List<PostDTO>> data = new MutableLiveData<>();

        api.getPosts("Bearer " + token).enqueue(new Callback<List<PostDTO>>() {
            @Override
            public void onResponse(Call<List<PostDTO>> call, Response<List<PostDTO>> response) {
                if (response.isSuccessful())
                    data.setValue(response.body());
                else
                    data.setValue(new ArrayList<>());
            }

            @Override
            public void onFailure(Call<List<PostDTO>> call, Throwable t) {
                data.setValue(new ArrayList<>());
            }
        });

        return data;
    }
}
