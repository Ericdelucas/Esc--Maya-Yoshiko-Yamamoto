package com.example.testbackend;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class BodyFatCalculatorActivity extends AppCompatActivity {

    private TextInputEditText etWeight, etHeight, etAge;
    private RadioGroup rgGender;
    private MaterialButton btnCalculate;
    private MaterialCardView cardResult;
    private TextView tvBodyFatValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_fat_calculator);

        setupToolbar();
        initViews();

        btnCalculate.setOnClickListener(v -> calculateBodyFat());
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
        etAge = findViewById(R.id.etAge);
        rgGender = findViewById(R.id.rgGender);
        btnCalculate = findViewById(R.id.btnCalculate);
        cardResult = findViewById(R.id.cardResult);
        tvBodyFatValue = findViewById(R.id.tvBodyFatValue);
    }

    private void calculateBodyFat() {
        String weightStr = etWeight.getText().toString();
        String heightStr = etHeight.getText().toString();
        String ageStr = etAge.getText().toString();

        if (weightStr.isEmpty() || heightStr.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double weight = Double.parseDouble(weightStr);
            double height = Double.parseDouble(heightStr);
            int age = Integer.parseInt(ageStr);
            
            // 1 para Homem, 0 para Mulher (Fórmula de Deurenberg)
            int genderFactor = (rgGender.getCheckedRadioButtonId() == R.id.rbMale) ? 1 : 0;

            if (height <= 0 || age <= 0) {
                Toast.makeText(this, "Valores inválidos", Toast.LENGTH_SHORT).show();
                return;
            }

            double imc = weight / (height * height);
            
            // Fórmula de Deurenberg: 1.2 * IMC + 0.23 * idade - 10.8 * sexo - 5.4
            double bodyFat = (1.20 * imc) + (0.23 * age) - (10.8 * genderFactor) - 5.4;

            displayResult(bodyFat);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Erro no formato dos números", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayResult(double bodyFat) {
        tvBodyFatValue.setText(String.format(Locale.getDefault(), "%.1f%%", bodyFat));
        cardResult.setVisibility(View.VISIBLE);
    }
}