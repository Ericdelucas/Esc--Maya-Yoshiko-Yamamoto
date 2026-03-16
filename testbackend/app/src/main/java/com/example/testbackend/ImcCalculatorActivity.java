package com.example.testbackend;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.testbackend.utils.LocaleHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class ImcCalculatorActivity extends AppCompatActivity {

    private TextInputEditText etWeight, etHeight;
    private MaterialCardView cardResult;
    private TextView tvImcValue, tvImcCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imc);

        setupToolbar();

        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        cardResult = findViewById(R.id.cardResult);
        tvImcValue = findViewById(R.id.tvImcValue);
        tvImcCategory = findViewById(R.id.tvImcCategory);

        MaterialButton btnCalculate = findViewById(R.id.btnCalculate);
        btnCalculate.setOnClickListener(v -> calculateImc());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.bmi_calculator);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void calculateImc() {
        String weightStr = etWeight.getText().toString();
        String heightStr = etHeight.getText().toString();

        if (weightStr.isEmpty() || heightStr.isEmpty()) return;

        float weight = Float.parseFloat(weightStr);
        float height = Float.parseFloat(heightStr);
        float imc = weight / (height * height);

        tvImcValue.setText(String.format("%.1f", imc));
        
        String category;
        if (imc < 18.5) {
            category = getString(R.string.bmi_underweight);
        } else if (imc < 25) {
            category = getString(R.string.bmi_normal);
        } else if (imc < 30) {
            category = getString(R.string.bmi_overweight);
        } else {
            category = getString(R.string.bmi_obese);
        }

        tvImcCategory.setText(category);
        cardResult.setVisibility(View.VISIBLE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}