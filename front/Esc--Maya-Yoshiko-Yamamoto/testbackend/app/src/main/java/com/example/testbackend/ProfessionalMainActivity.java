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

import com.example.testbackend.models.DashboardStats;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.AuthApi;
import com.example.testbackend.storage.SessionManager;
import com.example.testbackend.utils.TokenManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfessionalMainActivity extends AppCompatActivity {
    
    private static final String TAG = "PROF_DEBUG";
    private TokenManager tokenManager;
    private SessionManager sessionManager;
    
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
        
        if (!isProfessionalUser()) {
            redirectToCorrectActivity();
            return;
        }

        setContentView(R.layout.activity_main_professional);
        
        setupViews();
        setupClickListeners();
        loadUserInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 🔥 TAREFA 3: Sempre buscar dados atualizados ao voltar para a tela
        carregarDadosPainel();
    }
    
    private boolean isProfessionalUser() {
        String role = tokenManager.getUserRole();
        if (role == null) return false;
        
        String normalizedRole = role.trim().toLowerCase();
        return normalizedRole.contains("prof") || 
               normalizedRole.contains("doc") || 
               normalizedRole.contains("admin");
    }
    
    private void redirectToCorrectActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void setupViews() {
        tvGreeting = findViewById(R.id.tvGreeting);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvUserInitial = findViewById(R.id.tvUserInitial);
        ivUserPhoto = findViewById(R.id.ivUserPhoto);
        cardAccountAvatar = findViewById(R.id.cardAccountAvatar);
        btnSettings = findViewById(R.id.btnSettings);
        
        tvTotalPacientes = findViewById(R.id.tvTotalPacientes);
        tvConsultasHoje = findViewById(R.id.tvConsultasHoje);
        tvExerciciosAtivos = findViewById(R.id.tvExerciciosAtivos);
        
        cardPatients = findViewById(R.id.cardPatients);
        cardAddPatient = findViewById(R.id.cardAddPatient);
        cardExercises = findViewById(R.id.cardExercises);
        cardReports = findViewById(R.id.cardReports);
        
        btnProfile = findViewById(R.id.btnProfile);
        btnCalendar = findViewById(R.id.btnCalendar);
        fabLogout = findViewById(R.id.fabLogout);
    }

    private void carregarDadosPainel() {
        // 🔥 TAREFA 2: Substituir dados ilusórios por chamada real à API
        Log.d(TAG, "Carregando estatísticas reais da API...");
        
        AuthApi authApi = ApiClient.getAuthClient().create(AuthApi.class);
        authApi.getDashboardStats(tokenManager.getAuthToken()).enqueue(new Callback<DashboardStats>() {
            @Override
            public void onResponse(Call<DashboardStats> call, Response<DashboardStats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DashboardStats stats = response.body();
                    
                    // ✅ Atualizando com dados reais do banco
                    if (tvTotalPacientes != null) tvTotalPacientes.setText(String.valueOf(stats.getTotalPatients()));
                    if (tvConsultasHoje != null) tvConsultasHoje.setText(String.valueOf(stats.getAppointmentsToday()));
                    if (tvExerciciosAtivos != null) tvExerciciosAtivos.setText(String.valueOf(stats.getActiveExercises()));
                    
                    Log.d(TAG, "Estatísticas atualizadas: " + stats.getTotalPatients() + " pacientes.");
                } else {
                    Log.e(TAG, "Erro ao buscar estatísticas: " + response.code());
                    // Fallback para zero se der erro, nunca usar dados mock
                    if (tvTotalPacientes != null) tvTotalPacientes.setText("0");
                }
            }

            @Override
            public void onFailure(Call<DashboardStats> call, Throwable t) {
                Log.e(TAG, "Falha na conexão ao buscar estatísticas", t);
                if (tvTotalPacientes != null) tvTotalPacientes.setText("-");
            }
        });
    }
    
    private void setupClickListeners() {
        if (cardAccountAvatar != null) {
            cardAccountAvatar.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        }
        if (cardPatients != null) {
            cardPatients.setOnClickListener(v -> startActivity(new Intent(this, PatientsListActivity.class)));
        }
        if (cardExercises != null) {
            cardExercises.setOnClickListener(v -> startActivity(new Intent(this, ExerciseListActivity.class)));
        }
        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }
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
    
    private void logout() {
        tokenManager.clearToken();
        sessionManager.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
