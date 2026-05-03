package com.example.testbackend;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.HealthToolsApi;
import com.example.testbackend.utils.LocaleHelper;
import com.example.testbackend.utils.TokenManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImcCalculatorActivity extends AppCompatActivity {

    private TextInputEditText etWeight, etHeight;
    private MaterialCardView cardResult;
    private TextView tvImcValue, tvImcCategory;
    private ProgressBar progressBar;
    private HealthToolsApi healthApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imc);

        // ✅ USANDO O CLIENTE DE SAÚDE CORRETO
        healthApi = ApiClient.getHealthClient().create(HealthToolsApi.class);
        setupToolbar();
        initViews();
    }

    private void initViews() {
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        cardResult = findViewById(R.id.cardResult);
        tvImcValue = findViewById(R.id.tvImcValue);
        tvImcCategory = findViewById(R.id.tvImcCategory);
        progressBar = findViewById(R.id.progressBar);

        MaterialButton btnCalculate = findViewById(R.id.btnCalculate);
        btnCalculate.setOnClickListener(v -> calculateImcOnServer());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.bmi_calculator);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void calculateImcOnServer() {
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();

        if (weightStr.isEmpty() || heightStr.isEmpty()) {
            Toast.makeText(this, "Preencha peso e altura", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            float weight = Float.parseFloat(weightStr);
            float height = Float.parseFloat(heightStr);

            if (height > 3) height = height / 100;

            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            btnCalculateEnabled(false);

            HealthToolsApi.BMICalculationRequest request = new HealthToolsApi.BMICalculationRequest(height, weight);

            // 🔥 USANDO ENDPOINT DE TESTE PARA EVITAR ERRO DE TOKEN (JWT)
            healthApi.calculateBMITest(request).enqueue(new Callback<HealthToolsApi.BMIResponse>() {
                @Override
                public void onResponse(Call<HealthToolsApi.BMIResponse> call, Response<HealthToolsApi.BMIResponse> response) {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    btnCalculateEnabled(true);

                    if (response.isSuccessful() && response.body() != null && response.body().success) {
                        HealthToolsApi.BMIData data = response.body().data;
                        tvImcValue.setText(String.format("%.1f", data.bmi));
                        tvImcCategory.setText(data.category);
                        cardResult.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(ImcCalculatorActivity.this, "Erro ao calcular no servidor", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<HealthToolsApi.BMIResponse> call, Throwable t) {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    btnCalculateEnabled(true);
                    Log.e("IMC_API", "Falha na conexão", t);
                    Toast.makeText(ImcCalculatorActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valores inválidos", Toast.LENGTH_SHORT).show();
        }
    }

    private void btnCalculateEnabled(boolean enabled) {
        MaterialButton btn = findViewById(R.id.btnCalculate);
        if (btn != null) btn.setEnabled(enabled);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
