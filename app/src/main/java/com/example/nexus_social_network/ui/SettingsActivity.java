package com.example.nexus_social_network.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nexus_social_network.Models.ChangePasswordDTO;
import com.example.nexus_social_network.R;
import com.example.nexus_social_network.network.ApiClient;
import com.example.nexus_social_network.network.ApiService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    private EditText currentPasswordInput, newPasswordInput, confirmPasswordInput;
    private Button changePasswordButton;
    private LinearLayout logoutButton;

    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Настройка toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Настройки");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Получаем токен
        SharedPreferences prefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        token = prefs.getString("jwt_token", null);

        // Инициализация View
        currentPasswordInput = findViewById(R.id.etCurrentPassword);
        newPasswordInput = findViewById(R.id.etNewPassword);
        confirmPasswordInput = findViewById(R.id.etConfirmPassword);
        changePasswordButton = findViewById(R.id.btnChangePassword);
        logoutButton = findViewById(R.id.btnLogout);

        // Кнопка выхода
        logoutButton.setOnClickListener(v -> showLogoutDialog());

        // Кнопка изменения пароля
        changePasswordButton.setOnClickListener(v -> changePassword());

        // Остальные кнопки (заглушки)
        setupPlaceholderButtons();
    }

    private void setupPlaceholderButtons() {
        // Приватность - заглушка
        findViewById(R.id.btnPrivacy).setOnClickListener(v ->
                showPlaceholderToast("Настройки приватности будут доступны позже"));

        // Уведомления - заглушка
        findViewById(R.id.btnNotifications).setOnClickListener(v ->
                showPlaceholderToast("Настройки уведомлений будут доступны позже"));

        // Удаление аккаунта - заглушка
        findViewById(R.id.btnDeleteAccount).setOnClickListener(v ->
                showPlaceholderToast("Удаление аккаунта будет доступно позже"));
    }

    // Диалог выхода
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Выход")
                .setMessage("Вы уверены, что хотите выйти из аккаунта?")
                .setPositiveButton("Выйти", (dialog, which) -> logout())
                .setNegativeButton("Отмена", null)
                .show();
    }

    // Выход из аккаунта
    private void logout() {
        // Очищаем SharedPreferences
        SharedPreferences prefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Возвращаемся на экран логина
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
    }

    // Изменение пароля
    private void changePassword() {
        String currentPassword = currentPasswordInput.getText().toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Валидация
        if (currentPassword.isEmpty()) {
            currentPasswordInput.setError("Введите текущий пароль");
            return;
        }

        if (newPassword.isEmpty()) {
            newPasswordInput.setError("Введите новый пароль");
            return;
        }

        if (newPassword.length() < 6) {
            newPasswordInput.setError("Пароль должен содержать минимум 6 символов");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordInput.setError("Пароли не совпадают");
            return;
        }

        if (token == null) {
            Toast.makeText(this, "Ошибка: сессия истекла", Toast.LENGTH_SHORT).show();
            logout();
            return;
        }

        // Показываем прогресс
        changePasswordButton.setEnabled(false);
        changePasswordButton.setText("Изменение...");

        // Создаем запрос
        ChangePasswordDTO request = new ChangePasswordDTO(currentPassword, newPassword);

        // Отправляем запрос
        ApiService apiService = ApiClient.getApiService();
        apiService.changePassword("Bearer " + token, request).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                changePasswordButton.setEnabled(true);
                changePasswordButton.setText("Изменить пароль");

                if (response.isSuccessful()) {
                    // Успех
                    Toast.makeText(SettingsActivity.this,
                            "Пароль успешно изменен", Toast.LENGTH_SHORT).show();

                    // Очищаем поля
                    currentPasswordInput.setText("");
                    newPasswordInput.setText("");
                    confirmPasswordInput.setText("");
                } else {
                    // Ошибка
                    String errorMessage = "Ошибка изменения пароля";
                    if (response.code() == 401) {
                        errorMessage = "Неверный текущий пароль";
                    } else if (response.code() == 400) {
                        errorMessage = "Новый пароль слишком короткий";
                    }
                    Toast.makeText(SettingsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                changePasswordButton.setEnabled(true);
                changePasswordButton.setText("Изменить пароль");
                Toast.makeText(SettingsActivity.this,
                        "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPlaceholderToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}