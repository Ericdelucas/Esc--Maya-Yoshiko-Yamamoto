package com.example.testbackend;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
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

public class BodyFatCalculatorActivity extends AppCompatActivity {

    private TextInputEditText etWeight, etHeight, etAge;
    private RadioGroup rgGender;
    private MaterialCardView cardResult;
    private TextView tvBodyFatValue, tvBodyFatCategory;
    private ProgressBar progressBar;
    private HealthToolsApi healthApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_fat_calculator);

        // ✅ USANDO O CLIENTE DE SAÚDE CORRETO
        healthApi = ApiClient.getHealthClient().create(HealthToolsApi.class);
        setupToolbar();
        initViews();
    }

    private void initViews() {
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etAge = findViewById(R.id.etAge);
        rgGender = findViewById(R.id.rgGender);
        cardResult = findViewById(R.id.cardResult);
        tvBodyFatValue = findViewById(R.id.tvBodyFatValue);
        tvBodyFatCategory = findViewById(R.id.tvBodyFatCategory);
        progressBar = findViewById(R.id.progressBar);

        MaterialButton btnCalculate = findViewById(R.id.btnCalculate);
        btnCalculate.setOnClickListener(v -> calculateBodyFatOnServer());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Gordura Corporal");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void calculateBodyFatOnServer() {
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();

        if (weightStr.isEmpty() || heightStr.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            float weight = Float.parseFloat(weightStr);
            float height = Float.parseFloat(heightStr);
            int age = Integer.parseInt(ageStr);
            String gender = rgGender.getCheckedRadioButtonId() == R.id.rbMale ? "M" : "F";

            if (height > 3) height = height / 100;

            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            
            HealthToolsApi.BodyFatCalculationRequest request = new HealthToolsApi.BodyFatCalculationRequest();
            request.weight = weight;
            request.height = height;
            request.age = age;
            request.gender = gender;

            // 🔥 USANDO ENDPOINT DE TESTE PARA EVITAR ERRO DE TOKEN (JWT)
            healthApi.calculateBodyFatTest(request).enqueue(new Callback<HealthToolsApi.BodyFatResponse>() {
                @Override
                public void onResponse(Call<HealthToolsApi.BodyFatResponse> call, Response<HealthToolsApi.BodyFatResponse> response) {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null && response.body().success) {
                        HealthToolsApi.BodyFatData data = response.body().data;
                        tvBodyFatValue.setText(String.format("%.1f%%", data.body_fat_percentage));
                        tvBodyFatCategory.setText(data.category);
                        cardResult.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(BodyFatCalculatorActivity.this, "Erro no cálculo", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<HealthToolsApi.BodyFatResponse> call, Throwable t) {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Log.e("FAT_API", "Erro de conexão", t);
                    Toast.makeText(BodyFatCalculatorActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valores inválidos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
