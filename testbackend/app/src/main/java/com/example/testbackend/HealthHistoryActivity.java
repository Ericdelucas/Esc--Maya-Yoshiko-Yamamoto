package com.example.testbackend;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.testbackend.models.HealthMetricResponse;
import com.example.testbackend.models.UserProfileResponse;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.AuthApi;
import com.example.testbackend.network.HealthApi;
import com.example.testbackend.utils.Constants;
import com.example.testbackend.utils.LocaleHelper;
import com.example.testbackend.utils.TokenManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HealthHistoryActivity extends AppCompatActivity {

    private static final String TAG = "HEALTH_DEBUG";
    private TextInputEditText etAge, etWeight, etHeight, etMedications, etAllergies, etObservations;
    private MaterialButton btnSave;
    private HealthApi healthApi;
    private TokenManager tokenManager;
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_history);

        tokenManager = new TokenManager(this);
        setupToolbar();
        initViews();
        setupAPI();
        loadUserProfile(); // Carrega o ID real do usuário logado
        setupListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.history_title);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etMedications = findViewById(R.id.etMedications);
        etAllergies = findViewById(R.id.etAllergies);
        etObservations = findViewById(R.id.etObservations);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.HEALTH_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        healthApi = retrofit.create(HealthApi.class);
    }

    private void loadUserProfile() {
        AuthApi authApi = ApiClient.getAuthClient().create(AuthApi.class);
        authApi.getProfile(tokenManager.getAuthToken()).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUserId = response.body().getId();
                    Log.d(TAG, "ID do usuário carregado: " + currentUserId);
                } else {
                    Log.e(TAG, "Falha ao carregar perfil: " + response.code());
                    Toast.makeText(HealthHistoryActivity.this, "Erro ao identificar usuário. Tente novamente.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Log.e(TAG, "Erro de rede ao carregar perfil", t);
                Toast.makeText(HealthHistoryActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> {
            if (currentUserId == -1) {
                Toast.makeText(this, "Aguarde o carregamento do perfil...", Toast.LENGTH_SHORT).show();
                return;
            }
            saveHealthData();
        });
    }

    private void saveHealthData() {
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();

        if (weightStr.isEmpty() || heightStr.isEmpty()) {
            Toast.makeText(this, "Peso e altura são obrigatórios para calcular o IMC", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double weight = Double.parseDouble(weightStr);
            double height = Double.parseDouble(heightStr);

            btnSave.setEnabled(false);
            btnSave.setText("Salvando...");

            // 1. Salvar IMC e Metas de Saúde no health-service
            healthApi.calculateIMC(currentUserId, weight, height).enqueue(new Callback<HealthMetricResponse>() {
                @Override
                public void onResponse(Call<HealthMetricResponse> call, Response<HealthMetricResponse> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Métricas salvas. Iniciando questionário...");
                        saveQuestionnaire(currentUserId);
                    } else {
                        btnSave.setEnabled(true);
                        btnSave.setText(R.string.save_history_btn);
                        Log.e(TAG, "Erro ao salvar métricas: " + response.code());
                        Toast.makeText(HealthHistoryActivity.this, "Erro ao salvar métricas no servidor", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<HealthMetricResponse> call, Throwable t) {
                    btnSave.setEnabled(true);
                    btnSave.setText(R.string.save_history_btn);
                    Log.e(TAG, "Falha na rede (Metrics)", t);
                    Toast.makeText(HealthHistoryActivity.this, "Falha de conexão com o serviço de saúde", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Peso ou altura inválidos", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveQuestionnaire(int userId) {
        Map<String, Object> medicalHistory = new HashMap<>();
        medicalHistory.put("observations", etObservations.getText().toString());
        medicalHistory.put("age", etAge.getText().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("medical_history", medicalHistory);
        data.put("medications", etMedications.getText().toString().split(","));
        data.put("allergies", etAllergies.getText().toString().split(","));
        data.put("habits", new HashMap<>()); // Pode ser expandido futuramente

        healthApi.saveQuestionnaire(userId, data).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                btnSave.setEnabled(true);
                btnSave.setText(R.string.save_history_btn);
                
                if (response.isSuccessful()) {
                    Toast.makeText(HealthHistoryActivity.this, "Tudo salvo com sucesso!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Log.e(TAG, "Erro ao salvar questionário: " + response.code());
                    Toast.makeText(HealthHistoryActivity.this, "Erro ao salvar dados médicos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                btnSave.setEnabled(true);
                btnSave.setText(R.string.save_history_btn);
                Log.e(TAG, "Falha na rede (Questionnaire)", t);
                Toast.makeText(HealthHistoryActivity.this, "Erro de rede ao salvar questionário", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
