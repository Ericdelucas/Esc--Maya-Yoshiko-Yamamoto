package com.example.testbackend.websocket;

import android.util.Log;
import com.example.testbackend.models.Message;
import com.google.gson.Gson;
import okhttp3.*;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;

public class ChatWebSocketClient {
    private static final String TAG = "CHAT_WS";
    private static final String BASE_URL = "wss://esc-maya-yoshiko-yamamoto.onrender.com/chat/ws/"; // Render.com - Produção

    private OkHttpClient client;
    private WebSocket webSocket;
    private String token;
    private ChatWebSocketListener listener;
    private Gson gson;

    public interface ChatWebSocketListener {
        void onMessageReceived(Message message);
        void onConnected();
        void onDisconnected();
        void onError(String error);
    }

    public ChatWebSocketClient(String token, ChatWebSocketListener listener) {
        this.token = token;
        this.listener = listener;
        this.gson = new Gson();
        this.client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();
    }

    public void connect() {
        Request request = new Request.Builder()
                .url(BASE_URL + token)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "Conectado ao WebSocket");
                if (listener != null) listener.onConnected();
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Mensagem recebida: " + text);
                try {
                    JSONObject json = new JSONObject(text);
                    if (json.getString("type").equals("message")) {
                        Message message = gson.fromJson(json.getJSONObject("data").toString(), Message.class);
                        if (listener != null) listener.onMessageReceived(message);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Erro ao processar mensagem", e);
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
                if (listener != null) listener.onDisconnected();
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "Falha no WebSocket: " + t.getMessage());
                if (listener != null) listener.onError(t.getMessage());
            }
        });
    }

    public void sendMessage(Message message) {
        try {
            JSONObject json = new JSONObject();
            json.put("type", "message");
            json.put("data", new JSONObject(gson.toJson(message)));
            if (webSocket != null) {
                webSocket.send(json.toString());
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao enviar mensagem", e);
        }
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Desconectado pelo usuário");
        }
    }
}
