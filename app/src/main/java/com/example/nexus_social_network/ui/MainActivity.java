package com.example.nexus_social_network.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.nexus_social_network.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        Log.d(TAG, "Токен в SharedPreferences: " + (token != null ? "есть" : "null"));

        if (token == null || token.isEmpty()) {
            Log.d(TAG, "Токена нет, переходим на LoginActivity");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Только если токен есть, показываем главный экран
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;

            int id = item.getItemId();
            if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            } else if (id == R.id.nav_chats) {
                selectedFragment = new ChatsFragment();
            } else if (id == R.id.nav_search) {
                selectedFragment = new SearchFragment();
            } else if (id == R.id.nav_feed) {
                selectedFragment = new FeedFragment();
            } else {
                selectedFragment = new FeedFragment();
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();

            return true;
        });

        // Устанавливаем вкладку по умолчанию
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_feed);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            boolean openProfile = data.getBooleanExtra("open_profile", false);
            if (openProfile) {
                BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
                bottomNav.setSelectedItemId(R.id.nav_profile);
            }
        }
    }
}