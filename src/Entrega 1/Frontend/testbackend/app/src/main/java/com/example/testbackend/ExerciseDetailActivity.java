package com.example.testbackend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.testbackend.models.Exercise;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

public class ExerciseDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        // Recebe o objeto do exercício
        Exercise exercise = (Exercise) getIntent().getSerializableExtra("exercise_data");

        // Configura a Toolbar (Substitui o antigo btnBack)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        // Ação de voltar
        toolbar.setNavigationOnClickListener(v -> finish());

        if (exercise != null) {
            setupUI(exercise);
        }

        // Configura o botão de iniciar exercício com IA
        MaterialButton btnStartIA = findViewById(R.id.btnStartIA);
        btnStartIA.setOnClickListener(v -> {
            Intent intent = new Intent(this, IAWorkoutActivity.class);
            intent.putExtra("exercise_data", exercise);
            startActivity(intent);
        });
    }

    private void setupUI(Exercise exercise) {
        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvDescription = findViewById(R.id.tvDescription);
        Chip chipCategory = findViewById(R.id.chipCategory);

        tvName.setText(exercise.getName());
        tvDescription.setText(exercise.getDescription());
        
        if (exercise.getCategory() != null) {
            chipCategory.setText(exercise.getCategory());
        }
    }
}