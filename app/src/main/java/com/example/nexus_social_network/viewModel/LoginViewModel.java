package com.example.nexus_social_network.viewModel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.nexus_social_network.responce.LoginResponse;
import com.example.nexus_social_network.repository.UserRepository;

public class LoginViewModel extends ViewModel {
    private UserRepository repository;

    public LoginViewModel(UserRepository repository){
        this.repository = repository;
    }

    public LiveData<LoginResponse> login(String email, String password){
        return repository.loginUser(email, password);
    }
}
