package com.example.nexus_social_network.Models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nexus_social_network.R;
import com.example.nexus_social_network.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public interface OnPostClick {
        void onClick(PostFeedItemDTO post);
    }

    private List<PostFeedItemDTO> posts = new ArrayList<>();
    private final OnPostClick listener;

    public PostAdapter(OnPostClick listener) {
        this.listener = listener;
    }

    public void setData(List<PostFeedItemDTO> list) {
        this.posts = list;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item_search, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder h, int pos) {
        PostFeedItemDTO post = posts.get(pos);

        h.username.setText(post.username);
        h.text.setText(post.contentText);

        Glide.with(h.itemView.getContext())
                .load(ApiClient.BASE_URL + post.userAvatar)
                .into(h.avatar);

        if (post.imageUrl != null && !post.imageUrl.isEmpty()) {
            h.postImage.setVisibility(View.VISIBLE);
            Glide.with(h.itemView.getContext())
                    .load(ApiClient.BASE_URL + post.imageUrl)
                    .into(h.postImage);
        } else {
            h.postImage.setVisibility(View.GONE);
        }

        h.itemView.setOnClickListener(v -> listener.onClick(post));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar, postImage;
        TextView username, text;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.ivUserAvatar);
            postImage = itemView.findViewById(R.id.ivPostImage);
            username = itemView.findViewById(R.id.tvPostUsername);
            text = itemView.findViewById(R.id.tvPostText);
        }
    }
}
