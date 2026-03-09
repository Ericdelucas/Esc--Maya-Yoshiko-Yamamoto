package com.example.testbackend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Simple Session Check
        SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        setupButtons();
    }

    private void setupButtons() {
        Button btnExercises = findViewById(R.id.btnExercises);
        Button btnStartIA = findViewById(R.id.btnStartIA);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnExercises.setOnClickListener(v -> {
            startActivity(new Intent(this, ExerciseListActivity.class));
        });

        btnStartIA.setOnClickListener(v -> {
            startActivity(new Intent(this, IAWorkoutActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            logout();
        });
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
        prefs.edit().remove("jwt_token").apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}