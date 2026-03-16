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

        MaterialCardView cardImc = findViewById(R.id.cardImc);
        MaterialCardView cardBodyFat = findViewById(R.id.cardBodyFat);
        MaterialCardView cardQuestionnaire = findViewById(R.id.cardQuestionnaire);
        MaterialCardView cardHistory = findViewById(R.id.cardHistory);

        cardImc.setOnClickListener(v -> startActivity(new Intent(this, ImcCalculatorActivity.class)));
        cardBodyFat.setOnClickListener(v -> startActivity(new Intent(this, BodyFatCalculatorActivity.class)));
        cardQuestionnaire.setOnClickListener(v -> startActivity(new Intent(this, HealthQuestionnaireActivity.class)));
        cardHistory.setOnClickListener(v -> startActivity(new Intent(this, HealthHistoryActivity.class)));
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.health_tools);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}