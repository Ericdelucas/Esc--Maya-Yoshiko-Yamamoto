package com.example.testbackend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.testbackend.adapters.ChatAdapter;
import com.example.testbackend.models.ChatMessage;
import com.example.testbackend.models.AssistantRequest;
import com.example.testbackend.models.AssistantResponse;
import com.example.testbackend.models.AssistantAction;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.AssistantApi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssistantActivity extends AppCompatActivity {

    private static final String TAG = "AI_CHAT";
    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private List<ChatMessage> messages;
    private TextInputEditText etMessage;
    private MaterialButton btnSend;
    private String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant);

        setupToolbar();
        setupChat();
        setupSession();

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        if (btnSend != null) {
            btnSend.setOnClickListener(v -> {
                if (etMessage != null && etMessage.getText() != null) {
                    String text = etMessage.getText().toString().trim();
                    if (!text.isEmpty()) {
                        sendMessage(text);
                    }
                }
            });
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Assistente SmartSaúde");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupChat() {
        rvChat = findViewById(R.id.rvChat);
        messages = new ArrayList<>();
        messages.add(new ChatMessage("Olá! Sou o assistente do SmartSaúde. Como posso ajudar você hoje?", ChatMessage.TYPE_ASSISTANT));
        
        adapter = new ChatAdapter(messages);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);
    }

    private void setupSession() {
        SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
        sessionId = prefs.getString("assistant_session_id", null);
        if (sessionId == null) {
            sessionId = "session-" + System.currentTimeMillis();
            prefs.edit().putString("assistant_session_id", sessionId).apply();
        }
    }

    private void sendMessage(final String text) {
        Log.d(TAG, "Enviando mensagem: " + text);
        
        addMessage(text, ChatMessage.TYPE_USER);
        etMessage.setText("");
        btnSend.setEnabled(false);

        addMessage("IA está pensando...", ChatMessage.TYPE_ASSISTANT);
        final int thinkingPos = messages.size() - 1;

        AssistantApi api = ApiClient.getAiClient().create(AssistantApi.class);
        AssistantRequest request = new AssistantRequest(
            sessionId,
            1, 
            text,
            "AssistantActivity"
        );

        api.chat(request).enqueue(new Callback<AssistantResponse>() {
            @Override
            public void onResponse(Call<AssistantResponse> call, Response<AssistantResponse> response) {
                btnSend.setEnabled(true);
                messages.remove(thinkingPos);
                
                Log.d(TAG, "HTTP code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    AssistantResponse res = response.body();
                    addMessage(res.getReply(), ChatMessage.TYPE_ASSISTANT);

                    AssistantAction action = res.getAction();
                    // Fallback local se o backend não retornar uma ação clara
                    if (action == null) {
                        action = resolveLocalNavigation(text);
                    }

                    if (action != null && "open_screen".equals(action.getType())) {
                        showNavigationAction(action);
                    }
                } else {
                    Log.e(TAG, "Erro na resposta: " + response.code());
                    addMessage("Não consegui processar sua solicitação agora.", ChatMessage.TYPE_ASSISTANT);
                    
                    // Fallback mesmo em caso de erro do servidor
                    AssistantAction localAction = resolveLocalNavigation(text);
                    if (localAction != null) {
                        showNavigationAction(localAction);
                    }
                }
                adapter.notifyDataSetChanged();
                rvChat.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onFailure(Call<AssistantResponse> call, Throwable t) {
                btnSend.setEnabled(true);
                messages.remove(thinkingPos);
                Log.e(TAG, "Falha de rede", t);
                
                addMessage("Falha de rede: " + t.getClass().getSimpleName(), ChatMessage.TYPE_ASSISTANT);
                
                // Fallback local para offline
                AssistantAction localAction = resolveLocalNavigation(text);
                if (localAction != null) {
                    showNavigationAction(localAction);
                }
                
                adapter.notifyDataSetChanged();
                rvChat.scrollToPosition(messages.size() - 1);
            }
        });
    }

    private void addMessage(String text, int type) {
        messages.add(new ChatMessage(text, type));
        adapter.notifyItemInserted(messages.size() - 1);
        rvChat.scrollToPosition(messages.size() - 1);
    }

    private AssistantAction resolveLocalNavigation(String text) {
        if (text == null) return null;
        String lower = text.toLowerCase();

        if (lower.contains("imc") || lower.contains("indice de massa corporal")) {
            return new AssistantAction("open_screen", "imc_calculator", "Abrir Calculadora de IMC");
        }
        if (lower.contains("gordura")) {
            return new AssistantAction("open_screen", "body_fat_calculator", "Abrir Calculadora de Gordura");
        }
        if (lower.contains("histórico") || lower.contains("historico")) {
            return new AssistantAction("open_screen", "health_history", "Abrir Histórico de Saúde");
        }
        if (lower.contains("questionário") || lower.contains("questionario")) {
            return new AssistantAction("open_screen", "health_questionnaire", "Abrir Questionário");
        }
        if (lower.contains("progresso") || lower.contains("evolução") || lower.contains("evolucao")) {
            return new AssistantAction("open_screen", "progress_dashboard", "Abrir Painel de Progresso");
        }
        if (lower.contains("exerc") || lower.contains("treino") || lower.contains("lista")) {
            return new AssistantAction("open_screen", "exercise_list", "Abrir Lista de Exercícios");
        }
        if (lower.contains("config") || lower.contains("ajuste")) {
            return new AssistantAction("open_screen", "settings", "Abrir Configurações");
        }
        return null;
    }

    private void showNavigationAction(AssistantAction action) {
        new MaterialAlertDialogBuilder(this)
            .setTitle(action.getLabel() != null ? action.getLabel() : "Sugestão do Assistente")
            .setMessage("Deseja abrir a tela recomendada agora?")
            .setPositiveButton("Abrir agora", (dialog, which) -> openTargetScreen(action.getTarget()))
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void openTargetScreen(String target) {
        Intent intent = null;
        switch (target) {
            case "body_fat_calculator":
                intent = new Intent(this, BodyFatCalculatorActivity.class);
                break;
            case "imc_calculator":
                intent = new Intent(this, ImcCalculatorActivity.class);
                break;
            case "health_history":
                intent = new Intent(this, HealthHistoryActivity.class);
                break;
            case "health_questionnaire":
                intent = new Intent(this, HealthQuestionnaireActivity.class);
                break;
            case "exercise_list":
                intent = new Intent(this, ExerciseListActivity.class);
                break;
            case "progress_dashboard":
                intent = new Intent(this, ProgressDashboardActivity.class);
                break;
            case "settings":
                intent = new Intent(this, SettingsActivity.class);
                break;
        }

        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Tela não reconhecida: " + target, Toast.LENGTH_SHORT).show();
        }
    }
}