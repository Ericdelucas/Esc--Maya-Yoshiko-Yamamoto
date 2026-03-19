package com.example.testbackend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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
    private MaterialCardView cardAccountAvatar, cardProfessionalExams;
    private ImageButton btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        String email = prefs.getString("user_email", "usuário");
        String role = prefs.getString("user_role", "Patient");
        
        if (token == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        
        setupAvatar(email);
        setupNavigation(role);
    }

    private void setupAvatar(String email) {
        tvUserInitial = findViewById(R.id.tvUserInitial);
        cardAccountAvatar = findViewById(R.id.cardAccountAvatar);
        btnSettings = findViewById(R.id.btnSettings);

        if (email != null && !email.isEmpty()) {
            String initial = email.substring(0, 1).toUpperCase();
            tvUserInitial.setText(initial);
        }

        cardAccountAvatar.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        }
    }

    private void setupNavigation(String role) {
        // --- VISIBILIDADE DO MÉDICO ---
        cardProfessionalExams = findViewById(R.id.cardProfessionalExams);
        if (role != null && (role.equalsIgnoreCase("Professional") || role.equalsIgnoreCase("Doctor"))) {
            cardProfessionalExams.setVisibility(View.VISIBLE);
            cardProfessionalExams.setOnClickListener(v -> {
                // TODO: Criar PatientExamsActivity
                Toast.makeText(this, "Abrindo Gestão de Exames...", Toast.LENGTH_SHORT).show();
            });
        }

        // Botão Meus Exercícios
        findViewById(R.id.btnExercises).setOnClickListener(v -> startActivity(new Intent(this, ExerciseListActivity.class)));

        // Seção Gamificação
        findViewById(R.id.cardChallenges).setOnClickListener(v -> startActivity(new Intent(this, ChallengesActivity.class)));
        findViewById(R.id.cardLeaderboard).setOnClickListener(v -> startActivity(new Intent(this, LeaderboardActivity.class)));
        findViewById(R.id.cardGoals).setOnClickListener(v -> startActivity(new Intent(this, GoalsActivity.class)));
        findViewById(R.id.cardProfessionals).setOnClickListener(v -> startActivity(new Intent(this, ProfessionalsActivity.class)));

        // Seção Saúde
        findViewById(R.id.btnHealth).setOnClickListener(v -> startActivity(new Intent(this, HealthHubActivity.class)));

        // Card de Progresso
        findViewById(R.id.cardProgress).setOnClickListener(v -> startActivity(new Intent(this, ProgressActivity.class)));

        // Botão de Logout
        findViewById(R.id.btnLogout).setOnClickListener(v -> logout());

        // FAB Assistente IA
        findViewById(R.id.fabAssistant).setOnClickListener(v -> startActivity(new Intent(this, AssistantActivity.class)));
    }

    private void logout() {
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