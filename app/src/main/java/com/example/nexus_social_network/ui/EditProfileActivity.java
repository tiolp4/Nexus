package com.example.nexus_social_network.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.nexus_social_network.R;
import com.example.nexus_social_network.network.ApiClient;
import com.example.nexus_social_network.Models.FileUtils;
import com.example.nexus_social_network.repository.UserRepository;
import com.example.nexus_social_network.viewModel.EditProfileViewModel;
import com.squareup.picasso.Picasso;

import java.io.File;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView avatarView;
    private EditText usernameInput, descriptionInput;
    private Button saveButton, chooseAvatarButton, deleteAvatarButton;

    private Uri selectedAvatarUri = null;
    private EditProfileViewModel viewModel;

    private ActivityResultLauncher<Intent> chooseImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profle);

        avatarView = findViewById(R.id.editProfileImage);
        usernameInput = findViewById(R.id.editTextUsernameProfile);
        descriptionInput = findViewById(R.id.editDescriptionProfile);
        deleteAvatarButton = findViewById(R.id.buttonDeleteProfile);
        saveButton = findViewById(R.id.buttonSaveProfile);

        UserRepository repository = new UserRepository(this);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new EditProfileViewModel(getApplication(), repository);
            }
        }).get(EditProfileViewModel.class);

        initImagePicker();
        loadProfile();

        avatarView.setOnClickListener(v -> pickImage());
        deleteAvatarButton.setOnClickListener(v -> deleteAvatar());
        saveButton.setOnClickListener(v -> saveProfile());
    }

    private void loadProfile() {
        viewModel.getProfile().observe(this, user -> {
            if (user != null) {
                usernameInput.setText(user.getUsername());
                descriptionInput.setText(user.getDescription());
                if (user.getAvatarUrl() != null) {
                    Picasso.get().load(ApiClient.BASE_URL + user.getAvatarUrl()).into(avatarView);
                } else {
                    avatarView.setImageResource(R.drawable.user);
                }
            }
        });
    }

    private void initImagePicker() {
        chooseImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedAvatarUri = result.getData().getData();
                        avatarView.setImageURI(selectedAvatarUri);
                        uploadAvatar();
                    }
                }
        );
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        chooseImageLauncher.launch(intent);
    }

    private void uploadAvatar() {
        if (selectedAvatarUri == null) return;

        File file = FileUtils.getFile(this, selectedAvatarUri);
        if (file == null) return;

        viewModel.uploadAvatar(file).observe(this, avatar -> {
            if (avatar != null) {
                Toast.makeText(this, "Аватар обновлён", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Ошибка загрузки аватара", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteAvatar() {
        viewModel.deleteAvatar().observe(this, success -> {
            if (success != null && success) {
                avatarView.setImageResource(R.drawable.user);
                Toast.makeText(this, "Аватар удалён", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Ошибка удаления аватара", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String username = usernameInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        viewModel.updateProfile(username, description).observe(this, user -> {
            if (user != null) {
                Toast.makeText(this, "Профиль сохранён", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Ошибка сохранения профиля", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
