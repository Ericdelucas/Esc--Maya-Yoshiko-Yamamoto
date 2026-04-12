package com.example.testbackend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.R;
import com.example.testbackend.adapters.ExerciseAdapter;
import com.example.testbackend.models.Exercise;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.ExerciseApi;
import com.example.testbackend.utils.Constants;
import com.example.testbackend.utils.LocaleHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseListActivity extends AppCompatActivity {

    private static final String TAG = "EXERCISE_DEBUG";
    private RecyclerView rvExercises;
    private ExerciseAdapter adapter;
    private List<Exercise> exerciseList = new ArrayList<>();
    private FloatingActionButton fabAdd;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_exercise_list);

        setupToolbar();
        initViews();
        checkUserRole();
        fetchExercises();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.my_exercises);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        rvExercises = findViewById(R.id.rvExercises);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new ExerciseAdapter(exerciseList);
        rvExercises.setAdapter(adapter);

        fabAdd = findViewById(R.id.fabAddExercise);
        
        // FORÇADO PARA TESTE: O botão agora sempre aparecerá, independente do Role.
        fabAdd.setVisibility(View.VISIBLE);

        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, AddExerciseActivity.class));
        });
    }

    private void checkUserRole() {
        SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
        token = prefs.getString("jwt_token", "");
        String rawRole = prefs.getString("user_role", "Patient");

        Log.d(TAG, "Role Bruto logado: [" + rawRole + "]");
        
        // A lógica de visibilidade por Role foi movida para override manual no initViews()
        // para facilitar seus testes de cadastro.
    }

    private void fetchExercises() {
        Log.d(TAG, "Chamando exercícios de: " + Constants.EXERCISE_BASE_URL);
        
        ExerciseApi exerciseApi = ApiClient.getExerciseClient().create(ExerciseApi.class);
        exerciseApi.getExercises("Bearer " + token).enqueue(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    exerciseList.clear();
                    exerciseList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Erro de Resposta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Exercise>> call, Throwable t) {
                Log.e(TAG, "NETWORK FAILURE", t);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}