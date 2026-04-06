package com.example.testbackend;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.testbackend.adapters.MessageAdapter;
import com.example.testbackend.models.Message;
import com.example.testbackend.utils.TokenManager;
import com.example.testbackend.websocket.ChatWebSocketClient;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements ChatWebSocketClient.ChatWebSocketListener {
    private static final String TAG = "CHAT_ACT";
    
    private RecyclerView rvMessages;
    private MessageAdapter adapter;
    private List<Message> messages;
    private EditText etMessage;
    private ImageButton btnSend;
    private ChatWebSocketClient wsClient;
    private TokenManager tokenManager;
    private int currentUserId;
    private int receiverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tokenManager = new TokenManager(this);
        // Em um cenário real, o ID do receptor viria via Intent
        receiverId = getIntent().getIntExtra("RECEIVER_ID", -1);
        
        setupViews();
        setupWebSocket();
    }

    private void setupViews() {
        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        
        messages = new ArrayList<>();
        // Mock current user ID para teste. Idealmente extraído do token.
        currentUserId = 1; 
        
        adapter = new MessageAdapter(messages, currentUserId);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);

        btnSend.setOnClickListener(v -> {
            String content = etMessage.getText().toString().trim();
            if (!content.isEmpty()) {
                Message msg = new Message();
                msg.setContent(content);
                msg.setSenderId(currentUserId);
                msg.setReceiverId(receiverId);
                
                wsClient.sendMessage(msg);
                
                // Adiciona localmente para feedback imediato
                messages.add(msg);
                adapter.notifyItemInserted(messages.size() - 1);
                rvMessages.scrollToPosition(messages.size() - 1);
                etMessage.setText("");
            }
        });
    }

    private void setupWebSocket() {
        wsClient = new ChatWebSocketClient(tokenManager.getAuthToken(), this);
        wsClient.connect();
    }

    @Override
    public void onMessageReceived(Message message) {
        runOnUiThread(() -> {
            messages.add(message);
            adapter.notifyItemInserted(messages.size() - 1);
            rvMessages.scrollToPosition(messages.size() - 1);
        });
    }

    @Override
    public void onConnected() {
        Log.d(TAG, "Chat conectado");
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "Chat desconectado");
    }

    @Override
    public void onError(String error) {
        runOnUiThread(() -> Toast.makeText(this, "Erro no chat: " + error, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wsClient != null) wsClient.disconnect();
    }
}
