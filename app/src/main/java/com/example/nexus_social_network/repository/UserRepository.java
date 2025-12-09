package com.example.nexus_social_network.repository;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.nexus_social_network.responce.AvatarResponse;
import com.example.nexus_social_network.Models.EditProfileRequest;
import com.example.nexus_social_network.Models.LoginRequest;
import com.example.nexus_social_network.responce.LoginResponse;
import com.example.nexus_social_network.responce.MessageResponse;
import com.example.nexus_social_network.Models.RegisterRequest;
import com.example.nexus_social_network.responce.RegisterResponse;
import com.example.nexus_social_network.Models.UserProfileDTO;
import com.example.nexus_social_network.responce.UserProfileResponse;
import com.example.nexus_social_network.responce.UserResponse;
import com.example.nexus_social_network.network.ApiClient;
import com.example.nexus_social_network.network.ApiService;
import com.example.nexus_social_network.ui.LoginActivity;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    public class ApiResult<T> {
        public T data;
        public String error;
        public int errorCode;

        public ApiResult(T data) {
            this.data = data;
        }

        public ApiResult(String error, int errorCode) {
            this.error = error;
            this.errorCode = errorCode;
        }
    }

    public interface ProfileCallback {
        void onSuccess(UserProfileResponse profile);
        void onError(String error, int errorCode);
    }

    private static final String TAG = "UserRepository";

    private final ApiService apiService;
    private final Context context;

    public UserRepository(Context context) {
        this.context = context;
        this.apiService = ApiClient.getApiService();
    }

    private void saveToken(String token, int userId, String username) {
        SharedPreferences prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        prefs.edit()
                .putString("jwt_token", token)
                .putInt("user_id", userId)
                .putString("username", username)
                .apply();
        Log.d(TAG, "Токен сохранен для пользователя: " + username);
    }

    private String getToken() {
        SharedPreferences prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        return prefs.getString("jwt_token", null);
    }

    private void handleUnauthorizedError() {
        Log.w(TAG, "Обнаружена 401 ошибка - очищаем токен и перенаправляем на логин");

        try {
            // Очищаем токен
            SharedPreferences prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
            prefs.edit()
                    .remove("jwt_token")
                    .remove("user_id")
                    .remove("username")
                    .apply();

            // Отправляем broadcast или запускаем активность
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);

        } catch (Exception e) {
            Log.e(TAG, "Ошибка при обработке unauthorized", e);
        }
    }

    // ============================
    // REGISTER
    // ============================

    public LiveData<RegisterResponse> registerUser(String email, String passwordHash, String username) {
        MutableLiveData<RegisterResponse> data = new MutableLiveData<>();
        RegisterRequest req = new RegisterRequest(email, passwordHash, username);

        apiService.registerUser(req).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Регистрация успешна, User ID: " + response.body().getId());
                    data.postValue(response.body());
                } else {
                    Log.e(TAG, "Ошибка регистрации: " + response.code());
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Log.e(TAG, "Ошибка запроса при регистрации", t);
                data.postValue(null);
            }
        });

        return data;
    }

    // ============================
    // LOGIN
    // ============================

    public LiveData<LoginResponse> loginUser(String email, String password) {
        MutableLiveData<LoginResponse> data = new MutableLiveData<>();

        apiService.loginUser(new LoginRequest(email, password))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse res = response.body();
                            saveToken(res.getToken(), res.getUserId(), res.getUsername());
                            Log.d(TAG, "Вход успешен для: " + res.getUsername());
                            data.postValue(res);
                        } else {
                            Log.e(TAG, "Ошибка входа: " + response.code());
                            data.postValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Log.e(TAG, "Ошибка сети при входе", t);
                        data.postValue(null);
                    }
                });

        return data;
    }

    // ============================
    // GET PROFILE /me
    // ============================

    public LiveData<UserProfileResponse> getProfile() {
        MutableLiveData<UserProfileResponse> data = new MutableLiveData<>();

        String token = getToken();
        if (token == null) {
            Log.w(TAG, "Токен отсутствует при запросе профиля");
            data.postValue(null);
            return data;
        }

        apiService.getMyProfile("Bearer " + token)
                .enqueue(new Callback<UserProfileResponse>() {
                    @Override
                    public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Профиль успешно загружен");
                            data.postValue(response.body());
                        } else {
                            Log.e(TAG, "Ошибка загрузки профиля: " + response.code());
                            if (response.code() == 401) {
                                handleUnauthorizedError();
                            }
                            data.postValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                        Log.e(TAG, "Ошибка сети при загрузке профиля", t);
                        data.postValue(null);
                    }
                });

        return data;
    }

    // ============================
    // UPDATE PROFILE (username + description)
    // ============================

    public LiveData<UserProfileResponse> updateProfile(String username, String description) {
        MutableLiveData<UserProfileResponse> data = new MutableLiveData<>();
        String token = getToken();

        if (token == null) {
            Log.w(TAG, "Токен отсутствует при обновлении профиля");
            data.postValue(null);
            return data;
        }

        EditProfileRequest req = new EditProfileRequest(username, description);

        apiService.updateProfile("Bearer " + token, req)
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            UserResponse r = response.body();
                            Log.d(TAG, "Профиль успешно обновлен: " + r.username);
                            data.postValue(new UserProfileResponse(
                                    r.id, r.username, r.description, r.avatarUrl
                            ));
                        } else {
                            Log.e(TAG, "Ошибка обновления профиля: " + response.code());
                            if (response.code() == 401) {
                                handleUnauthorizedError();
                            }
                            data.postValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Log.e(TAG, "Ошибка сети при обновлении профиля", t);
                        data.postValue(null);
                    }
                });

        return data;
    }

    // ============================
    // UPLOAD AVATAR
    // ============================

    public LiveData<AvatarResponse> uploadAvatar(File file) {
        MutableLiveData<AvatarResponse> data = new MutableLiveData<>();
        String token = getToken();

        if (token == null) {
            Log.w(TAG, "Токен отсутствует при загрузке аватара");
            data.postValue(null);
            return data;
        }

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), reqFile);

        apiService.uploadAvatar("Bearer " + token, body)
                .enqueue(new Callback<AvatarResponse>() {
                    @Override
                    public void onResponse(Call<AvatarResponse> call, Response<AvatarResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Аватар успешно загружен");
                            data.postValue(response.body());
                        } else {
                            Log.e(TAG, "Ошибка загрузки аватара: " + response.code());
                            if (response.code() == 401) {
                                handleUnauthorizedError();
                            }
                            data.postValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<AvatarResponse> call, Throwable t) {
                        Log.e(TAG, "Ошибка сети при загрузке аватара", t);
                        data.postValue(null);
                    }
                });

        return data;
    }

    // ============================
    // DELETE AVATAR
    // ============================

    public LiveData<Boolean> deleteAvatar() {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        String token = getToken();

        if (token == null) {
            Log.w(TAG, "Токен отсутствует при удалении аватара");
            data.postValue(false);
            return data;
        }

        apiService.deleteAvatar("Bearer " + token)
                .enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Аватар успешно удален");
                            data.postValue(true);
                        } else {
                            Log.e(TAG, "Ошибка удаления аватара: " + response.code());
                            if (response.code() == 401) {
                                handleUnauthorizedError();
                            }
                            data.postValue(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageResponse> call, Throwable t) {
                        Log.e(TAG, "Ошибка сети при удалении аватара", t);
                        data.postValue(false);
                    }
                });

        return data;
    }

    // ============================
    // Вспомогательный метод для выхода
    // ============================

    public void logout() {
        Log.d(TAG, "Выход из приложения");
        SharedPreferences prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        prefs.edit()
                .remove("jwt_token")
                .remove("user_id")
                .remove("username")
                .apply();
    }


    public void getProfileWithCallback(ProfileCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onError("Токен отсутствует", 0);
            return;
        }

        apiService.getMyProfile("Bearer " + token)
                .enqueue(new Callback<UserProfileResponse>() {
                    @Override
                    public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            if (response.code() == 401) {
                                handleUnauthorizedError();
                                callback.onError("Сессия истекла", 401);
                            } else {
                                callback.onError("Ошибка: " + response.code(), response.code());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                        callback.onError("Ошибка сети: " + t.getMessage(), 0);
                    }
                });
    }


    public interface UserCardCallback {
        void onSuccess(UserProfileDTO response);
        void onError(String errorMessage);
    }

    public void getUserByUsername(String username, UserCardCallback callback) {
        String token = getToken();
        if (token == null) {
            callback.onError("Токен отсутствует");
            return;
        }

        Call<UserProfileDTO> call = apiService.getUserByUsername(username, "Bearer " + token);

        call.enqueue(new Callback<UserProfileDTO>() {
            @Override
            public void onResponse(Call<UserProfileDTO> call, Response<UserProfileDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    if (response.code() == 401) {
                        handleUnauthorizedError();
                        callback.onError("Сессия истекла");
                    } else if (response.code() == 404) {
                        callback.onError("Пользователь не найден");
                    } else {
                        callback.onError("Ошибка " + response.code() + ": " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfileDTO> call, Throwable t) {
                callback.onError("Ошибка сети: " + t.getMessage());
            }
        });
    }
}