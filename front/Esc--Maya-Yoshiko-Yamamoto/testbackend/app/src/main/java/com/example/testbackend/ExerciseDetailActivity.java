package com.example.testbackend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.testbackend.models.Exercise;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.ExerciseApi;
import com.example.testbackend.utils.LocaleHelper;
import com.google.android.material.button.MaterialButton;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseDetailActivity extends AppCompatActivity {

    private ImageView ivExercise;
    private TextView tvName, tvDescription, tvInstructions;
    private MaterialButton btnStartIA, btnVideo;
    private String token;
    private Exercise currentExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        initViews();
        setupToolbar();

        int exerciseId = getIntent().getIntExtra("exercise_id", -1);
        if (exerciseId != -1) {
            fetchExerciseDetails(exerciseId);
        } else {
            Toast.makeText(this, "Erro ao carregar exercício", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        ivExercise = findViewById(R.id.ivExerciseDetail);
        tvName = findViewById(R.id.tvDetailName);
        tvDescription = findViewById(R.id.tvDescription);
        tvInstructions = findViewById(R.id.tvInstructions);
        btnStartIA = findViewById(R.id.btnStartIA);
        btnVideo = findViewById(R.id.btnWatchVideo);

        SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
        token = prefs.getString("jwt_token", "");
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void fetchExerciseDetails(int id) {
        ExerciseApi api = ApiClient.getExerciseClient().create(ExerciseApi.class);
        api.getExerciseById("Bearer " + token, id).enqueue(new Callback<Exercise>() {
            @Override
            public void onResponse(Call<Exercise> call, Response<Exercise> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentExercise = response.body();
                    displayExercise(currentExercise);
                }
            }

            @Override
            public void onFailure(Call<Exercise> call, Throwable t) {
                Toast.makeText(ExerciseDetailActivity.this, "Falha ao carregar detalhes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayExercise(Exercise exercise) {
        tvName.setText(exercise.getTitle());
        tvDescription.setText(exercise.getDescription());
        tvInstructions.setText(exercise.getInstructions());

        // Placeholder para imagem
        ivExercise.setImageResource(android.R.drawable.ic_menu_today);

        if (exercise.getVideoUrl() != null && !exercise.getVideoUrl().isEmpty()) {
            btnVideo.setVisibility(View.VISIBLE);
            btnVideo.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(exercise.getVideoUrl()));
                startActivity(intent);
            });
        }

        btnStartIA.setOnClickListener(v -> {
            Intent intent = new Intent(this, IAWorkoutActivity.class);
            // Agora a classe Exercise implementa Serializable, então este método funciona!
            intent.putExtra("exercise_data", (Serializable) exercise);
            startActivity(intent);
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}