package com.example.testbackend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Verificação simples de sessão
        SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        setupNavigation();
    }

    private void setupNavigation() {
        // Botão Exercícios
        MaterialButton btnExercises = findViewById(R.id.btnExercises);
        if (btnExercises != null) {
            btnExercises.setOnClickListener(v -> {
                startActivity(new Intent(this, ExerciseListActivity.class));
            });
        }

        // Botão Saúde (Health Hub)
        MaterialButton btnHealth = findViewById(R.id.btnHealth);
        if (btnHealth != null) {
            btnHealth.setOnClickListener(v -> {
                startActivity(new Intent(this, HealthHubActivity.class));
            });
        }

        // Card Progresso
        MaterialCardView cardProgress = findViewById(R.id.cardProgress);
        if (cardProgress != null) {
            cardProgress.setOnClickListener(v -> {
                startActivity(new Intent(this, ProgressDashboardActivity.class));
            });
        }

        // Botão Configurações
        MaterialButton btnSettings = findViewById(R.id.btnSettings);
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                startActivity(new Intent(this, SettingsActivity.class));
            });
        }

        // Assistente IA (Botão Flutuante)
        FloatingActionButton fabAssistant = findViewById(R.id.fabAssistant);
        if (fabAssistant != null) {
            fabAssistant.setOnClickListener(v -> {
                startActivity(new Intent(this, AssistantActivity.class));
            });
        }

        // Botão Logout
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                logout();
            });
        }
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
        prefs.edit().remove("jwt_token").apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}