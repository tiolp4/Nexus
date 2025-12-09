package com.example.nexus_social_network.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.nexus_social_network.responce.AvatarResponse;
import com.example.nexus_social_network.responce.UserProfileResponse;
import com.example.nexus_social_network.repository.UserRepository;

import java.io.File;

public class EditProfileViewModel extends AndroidViewModel {

    private final UserRepository repository;

    public EditProfileViewModel(@NonNull Application application, UserRepository repository) {
        super(application);
        this.repository = repository;
    }

    public LiveData<UserProfileResponse> getProfile() {
        return repository.getProfile();
    }

    public LiveData<UserProfileResponse> updateProfile(String username, String description) {
        return repository.updateProfile(username, description);
    }

    public LiveData<AvatarResponse> uploadAvatar(File file) {
        return repository.uploadAvatar(file);
    }

    public LiveData<Boolean> deleteAvatar() {
        return repository.deleteAvatar();
    }
}
