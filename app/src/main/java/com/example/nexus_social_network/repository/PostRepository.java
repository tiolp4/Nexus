package com.example.nexus_social_network.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.nexus_social_network.responce.PostResponseDTO;
import com.example.nexus_social_network.network.ApiService;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;

public class PostRepository {

    private ApiService apiService;

    public PostRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public MutableLiveData<PostResponseDTO> createPost(String token, String contentText, File imageFile) {
        MutableLiveData<PostResponseDTO> liveData = new MutableLiveData<>();

        // RequestBody для текста
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"), contentText);

        // RequestBody и Part для файла (если есть)
        MultipartBody.Part imagePart = null;
        if (imageFile != null && imageFile.exists()) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
            imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), fileBody);
        }

        // Вызов Retrofit
        Call<PostResponseDTO> call = apiService.createPost(
                "Bearer " + token,
                contentBody,
                imagePart
        );
        call.enqueue(new Callback<PostResponseDTO>() {
            @Override
            public void onResponse(Call<PostResponseDTO> call, Response<PostResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    Log.e("PostRepository", "Failed to create post: " + response.message());
                    liveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<PostResponseDTO> call, Throwable t) {
                Log.e("PostRepository", "Error creating post", t);
                liveData.setValue(null);
            }
        });

        return liveData;
    }
}
