package com.example.nexus_social_network.Models;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nexus_social_network.ui.PostDetailsActivity;
import com.example.nexus_social_network.R;
import com.example.nexus_social_network.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private List<PostDTO> posts = new ArrayList<>();

    public void setPosts(List<PostDTO> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostDTO p = posts.get(position);

        holder.username.setText(p.username);
        holder.content.setText(p.contentText);
        holder.likes.setText("❤️ " + p.likesCount);
        holder.comments.setText("💬 " + p.commentsCount);

        // загрузка изображения
        if (p.imageUrl != null) {
            Glide.with(holder.itemView.getContext())
                    .load(ApiClient.BASE_URL + p.imageUrl)
                    .into(holder.image);
        } else {
            holder.image.setImageDrawable(null);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PostDetailsActivity.class);
            intent.putExtra("postId", p.id);
            v.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() { return posts.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username, content, likes, comments;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tvUsername);
            content = itemView.findViewById(R.id.tvContentText);
            likes = itemView.findViewById(R.id.tvLikes);
            comments = itemView.findViewById(R.id.tvComments);
            image = itemView.findViewById(R.id.ivPostImage);
        }
    }
}
