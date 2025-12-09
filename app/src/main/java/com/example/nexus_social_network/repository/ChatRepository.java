package com.example.nexus_social_network.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.nexus_social_network.Models.*;
import com.example.nexus_social_network.network.ApiClient;
import com.example.nexus_social_network.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository {

    private static final String TAG = "ChatRepository";
    private final ApiService apiService;
    private final Context context;
    private final SharedPreferences prefs;

    public ChatRepository(Context context) {
        this.context = context;
        this.apiService = ApiClient.getApiService();
        this.prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
    }

    private String getToken() {
        return prefs.getString("jwt_token", null);
    }

    private int getCurrentUserId() {
        return prefs.getInt("user_id", -1);
    }

    // Создать или получить существующий чат
    public LiveData<Integer> createOrGetChat(int otherUserId) {
        MutableLiveData<Integer> result = new MutableLiveData<>();
        String token = getToken();

        if (token == null) {
            result.postValue(-1);
            return result;
        }

        CreateChatDTO request = new CreateChatDTO(otherUserId);

        apiService.createOrGetChat("Bearer " + token, request)
                .enqueue(new Callback<MessageResponseWithChat>() {
                    @Override
                    public void onResponse(Call<MessageResponseWithChat> call,
                                           Response<MessageResponseWithChat> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.postValue(response.body().chatId);
                        } else {
                            Log.e(TAG, "Error creating chat: " + response.code());
                            result.postValue(-1);
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageResponseWithChat> call, Throwable t) {
                        Log.e(TAG, "Network error creating chat", t);
                        result.postValue(-1);
                    }
                });

        return result;
    }

    // Получить все чаты пользователя
    public LiveData<List<ChatDTO>> getUserChats(int page, int limit) {
        MutableLiveData<List<ChatDTO>> result = new MutableLiveData<>();
        String token = getToken();

        if (token == null) {
            result.postValue(null);
            return result;
        }

        apiService.getUserChats("Bearer " + token, page, limit)
                .enqueue(new Callback<List<ChatDTO>>() {
                    @Override
                    public void onResponse(Call<List<ChatDTO>> call, Response<List<ChatDTO>> response) {
                        if (response.isSuccessful()) {
                            result.postValue(response.body());
                        } else {
                            result.postValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ChatDTO>> call, Throwable t) {
                        result.postValue(null);
                    }
                });

        return result;
    }

    // Получить сообщения чата
    public LiveData<List<ChatMessageDTO>> getChatMessages(int chatId, int page, int limit) {
        MutableLiveData<List<ChatMessageDTO>> result = new MutableLiveData<>();
        String token = getToken();

        if (token == null) {
            result.postValue(null);
            return result;
        }

        apiService.getChatMessages("Bearer " + token, chatId, page, limit)
                .enqueue(new Callback<List<ChatMessageDTO>>() {
                    @Override
                    public void onResponse(Call<List<ChatMessageDTO>> call,
                                           Response<List<ChatMessageDTO>> response) {
                        if (response.isSuccessful()) {
                            result.postValue(response.body());
                        } else {
                            result.postValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ChatMessageDTO>> call, Throwable t) {
                        result.postValue(null);
                    }
                });

        return result;
    }

    // Отправить сообщение
    public LiveData<Boolean> sendMessage(int chatId, String content, int receiverId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        String token = getToken();

        if (token == null) {
            result.postValue(false);
            return result;
        }

        SendMessageDTO request = new SendMessageDTO(content, receiverId);

        apiService.sendMessage("Bearer " + token, chatId, request)
                .enqueue(new Callback<MessageResponseWithChat>() {
                    @Override
                    public void onResponse(Call<MessageResponseWithChat> call,
                                           Response<MessageResponseWithChat> response) {
                        result.postValue(response.isSuccessful());
                    }

                    @Override
                    public void onFailure(Call<MessageResponseWithChat> call, Throwable t) {
                        result.postValue(false);
                    }
                });

        return result;
    }
}