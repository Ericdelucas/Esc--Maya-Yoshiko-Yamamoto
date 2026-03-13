package com.example.testbackend;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class HealthHistoryActivity extends AppCompatActivity {

    private TextInputEditText etAge, etWeight, etHeight, etMedications, etAllergies, etObservations;
    private MaterialButton btnSave;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_history);

        prefs = getSharedPreferences("health_history_prefs", MODE_PRIVATE);

        setupToolbar();
        initViews();
        loadHealthData();

        btnSave.setOnClickListener(v -> saveHealthData());
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
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etMedications = findViewById(R.id.etMedications);
        etAllergies = findViewById(R.id.etAllergies);
        etObservations = findViewById(R.id.etObservations);
        btnSave = findViewById(R.id.btnSave);
    }

    private void loadHealthData() {
        etAge.setText(prefs.getString("age", ""));
        etWeight.setText(prefs.getString("weight", ""));
        etHeight.setText(prefs.getString("height", ""));
        etMedications.setText(prefs.getString("medications", ""));
        etAllergies.setText(prefs.getString("allergies", ""));
        etObservations.setText(prefs.getString("observations", ""));
    }

    private void saveHealthData() {
        SharedPreferences.Editor editor = prefs.edit();
        
        editor.putString("age", etAge.getText().toString());
        editor.putString("weight", etWeight.getText().toString());
        editor.putString("height", etHeight.getText().toString());
        editor.putString("medications", etMedications.getText().toString());
        editor.putString("allergies", etAllergies.getText().toString());
        editor.putString("observations", etObservations.getText().toString());
        
        editor.apply();
        Toast.makeText(this, "Histórico salvo com sucesso!", Toast.LENGTH_SHORT).show();
        finish();
    }
}