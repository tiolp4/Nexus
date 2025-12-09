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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public interface OnChatClickListener {
        void onChatClick(ChatDTO chat);
    }

    private List<ChatDTO> chats;
    private final OnChatClickListener listener;
    private final SimpleDateFormat dateFormat;

    public ChatAdapter(OnChatClickListener listener) {
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    public void setChats(List<ChatDTO> chats) {
        this.chats = chats;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatDTO chat = chats.get(position);
        holder.bind(chat, listener);
    }

    @Override
    public int getItemCount() {
        return chats != null ? chats.size() : 0;
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final ImageView avatar;
        private final TextView userName;
        private final TextView lastMessage;
        private final TextView timestamp;
        private final TextView unreadCount;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.chatAvatar);
            userName = itemView.findViewById(R.id.chatUserName);
            lastMessage = itemView.findViewById(R.id.chatLastMessage);
            timestamp = itemView.findViewById(R.id.chatTimestamp);
            unreadCount = itemView.findViewById(R.id.chatUnreadCount);
        }

        public void bind(ChatDTO chat, OnChatClickListener listener) {
            // Определяем собеседника
            String otherUserName = chat.userId == chat.user1Id ? chat.user2Name : chat.user1Name;
            String otherUserAvatar = chat.userId == chat.user1Id ? chat.user2Avatar : chat.user1Avatar;

            userName.setText(otherUserName != null ? otherUserName : "Пользователь");

            // Последнее сообщение
            if (chat.lastMessage != null) {
                lastMessage.setText(chat.lastMessage.content);
                lastMessage.setVisibility(View.VISIBLE);

                // Время
                try {
                    Date date = new Date(Long.parseLong(chat.lastMessage.createdAt));
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    timestamp.setText(sdf.format(date));
                } catch (Exception e) {
                    timestamp.setText("");
                }
            } else {
                lastMessage.setText("Нет сообщений");
                timestamp.setText("");
            }

            // Непрочитанные сообщения
            if (chat.unreadCount > 0) {
                unreadCount.setText(String.valueOf(chat.unreadCount));
                unreadCount.setVisibility(View.VISIBLE);
                lastMessage.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                unreadCount.setVisibility(View.GONE);
                lastMessage.setTypeface(null, android.graphics.Typeface.NORMAL);
            }

            // Аватар
            if (otherUserAvatar != null && !otherUserAvatar.isEmpty()) {
                String avatarUrl = otherUserAvatar;
                if (!avatarUrl.startsWith("http")) {
                    avatarUrl = ApiClient.BASE_URL + avatarUrl;
                }
                Glide.with(itemView.getContext())
                        .load(avatarUrl)
                        .circleCrop()
                        .placeholder(R.drawable.user_profile)
                        .into(avatar);
            } else {
                avatar.setImageResource(R.drawable.user_profile);
            }

            // Клик
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChatClick(chat);
                }
            });
        }
    }
}