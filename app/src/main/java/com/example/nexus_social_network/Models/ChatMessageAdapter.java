package com.example.nexus_social_network.Models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nexus_social_network.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<ChatMessageDTO> messages = new ArrayList<>();
    private int currentUserId;
    private final SimpleDateFormat timeFormat;

    public ChatMessageAdapter(int currentUserId) {
        this.currentUserId = currentUserId;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    public void setMessages(List<ChatMessageDTO> messages) {
        this.messages = messages != null ? messages : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessageDTO message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessageDTO message = messages.get(position);
        return message.senderId == currentUserId ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_SENT) {
            View view = inflater.inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessageDTO message = messages.get(position);

        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // Вспомогательный метод для форматирования времени
    private String formatTime(String timestamp) {
        try {
            // Пробуем разные форматы timestamp
            long time;
            if (timestamp.contains("-") || timestamp.contains("T")) {
                // Это ISO строка даты
                // Временное решение - показываем текущее время
                time = System.currentTimeMillis();
            } else {
                // Пробуем как число
                time = Long.parseLong(timestamp);
            }
            return timeFormat.format(new Date(time));
        } catch (Exception e) {
            e.printStackTrace();
            return ""; // или можно вернуть "--:--"
        }
    }

    // ViewHolder для отправленных сообщений
    class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageText;
        private final TextView messageTime;
        private final TextView readStatus;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.sentMessageText);
            messageTime = itemView.findViewById(R.id.sentMessageTime);
            readStatus = itemView.findViewById(R.id.sentMessageStatus);
        }

        public void bind(ChatMessageDTO message) {
            messageText.setText(message.content);

            // Время
            messageTime.setText(formatTime(message.createdAt));

            // Статус прочтения
            if (message.isRead) {
                readStatus.setText("✓✓");
                readStatus.setTextColor(itemView.getResources().getColor(android.R.color.holo_blue_dark));
            } else {
                readStatus.setText("✓");
                readStatus.setTextColor(itemView.getResources().getColor(android.R.color.darker_gray));
            }
        }
    }

    // ViewHolder для полученных сообщений
    class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageText;
        private final TextView messageTime;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.receivedMessageText);
            messageTime = itemView.findViewById(R.id.receivedMessageTime);
        }

        public void bind(ChatMessageDTO message) {
            messageText.setText(message.content);
            messageTime.setText(formatTime(message.createdAt));
        }
    }
}