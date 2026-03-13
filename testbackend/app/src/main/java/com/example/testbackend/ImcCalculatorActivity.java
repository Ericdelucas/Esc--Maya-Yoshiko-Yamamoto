package com.example.testbackend;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class ImcCalculatorActivity extends AppCompatActivity {

    private TextInputEditText etWeight, etHeight;
    private MaterialButton btnCalculate;
    private MaterialCardView cardResult;
    private TextView tvImcValue, tvImcCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imc);

        setupToolbar();
        initViews();

        btnCalculate.setOnClickListener(v -> calculateImc());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        btnCalculate = findViewById(R.id.btnCalculate);
        cardResult = findViewById(R.id.cardResult);
        tvImcValue = findViewById(R.id.tvImcValue);
        tvImcCategory = findViewById(R.id.tvImcCategory);
    }

    private void calculateImc() {
        String weightStr = etWeight.getText().toString();
        String heightStr = etHeight.getText().toString();

        if (weightStr.isEmpty() || heightStr.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double weight = Double.parseDouble(weightStr);
            double height = Double.parseDouble(heightStr);

            if (height <= 0) {
                Toast.makeText(this, "Altura deve ser maior que zero", Toast.LENGTH_SHORT).show();
                return;
            }

            double imc = weight / (height * height);
            displayResult(imc);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valores inválidos", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayResult(double imc) {
        tvImcValue.setText(String.format(Locale.getDefault(), "%.1f", imc));
        
        String category;
        int colorRes;

        if (imc < 18.5) {
            category = "Abaixo do peso";
            colorRes = android.R.color.holo_orange_light;
        } else if (imc < 25) {
            category = "Peso normal";
            colorRes = android.R.color.holo_green_dark;
        } else if (imc < 30) {
            category = "Sobrepeso";
            colorRes = android.R.color.holo_orange_dark;
        } else {
            category = "Obesidade";
            colorRes = android.R.color.holo_red_dark;
        }

        tvImcCategory.setText(category);
        tvImcValue.setTextColor(getResources().getColor(colorRes));
        cardResult.setVisibility(View.VISIBLE);
    }
}