package com.example.testbackend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.example.testbackend.utils.LocaleHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_DEBUG";
    private TextView tvUserInitial;
    private MaterialCardView cardAccountAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        String role = prefs.getString("user_role", "Patient");
        String email = prefs.getString("user_email", "usuário");
        
        Log.d(TAG, "Sessão atual - Role: " + role);

        if (token == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        
        setupAvatar(email);
        setupNavigation();
    }

    private void setupAvatar(String email) {
        tvUserInitial = findViewById(R.id.tvUserInitial);
        cardAccountAvatar = findViewById(R.id.cardAccountAvatar);

        if (email != null && !email.isEmpty()) {
            String initial = email.substring(0, 1).toUpperCase();
            tvUserInitial.setText(initial);
        }

        cardAccountAvatar.setOnClickListener(v -> {
            // ABRIR A TELA DE PERFIL PROFISSIONAL
            Log.d(TAG, "Abrindo tela de perfil...");
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void setupNavigation() {
        // Botão Meus Exercícios
        MaterialButton btnExercises = findViewById(R.id.btnExercises);
        if (btnExercises != null) {
            btnExercises.setOnClickListener(v -> {
                Log.d(TAG, "Abrindo Lista de Exercícios...");
                startActivity(new Intent(this, ExerciseListActivity.class));
            });
        }

        // Outras navegações...
        MaterialButton btnHealth = findViewById(R.id.btnHealth);
        if (btnHealth != null) {
            btnHealth.setOnClickListener(v -> startActivity(new Intent(this, HealthHubActivity.class)));
        }

        MaterialCardView cardProgress = findViewById(R.id.cardProgress);
        if (cardProgress != null) {
            cardProgress.setOnClickListener(v -> startActivity(new Intent(this, ProgressDashboardActivity.class)));
        }

        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> logout());
        }

        FloatingActionButton fabAssistant = findViewById(R.id.fabAssistant);
        if (fabAssistant != null) {
            fabAssistant.setOnClickListener(v -> startActivity(new Intent(this, AssistantActivity.class)));
        }
    }

    private void logout() {
        Log.d(TAG, "Limpando sessão (Logout)...");
        SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        
        Toast.makeText(this, "Sessão encerrada", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}