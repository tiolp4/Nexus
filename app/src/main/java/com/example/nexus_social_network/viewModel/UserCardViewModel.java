package com.example.nexus_social_network.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nexus_social_network.Models.UserProfileDTO;
import com.example.nexus_social_network.repository.UserRepository;

public class UserCardViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<UserProfileDTO> userData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public UserCardViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<UserProfileDTO> getUserData() {
        return userData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadUserByUsername(String username) {
        isLoading.postValue(true);
        error.postValue(null);

        userRepository.getUserByUsername(username, new UserRepository.UserCardCallback() {
            @Override
            public void onSuccess(UserProfileDTO response) {
                isLoading.postValue(false);
                userData.postValue(response);
            }

            @Override
            public void onError(String errorMessage) {
                isLoading.postValue(false);
                error.postValue(errorMessage);
            }
        });
    }
}