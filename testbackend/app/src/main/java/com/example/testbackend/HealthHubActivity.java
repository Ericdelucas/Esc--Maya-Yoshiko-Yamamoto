package com.example.testbackend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.testbackend.utils.LocaleHelper;
import com.google.android.material.card.MaterialCardView;

public class HealthHubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_hub);

        setupToolbar();
        setupNavigation();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.health_tools);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupNavigation() {
        // IMC
        MaterialCardView cardImc = findViewById(R.id.cardImc);
        cardImc.setOnClickListener(v -> startActivity(new Intent(this, ImcCalculatorActivity.class)));

        // Gordura Corporal
        MaterialCardView cardBodyFat = findViewById(R.id.cardBodyFat);
        cardBodyFat.setOnClickListener(v -> startActivity(new Intent(this, BodyFatCalculatorActivity.class)));

        // Questionário de Saúde
        MaterialCardView cardQuestionnaire = findViewById(R.id.cardQuestionnaire);
        cardQuestionnaire.setOnClickListener(v -> startActivity(new Intent(this, HealthQuestionnaireActivity.class)));

        // Histórico
        MaterialCardView cardHistory = findViewById(R.id.cardHistory);
        cardHistory.setOnClickListener(v -> startActivity(new Intent(this, HealthHistoryActivity.class)));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
