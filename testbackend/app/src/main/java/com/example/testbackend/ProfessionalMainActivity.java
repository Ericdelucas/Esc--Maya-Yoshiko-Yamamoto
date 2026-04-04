package com.example.testbackend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testbackend.storage.SessionManager;
import com.example.testbackend.utils.TokenManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProfessionalMainActivity extends AppCompatActivity {
    
    private static final String TAG = "PROF_DEBUG";
    private TokenManager tokenManager;
    private SessionManager sessionManager;
    
    // Novas Views do Layout Redesenhado
    private TextView tvGreeting, tvWelcome, tvUserInitial, tvTotalPacientes, tvConsultasHoje, tvExerciciosAtivos;
    private ImageView ivUserPhoto;
    private MaterialCardView cardAccountAvatar, cardPatients, cardAddPatient, cardExercises, cardReports;
    private FloatingActionButton fabLogout;
    private ImageButton btnSettings;
    private Button btnProfile, btnCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        tokenManager = new TokenManager(this);
        sessionManager = new SessionManager(this);
        
        // Verificação de segurança: Se não for profissional, volta para a MainActivity
        if (!isProfessionalUser()) {
            redirectToCorrectActivity();
            return;
        }

        setContentView(R.layout.activity_main_professional);
        
        setupViews();
        setupClickListeners();
        loadUserInfo();
        loadStatistics();
    }
    
    private boolean isProfessionalUser() {
        String role = tokenManager.getUserRole();
        boolean isProfessional = role != null && (role.equalsIgnoreCase("professional") || role.equalsIgnoreCase("doctor") || role.equalsIgnoreCase("admin"));
        Log.d(TAG, "Verificando perfil: " + role + " -> isProfessional: " + isProfessional);
        return isProfessional;
    }
    
    private void redirectToCorrectActivity() {
        Log.d(TAG, "Usuário não é profissional, redirecionando para MainActivity");
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void setupViews() {
        // Header e Perfil
        tvGreeting = findViewById(R.id.tvGreeting);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvUserInitial = findViewById(R.id.tvUserInitial);
        ivUserPhoto = findViewById(R.id.ivUserPhoto);
        cardAccountAvatar = findViewById(R.id.cardAccountAvatar);
        btnSettings = findViewById(R.id.btnSettings);
        
        // Estatísticas
        tvTotalPacientes = findViewById(R.id.tvTotalPacientes);
        tvConsultasHoje = findViewById(R.id.tvConsultasHoje);
        tvExerciciosAtivos = findViewById(R.id.tvExerciciosAtivos);
        
        // Cards de Gestão
        cardPatients = findViewById(R.id.cardPatients);
        cardAddPatient = findViewById(R.id.cardAddPatient);
        cardExercises = findViewById(R.id.cardExercises);
        cardReports = findViewById(R.id.cardReports);
        
        // Ações Rápidas e Logout
        btnProfile = findViewById(R.id.btnProfile);
        btnCalendar = findViewById(R.id.btnCalendar);
        fabLogout = findViewById(R.id.fabLogout);
    }
    
    private void setupClickListeners() {
        // Clicks do Header
        if (cardAccountAvatar != null) {
            cardAccountAvatar.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        }
        
        // 🔥 CORREÇÃO: Clicar em "Meus Pacientes" deve levar para a lista de pacientes
        if (cardPatients != null) {
            cardPatients.setOnClickListener(v -> {
                Log.d(TAG, "Navegando para PatientsListActivity");
                startActivity(new Intent(this, PatientsListActivity.class));
            });
        }
        
        if (cardAddPatient != null) {
            cardAddPatient.setOnClickListener(v -> Toast.makeText(this, "Funcionalidade: Novo Paciente (Em breve)", Toast.LENGTH_SHORT).show());
        }
        if (cardExercises != null) {
            cardExercises.setOnClickListener(v -> startActivity(new Intent(this, ExerciseListActivity.class)));
        }
        if (cardReports != null) {
            cardReports.setOnClickListener(v -> Toast.makeText(this, "Funcionalidade: Relatórios (Em breve)", Toast.LENGTH_SHORT).show());
        }
        
        // Clicks de Ações Rápidas
        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }
        if (btnCalendar != null) {
            btnCalendar.setOnClickListener(v -> Toast.makeText(this, "Funcionalidade: Agenda (Em breve)", Toast.LENGTH_SHORT).show());
        }
        
        // Logout
        if (fabLogout != null) {
            fabLogout.setOnClickListener(v -> logout());
        }
    }
    
    private void loadUserInfo() {
        String email = tokenManager.getUserEmail();
        if (email != null && !email.isEmpty()) {
            String name = email.split("@")[0];
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            
            if (tvWelcome != null) {
                tvWelcome.setText("Dr(a). " + name);
            }
            if (tvUserInitial != null) {
                tvUserInitial.setText(name.substring(0, 1).toUpperCase());
            }
        }
    }

    private void loadStatistics() {
        // Mock de dados estatísticos (Poderia vir de uma API no futuro)
        if (tvTotalPacientes != null) tvTotalPacientes.setText("12");
        if (tvConsultasHoje != null) tvConsultasHoje.setText("4");
        if (tvExerciciosAtivos != null) tvExerciciosAtivos.setText("9");
    }
    
    private void logout() {
        tokenManager.clearToken();
        sessionManager.logout();
        Toast.makeText(this, "Sessão encerrada", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
