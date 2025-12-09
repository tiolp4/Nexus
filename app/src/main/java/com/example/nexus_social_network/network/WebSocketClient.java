package com.example.nexus_social_network.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.nexus_social_network.Models.WebSocketMessageDTO;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketClient {

    private static final String TAG = "WebSocketClient";
    private static final String WS_URL = "ws://10.0.2.2:8080/chat/ws";

    private WebSocket webSocket;
    private final OkHttpClient client;
    private final Gson gson;
    private final Context context;
    private WebSocketListener listener;

    public interface MessageListener {
        void onNewMessage(String message);
        void onConnected();
        void onDisconnected();
        void onError(String error);
    }

    public WebSocketClient(Context context) {
        this.context = context;
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    public void connect(MessageListener messageListener) {
        SharedPreferences prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null) {
            messageListener.onError("No token found");
            return;
        }

        Request request = new Request.Builder()
                .url(WS_URL)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        this.listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket connected");
                messageListener.onConnected();
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Received message: " + text);
                messageListener.onNewMessage(text);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket closed: " + reason);
                messageListener.onDisconnected();
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket error", t);
                messageListener.onError(t.getMessage());
            }
        };

        webSocket = client.newWebSocket(request, listener);
    }

    public void sendMessage(String type, int chatId, String content, int receiverId) {
        if (webSocket == null) return;

        WebSocketMessageDTO message = new WebSocketMessageDTO();
        message.type = type;
        message.chatId = chatId;
        message.content = content;
        message.receiverId = receiverId;

        String json = gson.toJson(message);
        webSocket.send(json);
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Normal closure");
            webSocket = null;
        }
    }
}