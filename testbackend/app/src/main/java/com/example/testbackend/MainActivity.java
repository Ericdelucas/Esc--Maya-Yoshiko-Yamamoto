package com.example.testbackend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.testbackend.models.UserProfileResponse;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.AuthApi;
import com.example.testbackend.utils.Constants;
import com.example.testbackend.utils.LocaleHelper;
import com.example.testbackend.utils.TokenManager;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_DEBUG";
    private TextView tvUserInitial, tvGreeting;
    private ImageView ivUserPhoto;
    private MaterialCardView cardAccountAvatar, cardProfessionalExams;
    private ImageButton btnSettings;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // FORÇAR TEMA SALVO OU MODO CLARO ANTES DE TUDO
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

        setContentView(R.layout.activity_main);
        
        initViews();
        loadUserProfile();
        setupNavigation();
    }

    private void initViews() {
        tvUserInitial = findViewById(R.id.tvUserInitial);
        tvGreeting = findViewById(R.id.tvGreeting);
        ivUserPhoto = findViewById(R.id.ivUserPhoto);
        cardAccountAvatar = findViewById(R.id.cardAccountAvatar);
        btnSettings = findViewById(R.id.btnSettings);
        cardProfessionalExams = findViewById(R.id.cardProfessionalExams);

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

        setupProfessionalFeatures(profile.getRole());
    }

    private void setupProfessionalFeatures(String role) {
        if (role != null && (role.equalsIgnoreCase("Professional") || role.equalsIgnoreCase("Doctor") || role.equalsIgnoreCase("admin"))) {
            if (cardProfessionalExams != null) {
                cardProfessionalExams.setVisibility(View.VISIBLE);
                cardProfessionalExams.setOnClickListener(v -> {
                    Toast.makeText(this, "Abrindo Gestão de Exames...", Toast.LENGTH_SHORT).show();
                });
            }
        } else {
            if (cardProfessionalExams != null) {
                cardProfessionalExams.setVisibility(View.GONE);
            }
        }
    }

    private void setupNavigation() {
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
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
