package com.example.testbackend;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.adapters.ExerciseManagementAdapter;
import com.example.testbackend.models.DeleteExerciseResponse;
import com.example.testbackend.models.ProfessionalExercisesResponse;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.TaskApi;
import com.example.testbackend.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseManagementActivity extends AppCompatActivity {
    
    private static final String TAG = "EXERCISE_MANAGEMENT";
    
    private RecyclerView recyclerView;
    private ExerciseManagementAdapter adapter;
    private TaskApi taskApi;
    private TokenManager tokenManager;
    private TextView tvEmptyState;
    private List<ProfessionalExercisesResponse.ExerciseItem> exerciseList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_management);
        
        // Inicializar
        tokenManager = new TokenManager(this);
        taskApi = ApiClient.getTaskClient().create(TaskApi.class);
        
        initViews();
        loadExercises();
    }
    
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewExercises);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        
        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Botão voltar
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
    
    private void loadExercises() {
        String token = tokenManager.getAuthToken();
        if (token == null) {
            Toast.makeText(this, "Erro: usuário não logado", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Log.d(TAG, "Carregando exercícios para gerenciamento");
        
        // CHAMADA À API
        taskApi.getAllExercisesForManagement(token).enqueue(new Callback<ProfessionalExercisesResponse>() {
            @Override
            public void onResponse(Call<ProfessionalExercisesResponse> call, Response<ProfessionalExercisesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfessionalExercisesResponse manageResponse = response.body();
                    if (manageResponse.isSuccess()) {
                        exerciseList.clear();
                        if (manageResponse.getExercises() != null) {
                            exerciseList.addAll(manageResponse.getExercises());
                        }
                        
                        // Criar adapter com função de deletar
                        adapter = new ExerciseManagementAdapter(exerciseList, exerciseId -> {
                            deleteExercise(exerciseId);
                        });
                        
                        recyclerView.setAdapter(adapter);
                        updateUI();
                        
                        Log.d(TAG, "Carregados " + exerciseList.size() + " exercícios");
                    } else {
                        showError(manageResponse.getMessage());
                    }
                } else {
                    showError("Erro ao carregar exercícios");
                }
            }
            
            @Override
            public void onFailure(Call<ProfessionalExercisesResponse> call, Throwable t) {
                Log.e(TAG, "Falha ao carregar exercícios", t);
                showError("Falha de conexão");
            }
        });
    }
    
    private void deleteExercise(int exerciseId) {
        // Confirmação antes de deletar
        new AlertDialog.Builder(this)
            .setTitle("Deletar Exercício")
            .setMessage("Tem certeza que deseja deletar este exercício?")
            .setPositiveButton("Deletar", (dialog, which) -> {
                performDelete(exerciseId);
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }
    
    private void performDelete(int exerciseId) {
        String token = tokenManager.getAuthToken();
        if (token == null) return;
        
        Log.d(TAG, "Deletando exercício: " + exerciseId);
        
        // CHAMADA À API PARA DELETAR
        taskApi.deleteExerciseProfessional(token, exerciseId).enqueue(new Callback<DeleteExerciseResponse>() {
            @Override
            public void onResponse(Call<DeleteExerciseResponse> call, Response<DeleteExerciseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DeleteExerciseResponse deleteResponse = response.body();
                    if (deleteResponse.isSuccess()) {
                        Toast.makeText(ExerciseManagementActivity.this, deleteResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        
                        // Remover da lista e atualizar
                        exerciseList.removeIf(exercise -> exercise.getId().equals(exerciseId));
                        adapter.notifyDataSetChanged();
                        updateUI();
                        
                        Log.d(TAG, "Exercício deletado: " + deleteResponse.getExerciseId());
                    } else {
                        showError(deleteResponse.getMessage());
                    }
                } else {
                    showError("Erro ao deletar exercício");
                }
            }
            
            @Override
            public void onFailure(Call<DeleteExerciseResponse> call, Throwable t) {
                Log.e(TAG, "Falha ao deletar exercício", t);
                showError("Falha de conexão");
            }
        });
    }
    
    private void updateUI() {
        if (exerciseList == null || exerciseList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
