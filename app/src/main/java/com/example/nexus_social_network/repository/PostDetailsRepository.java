package com.example.nexus_social_network.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.nexus_social_network.Models.CommentCreatedResponse;
import com.example.nexus_social_network.Models.CommentDTO;
import com.example.nexus_social_network.Models.CreateCommentDTO;
import com.example.nexus_social_network.Models.PostDTO;
import com.example.nexus_social_network.network.ApiClient;
import com.example.nexus_social_network.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailsRepository {

    private final ApiService api;

    public PostDetailsRepository() {
        api = ApiClient.getApiService();
    }

    public LiveData<PostDTO> loadPost(String token, int postId) {
        MutableLiveData<PostDTO> data = new MutableLiveData<>();

        api.getPostDetails("Bearer " + token, postId).enqueue(new Callback<PostDTO>() {
            @Override
            public void onResponse(Call<PostDTO> call, Response<PostDTO> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<PostDTO> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<List<CommentDTO>> loadComments(int postId) {
        MutableLiveData<List<CommentDTO>> data = new MutableLiveData<>();

        api.getComments(postId).enqueue(new Callback<List<CommentDTO>>() {
            @Override
            public void onResponse(Call<List<CommentDTO>> call, Response<List<CommentDTO>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<CommentDTO>> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<Boolean> addComment(String token, int postId, String text) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        api.addComment("Bearer " + token, postId, new CreateCommentDTO(text))
                .enqueue(new Callback<CommentCreatedResponse>() {
                    @Override
                    public void onResponse(Call<CommentCreatedResponse> call, Response<CommentCreatedResponse> response) {
                        result.setValue(response.isSuccessful());
                    }

                    @Override
                    public void onFailure(Call<CommentCreatedResponse> call, Throwable t) {
                        result.setValue(false);
                    }
                });

        return result;
    }
}
