package com.example.testbackend;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.HealthToolsApi;
import com.example.testbackend.utils.LocaleHelper;
import com.example.testbackend.utils.TokenManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HealthQuestionnaireActivity extends AppCompatActivity {

    private RadioGroup rgSymptoms, rgAllergies, rgMeds, rgChronic, rgSurgery, rgHabits;
    private TextInputEditText etAllergyDetails;
    private MaterialButton btnSave;
    private ProgressBar progressBar;
    private HealthToolsApi healthApi;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_questionnaire);

        tokenManager = new TokenManager(this);
        healthApi = ApiClient.getAuthClient().create(HealthToolsApi.class);
        setupToolbar();
        initViews();
    }

    private void initViews() {
        rgSymptoms = findViewById(R.id.rgSymptoms);
        rgAllergies = findViewById(R.id.rgAllergies);
        rgMeds = findViewById(R.id.rgMeds);
        rgChronic = findViewById(R.id.rgChronic);
        rgSurgery = findViewById(R.id.rgSurgery);
        rgHabits = findViewById(R.id.rgHabits);
        etAllergyDetails = findViewById(R.id.etAllergyDetails);
        btnSave = findViewById(R.id.btnSaveQuestionnaire);
        progressBar = findViewById(R.id.progressBar);

        btnSave.setOnClickListener(v -> saveQuestionnaire());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.questionnaire_title);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void saveQuestionnaire() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        HealthToolsApi.QuestionnaireRequest request = new HealthToolsApi.QuestionnaireRequest();
        request.answers = new ArrayList<>();

        // Coletar respostas
        request.answers.add(createAnswer("symptoms", rgSymptoms.getCheckedRadioButtonId() == R.id.rbSymptomsYes ? "yes" : "no"));
        request.answers.add(createAnswer("allergies", rgAllergies.getCheckedRadioButtonId() == R.id.rbAllergiesYes ? "yes" : "no"));
        request.answers.add(createAnswer("meds", rgMeds.getCheckedRadioButtonId() == R.id.rbMedsYes ? "yes" : "no"));
        request.answers.add(createAnswer("chronic", rgChronic.getCheckedRadioButtonId() == R.id.rbChronicYes ? "yes" : "no"));
        request.answers.add(createAnswer("surgery", rgSurgery.getCheckedRadioButtonId() == R.id.rbSurgeryYes ? "yes" : "no"));
        
        // Mapear hábitos
        String habitValue = "excellent";
        int checkedHabit = rgHabits.getCheckedRadioButtonId();
        if (checkedHabit == R.id.rbHabitsRegular) habitValue = "regular";
        else if (checkedHabit == R.id.rbHabitsPoor) habitValue = "poor";
        request.answers.add(createAnswer("habits", habitValue));

        // Enviar para o servidor
        String token = tokenManager.getAuthToken();
        Log.d("HealthQuestionnaire", "Token obtido: " + (token != null ? "SIM" : "NULL"));
        Log.d("HealthQuestionnaire", "Token length: " + (token != null ? token.length() : 0));
        Log.d("HealthQuestionnaire", "Token preview: " + (token != null && token.length() > 20 ? token.substring(0, 20) + "..." : "INVALID"));
        
        if (token == null) {
            Toast.makeText(this, "Usuário não logado. Faça login novamente.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        healthApi.saveQuestionnaire(token, request).enqueue(new Callback<HealthToolsApi.QuestionnaireResponse>() {
            @Override
            public void onResponse(Call<HealthToolsApi.QuestionnaireResponse> call, Response<HealthToolsApi.QuestionnaireResponse> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    String message = "Salvo! Pontuação: " + response.body().score + " - Risco: " + response.body().risk_level;
                    Toast.makeText(HealthQuestionnaireActivity.this, message, Toast.LENGTH_LONG).show();
                    finish(); // Fecha a tela após salvar
                } else {
                    Toast.makeText(HealthQuestionnaireActivity.this, "Erro ao salvar no servidor", Toast.LENGTH_SHORT).show();
                    Log.e("HealthQuestionnaire", "Erro response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<HealthToolsApi.QuestionnaireResponse> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Toast.makeText(HealthQuestionnaireActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("HealthQuestionnaire", "Erro de conexão", t);
            }
        });
    }

    private HealthToolsApi.Answer createAnswer(String id, String value) {
        HealthToolsApi.Answer a = new HealthToolsApi.Answer();
        a.question_id = id;
        a.answer = value;
        return a;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
