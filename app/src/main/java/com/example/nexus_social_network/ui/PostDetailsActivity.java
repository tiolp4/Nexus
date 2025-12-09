package com.example.nexus_social_network.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nexus_social_network.Models.CommentsAdapter;
import com.example.nexus_social_network.R;
import com.example.nexus_social_network.viewModel.PostDetailsViewModel;
import com.example.nexus_social_network.viewModel.PostLikeViewModel;
import com.example.nexus_social_network.network.ApiClient;

public class PostDetailsActivity extends AppCompatActivity {

    private PostDetailsViewModel viewModel;
    private CommentsAdapter adapter;

    private int postId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        postId = getIntent().getIntExtra("postId", -1);
        token = getSharedPreferences("my_prefs", MODE_PRIVATE)
                .getString("jwt_token", "");

        viewModel = new ViewModelProvider(this).get(PostDetailsViewModel.class);
        TextView tvLikes = findViewById(R.id.tvLikes);
        PostLikeViewModel likeViewModel = new ViewModelProvider(this).get(PostLikeViewModel.class);

        adapter = new CommentsAdapter();
        RecyclerView recycler = findViewById(R.id.recyclerComments);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        // UI
        TextView tvUser = findViewById(R.id.tvPostUsername);
        ImageView ivImage = findViewById(R.id.ivPostImage);
        TextView tvText = findViewById(R.id.tvPostContent);

        EditText etComment = findViewById(R.id.etComment);
        Button btnSend = findViewById(R.id.btnSendComment);

        // Observers
        viewModel.getPost().observe(this, post -> {
            if (post != null) {
                tvUser.setText(post.username);
                tvText.setText(post.contentText);

                Glide.with(this)
                        .load(ApiClient.BASE_URL + post.imageUrl)
                        .into(ivImage);
            }
        });
        likeViewModel.getLikesCount().observe(this, count -> tvLikes.setText(count + " likes"));

        likeViewModel.loadLikes(postId);

        tvLikes.setOnClickListener(v -> likeViewModel.toggleLike(token, postId));
        viewModel.getComments().observe(this, comments -> {
            if (comments != null) adapter.setData(comments);
        });

        // Load
        viewModel.loadPost(token, postId);

        btnSend.setOnClickListener(v -> {
            String txt = etComment.getText().toString().trim();
            if (!txt.isEmpty()) {
                viewModel.addComment(token, postId, txt);
                etComment.setText("");
            }
        });
    }
}
