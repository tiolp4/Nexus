package com.example.nexus_social_network.ui;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.nexus_social_network.R;
import com.example.nexus_social_network.repository.UserRepository;
import com.example.nexus_social_network.viewModel.RegisterViewModel;


public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etUsername, etPassword;
    private Button btnRegister;
    private RegisterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.editTextEmailRegister);
        etUsername = findViewById(R.id.editTextUsernameRegister);
        etPassword = findViewById(R.id.editTextPasswordRegister);
        btnRegister = findViewById(R.id.buttonCreateAccount);

        UserRepository repo = new UserRepository(this);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new RegisterViewModel(repo);
            }
        }).get(RegisterViewModel.class);

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(email.isEmpty() || username.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.register(email, password, username).observe(this, response -> {
                if(response != null){
                    Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
