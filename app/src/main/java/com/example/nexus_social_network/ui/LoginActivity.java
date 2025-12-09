package com.example.nexus_social_network.ui;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.nexus_social_network.R;
import com.example.nexus_social_network.repository.UserRepository;
import com.example.nexus_social_network.viewModel.LoginViewModel;


public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView createAccount;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.editTextEmailLogin);
        etPassword = findViewById(R.id.editTextPasswordLogin);
        btnLogin = findViewById(R.id.buttonEnterAccount);
        createAccount = findViewById(R.id.createAccountTextView);
        UserRepository repo = new UserRepository(this);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new LoginViewModel(repo);
            }
        }).get(LoginViewModel.class);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.login(email, password).observe(this, response -> {
                if(response != null && response.getToken() != null){
                    Toast.makeText(this, "Успешный вход!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Ошибка входа", Toast.LENGTH_SHORT).show();
                }
            });
        });

        createAccount.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

}
