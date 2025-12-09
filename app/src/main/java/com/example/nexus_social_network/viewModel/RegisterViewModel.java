package com.example.nexus_social_network.viewModel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.nexus_social_network.responce.RegisterResponse;
import com.example.nexus_social_network.repository.UserRepository;

public class RegisterViewModel extends ViewModel {
    private UserRepository repository;

    public RegisterViewModel(UserRepository repository){
        this.repository = repository;
    }

    public LiveData<RegisterResponse> register(String email, String passwordHash, String username){
        return repository.registerUser(email, passwordHash, username);
    }
}
