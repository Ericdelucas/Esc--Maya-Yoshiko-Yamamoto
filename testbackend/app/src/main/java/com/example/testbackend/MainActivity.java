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

import com.example.testbackend.models.DailyProgressData;
import com.example.testbackend.models.DailyProgressResponse;
import com.example.testbackend.models.UserProfileResponse;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.AuthApi;
import com.example.testbackend.network.TaskApi;
import com.example.testbackend.services.NotificationService;
import com.example.testbackend.utils.Constants;
import com.example.testbackend.utils.LocaleHelper;
import com.example.testbackend.utils.TokenManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "PATIENT_DEBUG";
    private TextView tvUserInitial, tvGreeting, tvProgressValue;
    private ImageView ivUserPhoto;
    private MaterialCardView cardAccountAvatar;
    private ImageButton btnSettings;
    private CircularProgressIndicator progressWeekly;
    private TokenManager tokenManager;
    private TaskApi taskApi;
    private NotificationService notificationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // FORÇAR TEMA SALVO
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        int themeMode = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(themeMode);

        super.onCreate(savedInstanceState);
        
        tokenManager = new TokenManager(this);
        taskApi = ApiClient.getTaskClient().create(TaskApi.class);
        
        if (!tokenManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // 🔥 Verificar se usuário é paciente
        if (!isPatientUser()) {
            redirectToCorrectActivity();
            return;
        }

        setContentView(R.layout.activity_main);
        
        initViews();
        loadUserProfile();
        loadDailyProgress();
        setupNavigation();

        // 🔥 INICIAR SERVIÇO DE NOTIFICAÇÕES PARA PACIENTE
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
                Log.d(TAG, "Permissão de notificação concedida ao paciente");
            } else {
                Log.w(TAG, "Permissão de notificação negada pelo paciente");
            }
        }
    }

    private boolean isPatientUser() {
        String role = tokenManager.getUserRole();
        boolean isPatient = role == null || !(role.equalsIgnoreCase("professional") || role.equalsIgnoreCase("doctor") || role.equalsIgnoreCase("admin"));
        Log.d(TAG, "Verificando perfil: " + role + " -> isPatient: " + isPatient);
        return isPatient;
    }

    private void redirectToCorrectActivity() {
        Log.d(TAG, "Usuário não é paciente, redirecionando para ProfessionalMainActivity");
        Intent intent = new Intent(this, ProfessionalMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void initViews() {
        tvUserInitial = findViewById(R.id.tvUserInitial);
        tvGreeting = findViewById(R.id.tvGreeting);
        ivUserPhoto = findViewById(R.id.ivUserPhoto);
        cardAccountAvatar = findViewById(R.id.cardAccountAvatar);
        btnSettings = findViewById(R.id.btnSettings);
        
        // Inicializar componentes de progresso
        progressWeekly = findViewById(R.id.progressWeekly);
        tvProgressValue = findViewById(R.id.tvProgressValue);

        cardAccountAvatar.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        }
    }

    private void loadUserProfile() {
        AuthApi authApi = ApiClient.getAuthClient().create(AuthApi.class);
        authApi.getProfile(tokenManager.getAuthToken()).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    Log.e(TAG, "Erro ao carregar perfil na Home: " + response.code());
                    loadUserDataLocally();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Log.e(TAG, "Falha na requisição de perfil na Home", t);
                loadUserDataLocally();
            }
        });
    }

    private void loadDailyProgress() {
        String token = tokenManager.getAuthToken();
        if (token == null) return;
        
        taskApi.getDailyProgress(token).enqueue(new Callback<DailyProgressResponse>() {
            @Override
            public void onResponse(Call<DailyProgressResponse> call, Response<DailyProgressResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DailyProgressResponse progressResponse = response.body();
                    if (progressResponse.isSuccess()) {
                        updateProgressUI(progressResponse.getData());
                    }
                } else {
                    Log.e(TAG, "Erro ao carregar progresso: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<DailyProgressResponse> call, Throwable t) {
                Log.e(TAG, "Falha ao carregar progresso", t);
            }
        });
    }

    private void updateProgressUI(DailyProgressData progressData) {
        if (progressData == null || progressWeekly == null || tvProgressValue == null) return;
        
        int progressInt = progressData.getProgressPercentage().intValue();
        progressWeekly.setProgress(progressInt);
        tvProgressValue.setText(progressInt + "%");
        
        Log.d(TAG, "Progresso atualizado: " + progressData.getProgressFraction() + " (" + progressInt + "%)");
    }

    private void loadUserDataLocally() {
        String email = tokenManager.getUserEmail();
        if (email != null && !email.isEmpty()) {
            String initial = email.substring(0, 1).toUpperCase();
            tvUserInitial.setText(initial);
            
            String name = email.split("@")[0];
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            tvGreeting.setText("Olá, " + name + "!");
        }
    }

    private void updateUI(UserProfileResponse profile) {
        String name = profile.getFullName();
        if (name == null || name.isEmpty()) {
            name = profile.getEmail().split("@")[0];
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        tvGreeting.setText("Olá, " + name + "!");

        String initial = profile.getEmail().substring(0, 1).toUpperCase();
        tvUserInitial.setText(initial);

        if (profile.getProfilePhotoUrl() != null && !profile.getProfilePhotoUrl().isEmpty()) {
            String baseUrl = Constants.AUTH_BASE_URL;
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            String fullImageUrl = baseUrl + profile.getProfilePhotoUrl();
            
            Picasso.get()
                .load(fullImageUrl)
                .into(ivUserPhoto, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        ivUserPhoto.setVisibility(View.VISIBLE);
                        tvUserInitial.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        ivUserPhoto.setVisibility(View.GONE);
                        tvUserInitial.setVisibility(View.VISIBLE);
                    }
                });
        } else {
            ivUserPhoto.setVisibility(View.GONE);
            tvUserInitial.setVisibility(View.VISIBLE);
        }
    }

    private void setupNavigation() {
        // Botão Meus Exercícios
        findViewById(R.id.btnExercises).setOnClickListener(v -> startActivity(new Intent(this, ExerciseListActivity.class)));

        // Seção Gamificação (Apenas Ranking agora)
        findViewById(R.id.cardLeaderboard).setOnClickListener(v -> startActivity(new Intent(this, LeaderboardActivity.class)));

        // Seção Saúde
        findViewById(R.id.btnHealth).setOnClickListener(v -> startActivity(new Intent(this, HealthHubActivity.class)));

        // Card de Progresso
        findViewById(R.id.cardProgress).setOnClickListener(v -> {
            Toast.makeText(this, "Seu progresso diário", Toast.LENGTH_SHORT).show();
        });

        // Botão de Logout
        findViewById(R.id.btnLogout).setOnClickListener(v -> logout());

        // FAB Assistente IA
        findViewById(R.id.fabAssistant).setOnClickListener(v -> startActivity(new Intent(this, AssistantActivity.class)));
    }

    private void logout() {
        if (notificationService != null) notificationService.stopPolling();
        tokenManager.clearToken();
        Toast.makeText(this, "Sessão encerrada", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tokenManager.isLoggedIn()) {
            loadUserProfile();
            loadDailyProgress();
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
