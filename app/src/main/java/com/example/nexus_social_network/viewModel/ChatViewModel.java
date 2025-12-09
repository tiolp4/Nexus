package com.example.nexus_social_network.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nexus_social_network.Models.ChatDTO;
import com.example.nexus_social_network.Models.ChatMessageDTO;
import com.example.nexus_social_network.repository.ChatRepository;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private final ChatRepository chatRepository;
    private final MutableLiveData<List<ChatDTO>> chats = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<ChatMessageDTO>> messages = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ChatViewModel(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public LiveData<List<ChatDTO>> getChats() {
        return chats;
    }

    public LiveData<List<ChatMessageDTO>> getMessages() {
        return messages;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    // Загрузить чаты пользователя
    public void loadUserChats() {
        isLoading.postValue(true);
        chatRepository.getUserChats(1, 50).observeForever(chatList -> {
            isLoading.postValue(false);
            if (chatList != null) {
                chats.postValue(chatList);
            } else {
                error.postValue("Не удалось загрузить чаты");
            }
        });
    }

    // Загрузить сообщения чата
    public void loadChatMessages(int chatId) {
        isLoading.postValue(true);
        chatRepository.getChatMessages(chatId, 1, 100).observeForever(messageList -> {
            isLoading.postValue(false);
            if (messageList != null) {
                messages.postValue(messageList);
            } else {
                error.postValue("Не удалось загрузить сообщения");
            }
        });
    }

    // Создать или получить чат
    public void createOrGetChat(int otherUserId, ChatCreationCallback callback) {
        isLoading.postValue(true);
        chatRepository.createOrGetChat(otherUserId).observeForever(chatId -> {
            isLoading.postValue(false);
            if (chatId != null && chatId > 0) {
                callback.onChatCreated(chatId);
            } else {
                error.postValue("Не удалось создать чат");
                callback.onError();
            }
        });
    }

    // Отправить сообщение
    public void sendMessage(int chatId, String message, int receiverId) {
        chatRepository.sendMessage(chatId, message, receiverId).observeForever(success -> {
            if (success != null && success) {
                // Перезагружаем сообщения после отправки
                loadChatMessages(chatId);
            } else {
                error.postValue("Не удалось отправить сообщение");
            }
        });
    }

    public interface ChatCreationCallback {
        void onChatCreated(int chatId);
        void onError();
    }
}