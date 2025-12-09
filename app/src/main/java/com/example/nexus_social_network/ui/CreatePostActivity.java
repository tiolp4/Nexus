package com.example.nexus_social_network.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.nexus_social_network.Models.FileUtils;
import com.example.nexus_social_network.R;
import com.example.nexus_social_network.network.ApiClient;
import com.example.nexus_social_network.repository.PostRepository;
import com.example.nexus_social_network.viewModel.PostViewModel;

import java.io.File;

public class CreatePostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 100;

    private EditText contentEditText;
    private ImageView imagePreview,backButton;
    private Button selectImageButton, createPostButton;

    private File selectedImageFile = null;
    private PostViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        contentEditText = findViewById(R.id.editTextPostContent);
        imagePreview = findViewById(R.id.imageViewPostPreview);
        selectImageButton = findViewById(R.id.buttonSelectImage);
        createPostButton = findViewById(R.id.buttonCreatePost);
        backButton = findViewById(R.id.backButton);
        PostRepository repository = new PostRepository(ApiClient.getApiService());
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new PostViewModel(repository);
            }
        }).get(PostViewModel.class);

        selectImageButton.setOnClickListener(v -> openImagePicker());
        createPostButton.setOnClickListener(v -> createPost());
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        viewModel.getPostResponse().observe(this, postResponseDTO -> {
            if (postResponseDTO != null) {
                Toast.makeText(this, "Post created! ID: " + postResponseDTO.getPostId(), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to create post", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                imagePreview.setImageURI(imageUri);
                selectedImageFile = new File(FileUtils.getPath(this, imageUri));
            }
        }
    }

    private void createPost() {
        String content = contentEditText.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "Content cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = getSharedPreferences("my_prefs", MODE_PRIVATE)
                .getString("jwt_token", "");
        viewModel.createPost(token, content, selectedImageFile);

    }
}
