package com.example.nexus_social_network.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.nexus_social_network.Models.ChatAdapter;
import com.example.nexus_social_network.Models.ChatDTO;
import com.example.nexus_social_network.R;
import com.example.nexus_social_network.repository.ChatRepository;
import com.example.nexus_social_network.viewModel.ChatViewModel;

public class ChatsFragment extends Fragment {

    private ChatViewModel viewModel;
    private ChatAdapter chatAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация View
        recyclerView = view.findViewById(R.id.recyclerChats);
        progressBar = view.findViewById(R.id.progressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        // Настройка RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter = new ChatAdapter(chat -> {
            openChatActivity(chat);
        });
        recyclerView.setAdapter(chatAdapter);

        // Инициализация ViewModel
        ChatRepository repo = new ChatRepository(requireContext());
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new ChatViewModel(repo);
            }
        }).get(ChatViewModel.class);

        // Наблюдатели
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getChats().observe(getViewLifecycleOwner(), chats -> {
            if (chats != null) {
                chatAdapter.setChats(chats);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Pull-to-refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.loadUserChats();
        });

        // Загружаем чаты при создании
        viewModel.loadUserChats();
    }

    private void openChatActivity(ChatDTO chat) {
        Intent intent = new Intent(requireContext(), ChatActivity.class);
        intent.putExtra("chatId", chat.id);
        intent.putExtra("otherUserId", chat.otherUserId);
        intent.putExtra("otherUserName", chat.user1Id == chat.userId ? chat.user2Name : chat.user1Name);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Обновляем чаты при возвращении на экран
        viewModel.loadUserChats();
    }
}