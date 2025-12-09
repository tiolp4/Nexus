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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    public interface OnUserClick {
        void onClick(UserProfileDTO user);
    }

    private UserProfileDTO user;
    private final OnUserClick listener;

    public UserAdapter(OnUserClick listener) {
        this.listener = listener;
    }

    public void setSingleUser(UserProfileDTO user) {
        this.user = user;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder h, int position) {
        if (user == null) return;

        // Проверяем на null перед использованием
        if (h.username != null) {
            h.username.setText(user.username != null ? user.username : "");
        }

        // Проверяем description
        if (h.description != null) {
            if (user.bio != null && !user.bio.isEmpty()) {
                h.description.setText(user.bio);
                h.description.setVisibility(View.VISIBLE);
            } else {
                h.description.setVisibility(View.GONE);
            }
        }

        // Загрузка аватара
        if (h.avatar != null) {
            if (user.avatarUrl != null && !user.avatarUrl.isEmpty()) {
                String avatarUrl = user.avatarUrl;
                if (!avatarUrl.startsWith("http")) {
                    avatarUrl = ApiClient.BASE_URL + user.avatarUrl;
                }
                Glide.with(h.itemView.getContext())
                        .load(avatarUrl)
                        .circleCrop()
                        .placeholder(R.drawable.user_profile)
                        .into(h.avatar);
            } else {
                h.avatar.setImageResource(R.drawable.user_profile);
            }
        }

        h.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return user == null ? 0 : 1;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView username;
        TextView description;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            // ВАЖНО: Используем ID из user_item.xml, а не из fragment_user_card.xml
            avatar = itemView.findViewById(R.id.ivAvatar); // Должен быть в user_item.xml
            username = itemView.findViewById(R.id.tvUsername); // Должен быть в user_item.xml
            description = itemView.findViewById(R.id.tvDescription); // Должен быть в user_item.xml

            // Если description не найден, установим null
            if (description == null) {
                description = itemView.findViewById(R.id.cardDescription); // попробуем как fallback
            }
        }
    }
}