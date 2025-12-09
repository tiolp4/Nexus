package com.example.nexus_social_network.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.nexus_social_network.Models.UserProfileDTO;
import com.example.nexus_social_network.R;
import com.example.nexus_social_network.repository.ChatRepository;
import com.example.nexus_social_network.repository.UserRepository;
import com.example.nexus_social_network.viewModel.ChatViewModel;
import com.example.nexus_social_network.viewModel.UserCardViewModel;

public class UserCardDialogFragment extends DialogFragment {

    private static final String ARG_USERNAME = "username";
    private static final String ARG_USER_ID = "user_id";

    // Объявляем переменные
    private ImageView userAvatar;
    private TextView usernameText;
    private TextView descriptionText;
    private TextView onlineStatusText;
    private UserCardViewModel viewModel;
    private ChatViewModel chatViewModel;

    public interface UserCardListener {
        void onMessageClick(String username, int userId);
        void onFollowClick(String username, int userId);
        void onChatCreated(int chatId, String username, int userId);
    }

    private UserCardListener listener;
    private String username;
    private int userId = -1;
    private int currentUserId;

    public static UserCardDialogFragment newInstance(String username, int userId) {
        UserCardDialogFragment fragment = new UserCardDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        args.putInt(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setUserCardListener(UserCardListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFullScreen);
        Log.d("UserCardDialog", "onCreate called, username: " + username + ", userId: " + userId);
        // Получаем ID текущего пользователя в onCreate
        if (getContext() != null) {
            SharedPreferences prefs = getContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
            currentUserId = prefs.getInt("user_id", -1);
        }

        if (getArguments() != null) {
            username = getArguments().getString(ARG_USERNAME);
            userId = getArguments().getInt(ARG_USER_ID, -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_card_overlay, container, false);

        // Инициализируем View
        userAvatar = view.findViewById(R.id.cardUserAvatar);
        usernameText = view.findViewById(R.id.cardUsername);
        descriptionText = view.findViewById(R.id.cardDescription);
        onlineStatusText = view.findViewById(R.id.cardStatusText);
        View statusIndicator = view.findViewById(R.id.cardStatusIndicator);

        // Проверяем, не пытаемся ли открыть чат с самим собой
        if (userId == currentUserId) {
            view.findViewById(R.id.btnMessage).setVisibility(View.GONE);
            view.findViewById(R.id.btnFollow).setVisibility(View.GONE);
            TextView warningText = view.findViewById(R.id.warningText);
            if (warningText != null) {
                warningText.setVisibility(View.VISIBLE);
                warningText.setText("Это ваш профиль");
            }
        }

        // Инициализация ViewModel для данных пользователя
        UserRepository repo = new UserRepository(requireContext());
        viewModel = new UserCardViewModel(repo);

        // Инициализация ViewModel для чатов
        ChatRepository chatRepo = new ChatRepository(requireContext());
        chatViewModel = new ChatViewModel(chatRepo);

        // Наблюдатели для данных пользователя
        viewModel.getUserData().observe(getViewLifecycleOwner(), this::updateUI);

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                descriptionText.setText("Ошибка: " + error);
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Наблюдатель для загрузки
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            View progressBar = view.findViewById(R.id.progressBar);
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        // Загружаем данные пользователя
        if (username != null) {
            usernameText.setText("Загрузка...");
            viewModel.loadUserByUsername(username);
        } else {
            usernameText.setText("Пользователь");
        }

        // Кнопка закрытия
        ImageView btnClose = view.findViewById(R.id.btnCloseCard);
        btnClose.setOnClickListener(v -> dismiss());

        // Кнопка "Написать" - создание/открытие чата
        view.findViewById(R.id.btnMessage).setOnClickListener(v -> {
            if (listener != null && username != null && userId != -1) {
                // Проверяем, не пытаемся ли написать самому себе
                if (userId == currentUserId) {
                    Toast.makeText(requireContext(),
                            "Нельзя написать самому себе",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Показываем прогресс
                showLoading(true);

                // Создаем или получаем чат через ViewModel
                chatViewModel.createOrGetChat(userId, new ChatViewModel.ChatCreationCallback() {
                    @Override
                    public void onChatCreated(int chatId) {
                        showLoading(false);

                        // Уведомляем слушателя о создании чата
                        if (listener != null) {
                            listener.onChatCreated(chatId, username, userId);
                        }

                        dismiss();
                    }

                    @Override
                    public void onError() {
                        showLoading(false);
                        Toast.makeText(requireContext(),
                                "Не удалось создать чат",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(requireContext(),
                        "Данные пользователя не загружены",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Кнопка "Подписаться"
        view.findViewById(R.id.btnFollow).setOnClickListener(v -> {
            if (listener != null && username != null && userId != -1) {
                // Проверяем, не пытаемся ли подписаться на самого себя
                if (userId == currentUserId) {
                    Toast.makeText(requireContext(),
                            "Нельзя подписаться на самого себя",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                listener.onFollowClick(username, userId);

                // Меняем текст кнопки после нажатия
                TextView followBtn = view.findViewById(R.id.btnFollow);
                if (followBtn.getText().toString().equals("Подписаться")) {
                    followBtn.setText("Отписаться");
                    followBtn.setBackgroundColor(getResources().getColor(R.color.gray_300));
                } else {
                    followBtn.setText("Подписаться");
                    followBtn.setBackgroundColor(getResources().getColor(R.color.blue_500));
                }
            }
        });
        Log.d("UserCardDialog", "onCreateView, userId from args: " + userId);
        return view;
    }

    // Метод обновления UI
    private void updateUI(UserProfileDTO user) {
        if (user == null || getView() == null) return;

        // Сохраняем ID пользователя, если он пришел с данными
        if (user.getId() > 0 && userId == -1) {
            userId = user.getId();
        }

        // Имя пользователя
        usernameText.setText(user.getUsername() != null ? user.getUsername() : "");

        // Описание (bio)
        if (user.getBio() != null && !user.getBio().isEmpty()) {
            descriptionText.setText(user.getBio());
            descriptionText.setVisibility(View.VISIBLE);
        } else {
            descriptionText.setText("Нет описания");
            descriptionText.setVisibility(View.VISIBLE);
        }

        // Статус онлайн (если есть в API)
        if (onlineStatusText != null) {
            onlineStatusText.setText("В сети");
            onlineStatusText.setTextColor(getResources().getColor(R.color.green_500));
        }

        // Аватар
        String avatarUrl = user.getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            // Добавляем базовый URL если нужно
            if (!avatarUrl.startsWith("http")) {
                avatarUrl = "http://10.0.2.2:8080" + avatarUrl;
            }
            Glide.with(requireContext())
                    .load(avatarUrl)
                    .circleCrop()
                    .placeholder(R.drawable.user_profile)
                    .into(userAvatar);
        } else {
            userAvatar.setImageResource(R.drawable.user_profile);
        }
    }

    private void showLoading(boolean isLoading) {
        if (getView() == null) return;

        View progressBar = getView().findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }

        // Блокируем кнопки при загрузке
        View btnMessage = getView().findViewById(R.id.btnMessage);
        View btnFollow = getView().findViewById(R.id.btnFollow);

        if (btnMessage != null) {
            btnMessage.setEnabled(!isLoading);
            btnMessage.setAlpha(isLoading ? 0.5f : 1.0f);
        }

        if (btnFollow != null) {
            btnFollow.setEnabled(!isLoading);
            btnFollow.setAlpha(isLoading ? 0.5f : 1.0f);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            // Добавляем анимацию
            getDialog().getWindow().setWindowAnimations(R.style.DialogAnimation);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Очищаем ViewModel при уничтожении view
        // НЕ используем getViewLifecycleOwner() здесь!

        // Очищаем ссылки на View
        userAvatar = null;
        usernameText = null;
        descriptionText = null;
        onlineStatusText = null;

        // Очищаем наблюдатели ViewModel
        if (viewModel != null) {
            viewModel.getUserData().removeObservers(this);
            viewModel.getError().removeObservers(this);
            viewModel.getIsLoading().removeObservers(this);
        }

        if (chatViewModel != null) {
            // Очищаем chatViewModel если есть какие-то наблюдатели
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Очищаем слушателя
        listener = null;
    }
}