package com.example.testbackend;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.testbackend.models.DashboardStats;
import com.example.testbackend.models.UserProfileResponse;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.AuthApi;
import com.example.testbackend.services.NotificationService;
import com.example.testbackend.utils.Constants;
import com.example.testbackend.utils.LocaleHelper;
import com.example.testbackend.utils.TokenManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfessionalMainActivity extends AppCompatActivity {

    private static final String TAG = "PROFESSIONAL_DEBUG";
    private TextView tvUserInitial, tvGreeting;
    private TextView tvTotalPacientes, tvConsultasHoje;
    private ImageView ivUserPhoto;
    private MaterialCardView cardAccountAvatar;
    private ImageButton btnSettings;
    private TokenManager tokenManager;
    private NotificationService notificationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // FORÇAR TEMA SALVO
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        int themeMode = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(themeMode);

        super.onCreate(savedInstanceState);
        
        tokenManager = new TokenManager(this);
        
        if (!tokenManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main_professional);
        
        initViews();
        loadUserProfile();
        loadDashboardData();
        setupNavigation();
        
        // 🔥 INICIAR SERVIÇO DE NOTIFICAÇÕES
        requestNotificationPermission();
        notificationService = new NotificationService(this);
        notificationService.startPolling();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                
                ActivityCompat.requestPermissions(
                    this, 
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 
                    100
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permissão de notificação concedida");
            } else {
                Log.w(TAG, "Permissão de notificação negada");
            }
        }
    }

    private void initViews() {
        tvUserInitial = findViewById(R.id.tvUserInitial);
        tvGreeting = findViewById(R.id.tvGreeting);
        ivUserPhoto = findViewById(R.id.ivUserPhoto);
        cardAccountAvatar = findViewById(R.id.cardAccountAvatar);
        btnSettings = findViewById(R.id.btnSettings);
        
        // IDs do Painel de Visão Geral conforme o seu XML restaurado
        tvTotalPacientes = findViewById(R.id.tvTotalPacientes);
        tvConsultasHoje = findViewById(R.id.tvConsultasHoje);

        if (cardAccountAvatar != null) {
            cardAccountAvatar.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }
        
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        }
    }

    private void loadDashboardData() {
        AuthApi authApi = ApiClient.getAuthClient().create(AuthApi.class);
        String token = tokenManager.getAuthToken();
        
        authApi.getDashboardStats(token).enqueue(new Callback<DashboardStats>() {
            @Override
            public void onResponse(Call<DashboardStats> call, Response<DashboardStats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DashboardStats data = response.body();
                    updateDashboardUI(data);
                } else {
                    Log.e(TAG, "Erro ao carregar dashboard: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DashboardStats> call, Throwable t) {
                Log.e(TAG, "Falha na requisição do dashboard", t);
            }
        });
    }

    private void updateDashboardUI(DashboardStats data) {
        if (data == null) return;
        
        if (tvTotalPacientes != null) tvTotalPacientes.setText(String.valueOf(data.getTotalPatients()));
        if (tvConsultasHoje != null) tvConsultasHoje.setText(String.valueOf(data.getAppointmentsToday()));
        
        Log.d(TAG, "Dashboard atualizado com dados reais do banco.");
    }

    private void loadUserProfile() {
        AuthApi authApi = ApiClient.getAuthClient().create(AuthApi.class);
        authApi.getProfile(tokenManager.getAuthToken()).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    loadUserDataLocally();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                loadUserDataLocally();
            }
        });
    }

    private void loadUserDataLocally() {
        String email = tokenManager.getUserEmail();
        if (email != null && !email.isEmpty()) {
            String initial = email.substring(0, 1).toUpperCase();
            if (tvUserInitial != null) tvUserInitial.setText(initial);
            
            String name = email.split("@")[0];
            if (tvGreeting != null) {
                tvGreeting.setText("Olá, Prof. " + name.substring(0, 1).toUpperCase() + name.substring(1));
            }
        }
    }

    private void updateUI(UserProfileResponse profile) {
        String name = profile.getFullName();
        if (name == null || name.isEmpty()) {
            name = profile.getEmail().split("@")[0];
        }
        if (tvGreeting != null) tvGreeting.setText("Olá, Prof. " + name);

        String initial = profile.getEmail().substring(0, 1).toUpperCase();
        if (tvUserInitial != null) tvUserInitial.setText(initial);

        if (profile.getProfilePhotoUrl() != null && !profile.getProfilePhotoUrl().isEmpty() && ivUserPhoto != null) {
            String baseUrl = Constants.AUTH_BASE_URL;
            if (baseUrl.endsWith("/")) baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            String fullImageUrl = baseUrl + profile.getProfilePhotoUrl();
            Picasso.get().load(fullImageUrl).into(ivUserPhoto, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    ivUserPhoto.setVisibility(View.VISIBLE);
                    if (tvUserInitial != null) tvUserInitial.setVisibility(View.GONE);
                }
                @Override
                public void onError(Exception e) {
                    ivUserPhoto.setVisibility(View.GONE);
                    if (tvUserInitial != null) tvUserInitial.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void setupNavigation() {
        // IDs Reais conforme o layout XML restaurado
        
        View cardPatients = findViewById(R.id.cardPatients);
        if (cardPatients != null) {
            cardPatients.setOnClickListener(v -> startActivity(new Intent(this, PatientsListActivity.class)));
        }

        View cardAddPatient = findViewById(R.id.cardAddPatient);
        if (cardAddPatient != null) {
            cardAddPatient.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        }

        View cardReports = findViewById(R.id.cardReports);
        if (cardReports != null) {
            cardReports.setOnClickListener(v -> startActivity(new Intent(this, PatientReportsActivity.class)));
        }

        View cardExercises = findViewById(R.id.cardExercises);
        if (cardExercises != null) {
            cardExercises.setOnClickListener(v -> startActivity(new Intent(this, ExerciseListActivity.class)));
        }

        // Botões de Ações Rápidas (IDs corretos do XML)
        View btnProfile = findViewById(R.id.btnProfile);
        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }

        View btnCalendar = findViewById(R.id.btnCalendar);
        if (btnCalendar != null) {
            btnCalendar.setOnClickListener(v -> startActivity(new Intent(this, CalendarActivity.class)));
        }
        
        // FAB Logout
        View fabLogout = findViewById(R.id.fabLogout);
        if (fabLogout != null) {
            fabLogout.setOnClickListener(v -> logout());
        }

        // FAB Add Task (+): Leva para a criação de tarefas
        View fabAddTask = findViewById(R.id.fabAddTask);
        if (fabAddTask != null) {
            fabAddTask.setOnClickListener(v -> {
                startActivity(new Intent(this, CreateTaskActivity.class));
            });
        }
    }

    private void logout() {
        if (notificationService != null) notificationService.stopPolling();
        tokenManager.clearToken();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tokenManager.isLoggedIn()) {
            loadDashboardData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (notificationService != null) {
            notificationService.stopPolling();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
