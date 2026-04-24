package com.example.testbackend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.testbackend.adapters.TaskWithRadioAdapter;
import com.example.testbackend.models.Task;
import com.example.testbackend.models.TaskCompletionResponse;
import com.example.testbackend.models.TestTasksResponse;
import com.example.testbackend.models.UserPointsResponse;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.TaskApi;
import com.example.testbackend.utils.LocaleHelper;
import com.example.testbackend.utils.TokenManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseListActivity extends AppCompatActivity implements TaskWithRadioAdapter.OnTaskCompleteListener {

    private static final String TAG = "EXERCISE_DEBUG";
    private RecyclerView rvExercises;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvUserPoints;
    private TaskWithRadioAdapter adapter;
    private List<Task> taskList = new ArrayList<>();
    private FloatingActionButton fabAdd;
    private TokenManager tokenManager;
    private TaskApi taskApi;
    private UserPointsResponse currentUserPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_exercise_list);

            tokenManager = new TokenManager(this);
            taskApi = ApiClient.getTaskClient().create(TaskApi.class);

            setupToolbar();
            initViews();
            checkUserRole();
            loadPatientTasks();
            updateUserPoints(); 
        } catch (Exception e) {
            Log.e(TAG, "Erro fatal no onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Erro ao abrir tela", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Meus Exercícios");
            }
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void initViews() {
        swipeRefresh = findViewById(R.id.swipeRefresh);
        rvExercises = findViewById(R.id.rvExercises);
        tvUserPoints = findViewById(R.id.tvUserPoints); // Pode ser nulo se não houver no XML ainda
        
        if (rvExercises != null) {
            rvExercises.setLayoutManager(new LinearLayoutManager(this));
            adapter = new TaskWithRadioAdapter(taskList, this);
            rvExercises.setAdapter(adapter);
        }

        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this::refreshData);
        }

        fabAdd = findViewById(R.id.fabAddExercise);
    }

    private void refreshData() {
        loadPatientTasks();
        updateUserPoints();
    }

    private void checkUserRole() {
        if (tokenManager == null || fabAdd == null) return;
        String role = tokenManager.getUserRole();
        if (role != null && (role.equalsIgnoreCase("professional") || role.equalsIgnoreCase("doctor"))) {
            fabAdd.setVisibility(View.VISIBLE);
        } else {
            fabAdd.setVisibility(View.GONE);
        }
    }

    private void loadPatientTasks() {
        String token = tokenManager.getAuthToken();
        if (token == null || taskApi == null) {
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            return;
        }
        
        Log.d(TAG, "Chamando /tasks/test com token: " + token);
        
        taskApi.getTestTasks(token).enqueue(new Callback<TestTasksResponse>() {
            @Override
            public void onResponse(Call<TestTasksResponse> call, Response<TestTasksResponse> response) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                if (isFinishing()) return;
                
                if (response.isSuccessful() && response.body() != null) {
                    TestTasksResponse data = response.body();
                    List<Task> tasks = data.getTasks();
                    
                    taskList.clear();
                    if (tasks != null) {
                        taskList.addAll(tasks);
                    }
                    
                    if (rvExercises != null) {
                        adapter = new TaskWithRadioAdapter(taskList, ExerciseListActivity.this);
                        rvExercises.setAdapter(adapter);
                    }
                    
                    Log.d(TAG, "Tarefas atualizadas: " + (tasks != null ? tasks.size() : 0));
                } else {
                    Log.e(TAG, "Erro API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TestTasksResponse> call, Throwable t) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                if (isFinishing()) return;
                Log.e(TAG, "Falha de conexão: " + t.getMessage());
            }
        });
    }

    @Override
    public void onTaskComplete(Task task) {
        if (task == null) return;
        
        Toast.makeText(this, "Concluindo: " + task.getTitle(), Toast.LENGTH_SHORT).show();
        completeTaskOnBackend(task);
    }

    private void completeTaskOnBackend(Task task) {
        String token = tokenManager.getAuthToken();
        if (token == null || taskApi == null) {
            Toast.makeText(this, "Erro de autenticação", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Log.d(TAG, "Concluindo tarefa: " + task.getId());
        
        taskApi.completeTask(token).enqueue(new Callback<TaskCompletionResponse>() {
            @Override
            public void onResponse(Call<TaskCompletionResponse> call, Response<TaskCompletionResponse> response) {
                if (isFinishing()) return;
                
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ExerciseListActivity.this, 
                        "Tarefa concluída! +" + (task.getPointsValue() != null ? task.getPointsValue() : 0) + " pontos", 
                        Toast.LENGTH_LONG).show();
                    
                    updateTaskAsCompleted(task);
                    updateUserPoints(); // Atualiza ranking/pontos imediatamente
                } else {
                    Log.e(TAG, "Erro ao concluir tarefa: " + response.code());
                    Toast.makeText(ExerciseListActivity.this, "Erro ao registrar conclusão", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<TaskCompletionResponse> call, Throwable t) {
                if (isFinishing()) return;
                Log.e(TAG, "Falha na API: " + t.getMessage());
                Toast.makeText(ExerciseListActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTaskAsCompleted(Task task) {
        task.setCompletedToday(true);
        if (adapter != null) {
            int position = taskList.indexOf(task);
            if (position != -1) {
                adapter.notifyItemChanged(position);
            }
        }
    }

    private void updateUserPoints() {
        String token = tokenManager.getAuthToken();
        if (token == null || taskApi == null) return;
        
        taskApi.getUserPoints(token).enqueue(new Callback<UserPointsResponse>() {
            @Override
            public void onResponse(Call<UserPointsResponse> call, Response<UserPointsResponse> response) {
                if (isFinishing()) return;
                
                if (response.isSuccessful() && response.body() != null) {
                    currentUserPoints = response.body();
                    updatePointsUI();
                    Log.d(TAG, "Pontos carregados: " + currentUserPoints.getTotalPoints());
                }
            }
            
            @Override
            public void onFailure(Call<UserPointsResponse> call, Throwable t) {
                Log.e(TAG, "Erro ao carregar pontos: " + t.getMessage());
            }
        });
    }

    private void updatePointsUI() {
        if (tvUserPoints != null && currentUserPoints != null) {
            // 🔥 **INCLUIR NOME DINÂMICO DO USUÁRIO**
            String userName = currentUserPoints.getUsername() != null ? currentUserPoints.getUsername() : "Usuário";
            tvUserPoints.setText("🏆 " + userName + " | Pontos: " + currentUserPoints.getTotalPoints() + 
                               " | Nível: " + currentUserPoints.getLevel());
            tvUserPoints.setVisibility(View.VISIBLE);
            
            Log.d(TAG, "UI Atualizada: " + userName + " | Pontos: " + currentUserPoints.getTotalPoints());
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
