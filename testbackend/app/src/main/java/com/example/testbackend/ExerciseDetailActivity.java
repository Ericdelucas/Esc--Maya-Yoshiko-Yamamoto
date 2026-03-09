package com.example.testbackend;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testbackend.models.Exercise;

/**
 * Tela de detalhes do exercício da Clínica Maya.
 * Demonstra uso de ConstraintLayout, TextView, ImageView, Button, Intent e Fragments.
 */
public class ExerciseDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        // Recebe o objeto via Intent (Uso de Intent Explícita)
        Exercise exercise = (Exercise) getIntent().getSerializableExtra("exercise_data");

        if (exercise != null) {
            setupUI(exercise);
            setupFragment(exercise);
        }

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupUI(Exercise exercise) {
        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvDescription = findViewById(R.id.tvDetailDescription);
        TextView tvFrequency = findViewById(R.id.tvFrequency);
        TextView tvWeeklyTotal = findViewById(R.id.tvWeeklyTotal);

        tvName.setText(exercise.getName());
        tvDescription.setText(exercise.getDescription());
        tvFrequency.setText("Frequência: " + exercise.getFrequencyPerWeek() + "x por semana");

        // Cálculo simples com dados numéricos conforme requisito
        int totalMinutes = exercise.getDurationMinutes() * exercise.getFrequencyPerWeek();
        tvWeeklyTotal.setText("Dedicação total semanal: " + totalMinutes + " minutos");
    }

    private void setupFragment(Exercise exercise) {
        // Uso de Fragments para exibir informações adicionais
        String tip = "Dica: Sempre realize este exercício em uma superfície firme.";
        if (exercise.getCategory().contains("Postural")) {
            tip = "Mantenha o olhar no horizonte para alinhar a cervical.";
        }
        
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, ExerciseTipsFragment.newInstance(tip))
                .commit();
    }
}