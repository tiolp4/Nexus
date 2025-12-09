package com.example.nexus_social_network.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nexus_social_network.responce.UserProfileResponse;
import com.example.nexus_social_network.repository.UserRepository;

public class ProfileViewModel extends ViewModel {
    private final UserRepository repository;
    private final MutableLiveData<UserProfileResponse> profile = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ProfileViewModel(UserRepository repository) {
        this.repository = repository;
    }

    public LiveData<UserProfileResponse> getProfile() {
        return profile;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadProfile() {
        repository.getProfileWithCallback(new UserRepository.ProfileCallback() {
            @Override
            public void onSuccess(UserProfileResponse profileResponse) {
                profile.postValue(profileResponse);
                error.postValue(null);
            }

            @Override
            public void onError(String errorMsg, int errorCode) {
                error.postValue(errorMsg);
                profile.postValue(null);
            }
        });
    }
}