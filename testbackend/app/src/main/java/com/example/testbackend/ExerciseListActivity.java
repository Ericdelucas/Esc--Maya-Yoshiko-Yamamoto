package com.example.testbackend;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.adapters.ExerciseAdapter;
import com.example.testbackend.models.Exercise;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.ExerciseApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseListActivity extends AppCompatActivity {

    private RecyclerView rvExercises;
    private ExerciseAdapter adapter;
    private List<Exercise> exerciseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        rvExercises = findViewById(R.id.rvExercises);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new ExerciseAdapter(exerciseList);
        rvExercises.setAdapter(adapter);

        fetchExercises();
    }

    private void fetchExercises() {
        SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", "");

        ExerciseApi exerciseApi = ApiClient.getExerciseClient().create(ExerciseApi.class);
        // Assuming the backend expects "Bearer <token>"
        exerciseApi.getExercises("Bearer " + token).enqueue(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    exerciseList.clear();
                    exerciseList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ExerciseListActivity.this, "Erro ao carregar exercícios: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Exercise>> call, Throwable t) {
                Log.e("ExerciseList", "Erro de rede", t);
                Toast.makeText(ExerciseListActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}