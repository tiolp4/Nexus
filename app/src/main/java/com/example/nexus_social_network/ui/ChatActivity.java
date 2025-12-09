package com.example.nexus_social_network.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nexus_social_network.Models.ChatMessageAdapter;
import com.example.nexus_social_network.R;
import com.example.nexus_social_network.repository.ChatRepository;
import com.example.nexus_social_network.viewModel.ChatViewModel;

public class ChatActivity extends AppCompatActivity {

    private ChatViewModel viewModel;
    private ChatMessageAdapter messageAdapter;
    private RecyclerView messagesRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton, backButton;
    private ProgressBar progressBar;
    private TextView typingIndicator;

    private int chatId;
    private int otherUserId;
    private String otherUserName;
    private int currentUserId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatId = getIntent().getIntExtra("chatId", -1);
        otherUserId = getIntent().getIntExtra("otherUserId", -1);
        otherUserName = getIntent().getStringExtra("otherUserName");

        SharedPreferences prefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(otherUserName != null ? otherUserName : "Чат");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        progressBar = findViewById(R.id.progressBar);
        typingIndicator = findViewById(R.id.typingIndicator);
        backButton = findViewById(R.id.backButton);

        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new ChatMessageAdapter(currentUserId);
        messagesRecyclerView.setAdapter(messageAdapter);

        ChatRepository repo = new ChatRepository(this);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new ChatViewModel(repo);
            }
        }).get(ChatViewModel.class);

        viewModel.getIsLoading().observe(this, loading -> {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getMessages().observe(this, messages -> {
            if (messages != null) {
                messageAdapter.setMessages(messages);
                scrollToBottom();
            }
        });

        sendButton.setOnClickListener(v -> sendMessage());
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        messageInput.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });

        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    showTypingIndicator(true);
                } else {
                    showTypingIndicator(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        if (chatId != -1) {
            viewModel.loadChatMessages(chatId);
        }
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (!message.isEmpty() && chatId != -1 && otherUserId != -1) {
            viewModel.sendMessage(chatId, message, otherUserId);
            messageInput.setText("");
            showTypingIndicator(false);
        }
    }

    private void scrollToBottom() {
        messagesRecyclerView.postDelayed(() -> {
            if (messageAdapter.getItemCount() > 0) {
                messagesRecyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
            }
        }, 100);
    }

    private void showTypingIndicator(boolean isTyping) {
        typingIndicator.setVisibility(isTyping ? View.VISIBLE : View.GONE);
        typingIndicator.setText(otherUserName + " печатает...");

        if (isTyping) {
            typingIndicator.removeCallbacks(hideTypingRunnable);
            typingIndicator.postDelayed(hideTypingRunnable, 3000);
        }
    }

    private final Runnable hideTypingRunnable = () -> {
        typingIndicator.setVisibility(View.GONE);
    };

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}