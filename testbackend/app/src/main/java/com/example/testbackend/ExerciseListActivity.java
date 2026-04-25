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
import com.example.testbackend.models.TaskCompletionRequest;
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
        tvUserPoints = findViewById(R.id.tvUserPoints);
        
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
            if (token == null) handleAuthError();
            return;
        }
        
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
                } else if (response.code() == 401 || response.code() == 403) {
                    handleAuthError();
                }
            }

            @Override
            public void onFailure(Call<TestTasksResponse> call, Throwable t) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                Log.e(TAG, "Falha de conexão: " + t.getMessage());
            }
        });
    }

    @Override
    public void onTaskComplete(Task task) {
        if (task == null) return;
        completeTaskOnBackend(task);
    }

    private void completeTaskOnBackend(Task task) {
        String token = tokenManager.getAuthToken();
        if (token == null || taskApi == null) {
            handleAuthError();
            return;
        }
        
        // 🔥 Cria request com ID REAL da tarefa para controle individual
        TaskCompletionRequest request = new TaskCompletionRequest(task.getId());
        
        taskApi.completeTask(token, request).enqueue(new Callback<TaskCompletionResponse>() {
            @Override
            public void onResponse(Call<TaskCompletionResponse> call, Response<TaskCompletionResponse> response) {
                if (isFinishing()) return;
                
                if (response.isSuccessful() && response.body() != null) {
                    TaskCompletionResponse result = response.body();
                    
                    if (result.isSuccess()) {
                        Toast.makeText(ExerciseListActivity.this, "Tarefa concluída! +" + result.getPointsAwarded() + " pontos", Toast.LENGTH_SHORT).show();
                        updateTaskAsCompleted(task);
                        updateUserPoints();
                        
                        // Mostra progressão diária
                        if (result.getTasksCompletedToday() != null) {
                            String progressMsg = "Progresso: " + result.getTasksCompletedToday() + "/5 tarefas hoje";
                            Toast.makeText(ExerciseListActivity.this, progressMsg, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Trata bloqueio de repetição
                        String message = result.getMessage();
                        if (result.getCanRepeatTomorrow() != null && result.getCanRepeatTomorrow()) {
                            message += "\n\n📅 Você poderá repetir este exercício amanhã!";
                        }
                        Toast.makeText(ExerciseListActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == 401 || response.code() == 403) {
                    handleAuthError();
                }
            }
            
            @Override
            public void onFailure(Call<TaskCompletionResponse> call, Throwable t) {
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
        if (token == null || taskApi == null) {
            handleAuthError();
            return;
        }
        
        taskApi.getUserPoints(token).enqueue(new Callback<UserPointsResponse>() {
            @Override
            public void onResponse(Call<UserPointsResponse> call, Response<UserPointsResponse> response) {
                if (isFinishing()) return;
                if (response.isSuccessful() && response.body() != null) {
                    currentUserPoints = response.body();
                    updatePointsUI();
                } else if (response.code() == 401 || response.code() == 403) {
                    handleAuthError();
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
            String userName = currentUserPoints.getUsername();
            if (userName == null || userName.isEmpty()) {
                userName = tokenManager.getUserName();
            }
            
            if (userName == null || userName.isEmpty()) userName = "Usuário";

            tvUserPoints.setText("🏆 " + userName + " | Pontos: " + currentUserPoints.getTotalPoints() + 
                               " | Nível: " + currentUserPoints.getLevel());
            tvUserPoints.setVisibility(View.VISIBLE);
        }
    }

    private void handleAuthError() {
        if (tokenManager != null) {
            tokenManager.clearToken();
        }
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
