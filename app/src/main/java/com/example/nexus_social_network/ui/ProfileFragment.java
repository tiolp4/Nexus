package com.example.nexus_social_network.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.nexus_social_network.R;
import com.example.nexus_social_network.repository.UserRepository;
import com.example.nexus_social_network.viewModel.ProfileViewModel;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private ProfileViewModel viewModel;
    private ImageView profileImage, settingImage;
    private TextView usernameText, descriptionText;
    private Button editButton, buttonAddPost;

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImage = view.findViewById(R.id.imageViewIcon);
        usernameText = view.findViewById(R.id.textViewUsernameProfile);
        descriptionText = view.findViewById(R.id.textViewDescription);
        editButton = view.findViewById(R.id.buttonEditProfile);
        buttonAddPost = view.findViewById(R.id.buttonAddPost);
        settingImage = view.findViewById(R.id.imageViewSettings);
        UserRepository repo = new UserRepository(requireContext());

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new ProfileViewModel(repo);
            }
        }).get(ProfileViewModel.class);

        // Подписка на ошибки
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Log.e(TAG, "Ошибка при загрузке профиля: " + error);

                if (error.contains("401") || error.contains("Unauthorized")) {
                    // Токен недействительный - очищаем и переходим на логин
                    clearTokenAndGoToLogin();
                } else {
                    Toast.makeText(requireContext(), "Ошибка: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Подписка на профиль
        viewModel.getProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                usernameText.setText(profile.getUsername());
                descriptionText.setText(
                        profile.getDescription() != null && !profile.getDescription().isEmpty()
                                ? profile.getDescription()
                                : "Без описания"
                );

                String avatarUrl = profile.getAvatarUrl();
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    if (!avatarUrl.startsWith("http")) {
                        avatarUrl = "http://10.0.2.2:8080" + avatarUrl;
                    }
                    Glide.with(requireContext())
                            .load(avatarUrl)
                            .circleCrop()
                            .placeholder(R.drawable.user_ph)
                            .into(profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.user_ph);
                }
            }
        });

        buttonAddPost.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CreatePostActivity.class)));

        editButton.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), EditProfileActivity.class)));
        settingImage.setOnClickListener(v->
                startActivity(new Intent(requireContext(), SettingsActivity.class)));
        // Загружаем профиль
        viewModel.loadProfile();
    }

    private void clearTokenAndGoToLogin() {
        try {
            // Очищаем токен
            SharedPreferences prefs = requireContext().getSharedPreferences("my_prefs", 0);
            prefs.edit().remove("jwt_token").apply();

            // Показываем сообщение
            Toast.makeText(requireContext(),
                    "Сессия истекла. Пожалуйста, войдите снова.",
                    Toast.LENGTH_LONG).show();

            // Переходим на логин
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();

        } catch (Exception e) {
            Log.e(TAG, "Ошибка при очистке токена", e);
        }
    }
}