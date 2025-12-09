package com.example.nexus_social_network.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nexus_social_network.Models.PostAdapter;
import com.example.nexus_social_network.Models.SearchViewModel;
import com.example.nexus_social_network.Models.UserAdapter;
import com.example.nexus_social_network.Models.UserProfileDTO;
import com.example.nexus_social_network.R;
import com.example.nexus_social_network.repository.ChatRepository;
import com.example.nexus_social_network.viewModel.ChatViewModel;

public class SearchFragment extends Fragment {

    private SearchViewModel viewModel;
    private ChatViewModel chatViewModel;
    private UserAdapter userAdapter;
    private PostAdapter postAdapter;
    private String token;
    private int currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {

        // Получаем токен и ID текущего пользователя
        Context context = requireContext();
        token = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                .getString("jwt_token", "");
        currentUserId = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                .getInt("user_id", -1);

        EditText etSearch = v.findViewById(R.id.etSearch);
        RecyclerView recyclerUsers = v.findViewById(R.id.recyclerUsers);
        RecyclerView recyclerPosts = v.findViewById(R.id.recyclerPosts);

        // Инициализация ViewModel для поиска
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        // Инициализация ViewModel для чатов
        ChatRepository chatRepo = new ChatRepository(context);
        chatViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new ChatViewModel(chatRepo);
            }
        }).get(ChatViewModel.class);

        // Используем ваш UserAdapter с адаптированным callback
        userAdapter = new UserAdapter(new UserAdapter.OnUserClick() {
            @Override
            public void onClick(UserProfileDTO user) {
                Log.d("SearchFragment", "User clicked: " + user.username + ", ID: " + user.id);

                if (user != null && user.username != null) {
                    Log.d("SearchFragment", "Showing card for user: " + user.username + " with ID: " + user.id);
                    int userId = user.id > 0 ? user.id : -1;
                    showUserCard(user.username, userId);
                }
            }
        });

        postAdapter = new PostAdapter(post -> {
            Intent intent = new Intent(requireContext(), PostDetailsActivity.class);
            intent.putExtra("postId", post.id);
            startActivity(intent);
        });

        recyclerUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerUsers.setAdapter(userAdapter);

        recyclerPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerPosts.setAdapter(postAdapter);

        viewModel.getUserResult().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                userAdapter.setSingleUser(user);
            }
        });

        viewModel.getPostResults().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null) {
                postAdapter.setData(posts);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.search(s.toString(), token);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Метод для показа карточки пользователя
    private void showUserCard(String username, int userId) {
        try {
            // Создаем диалог с передачей username и ID
            UserCardDialogFragment dialog = UserCardDialogFragment.newInstance(username, userId);

            dialog.setUserCardListener(new UserCardDialogFragment.UserCardListener() {
                @Override
                public void onMessageClick(String username, int userId) {
                    // Создаем или открываем чат с пользователем
                    createOrOpenChat(username, userId);
                }

                @Override
                public void onFollowClick(String username, int userId) {
                    // Подписаться/отписаться от пользователя
                    toggleFollowUser(username, userId);
                }

                @Override
                public void onChatCreated(int chatId, String username, int userId) {
                    // Открываем чат после создания
                    openChatActivity(chatId, userId, username);
                }
            });

            // Используем childFragmentManager для фрагментов внутри фрагмента
            if (getChildFragmentManager() != null) {
                dialog.show(getChildFragmentManager(), "user_card_dialog");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(),
                    "Ошибка открытия профиля",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Создать или открыть чат с пользователем
    private void createOrOpenChat(String username, int otherUserId) {
        if (otherUserId == -1) {
            Toast.makeText(requireContext(),
                    "Не удалось получить ID пользователя",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (otherUserId == currentUserId) {
            Toast.makeText(requireContext(),
                    "Нельзя написать самому себе",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Показываем загрузку
        Toast.makeText(requireContext(),
                "Создание чата...",
                Toast.LENGTH_SHORT).show();

        chatViewModel.createOrGetChat(otherUserId, new ChatViewModel.ChatCreationCallback() {
            @Override
            public void onChatCreated(int chatId) {
                // Открываем чат
                openChatActivity(chatId, otherUserId, username);
            }

            @Override
            public void onError() {
                Toast.makeText(requireContext(),
                        "Не удалось создать чат",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Открыть активность чата
    private void openChatActivity(int chatId, int otherUserId, String otherUserName) {
        try {
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra("chatId", chatId);
            intent.putExtra("otherUserId", otherUserId);
            intent.putExtra("otherUserName", otherUserName);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(),
                    "Чат будет доступен позже",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Подписаться/отписаться от пользователя
    private void toggleFollowUser(String username, int userId) {
        if (userId == currentUserId) {
            Toast.makeText(requireContext(),
                    "Нельзя подписаться на самого себя",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Реализовать логику подписки через API
        Toast.makeText(
                requireContext(),
                "Подписка на " + username,
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Очищаем ViewModel при уничтожении view
        if (chatViewModel != null) {
            chatViewModel = null;
        }
    }
}