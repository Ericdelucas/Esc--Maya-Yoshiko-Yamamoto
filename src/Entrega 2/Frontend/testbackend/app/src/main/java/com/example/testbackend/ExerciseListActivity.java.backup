package com.example.testbackend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.testbackend.models.DeleteExerciseResponse;
import com.example.testbackend.models.Patient;
import com.example.testbackend.models.PatientsResponse;
import com.example.testbackend.models.PatientExercisesResponse;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.TaskApi;
import com.example.testbackend.utils.LocaleHelper;
import com.example.testbackend.utils.TokenManager;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseListActivity extends AppCompatActivity implements TaskWithRadioAdapter.OnTaskCompleteListener, TaskWithRadioAdapter.OnTaskLongClickListener {

    private static final String TAG = "EXERCISE_DEBUG";
    private RecyclerView rvExercises;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvUserPoints;
    private Button btnSelectPatient;
    private TaskWithRadioAdapter adapter;
    private List<Task> taskList = new ArrayList<>();
    private List<Patient> patientList = new ArrayList<>();
    private TokenManager tokenManager;
    private TaskApi taskApi;
    private UserPointsResponse currentUserPoints;
    private Patient selectedPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_exercise_list);

            tokenManager = new TokenManager(this);
            taskApi = ApiClient.getTaskClient().create(TaskApi.class);

            setupToolbar();
            initViews();
            loadPatients();
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
        btnSelectPatient = findViewById(R.id.btnSelectPatient);
        
        if (rvExercises != null) {
            rvExercises.setLayoutManager(new LinearLayoutManager(this));
            adapter = new TaskWithRadioAdapter(taskList, this, this);
            rvExercises.setAdapter(adapter);
        }

        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this::refreshData);
        }

        if (btnSelectPatient != null) {
            btnSelectPatient.setOnClickListener(v -> showPatientSelectionDialog());
        }
    }

    private void refreshData() {
        loadPatientTasks();
        updateUserPoints();
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
                        adapter = new TaskWithRadioAdapter(taskList, ExerciseListActivity.this, ExerciseListActivity.this);
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
    public void onTaskLongClick(Task task) {
        new AlertDialog.Builder(this)
            .setTitle("Excluir Exercício")
            .setMessage("Tem certeza que deseja excluir o exercício \"" + task.getTitle() + "\"?")
            .setPositiveButton("Excluir", (dialog, which) -> deleteTask(task))
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void deleteTask(Task task) {
        String token = tokenManager.getAuthToken();
        if (token == null || taskApi == null) {
            Toast.makeText(this, "Erro de autenticação", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Log.d(TAG, "Excluindo exercício ID: " + task.getId());
        
        // Usar endpoint de exclusão profissional
        taskApi.deleteExerciseProfessional(token, task.getId()).enqueue(new Callback<DeleteExerciseResponse>() {
            @Override
            public void onResponse(Call<DeleteExerciseResponse> call, Response<DeleteExerciseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DeleteExerciseResponse result = response.body();
                    
                    if (result.isSuccess()) {
                        // Remover da lista local
                        int position = taskList.indexOf(task);
                        if (position != -1) {
                            taskList.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, taskList.size());
                        }
                        
                        Toast.makeText(ExerciseListActivity.this, "Exercício excluído com sucesso", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ExerciseListActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Tratar diferentes tipos de erro
                    if (response.code() == 401) {
                        Toast.makeText(ExerciseListActivity.this, "Sessão expirada. Faça login novamente.", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Erro 401 - Sessão expirada");
                        // Opcional: redirecionar para tela de login
                        tokenManager.clearToken();
                        // Intent loginIntent = new Intent(this, LoginActivity.class);
                        // startActivity(loginIntent);
                        // finish();
                    } else if (response.code() == 403) {
                        Toast.makeText(ExerciseListActivity.this, "Você não tem permissão para excluir exercícios.", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Erro 403 - Sem permissão");
                    } else if (response.code() == 404) {
                        Toast.makeText(ExerciseListActivity.this, "Exercício não encontrado ou já excluído.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Erro 404 - Exercício não encontrado");
                        // Remover da lista local mesmo assim
                        int position = taskList.indexOf(task);
                        if (position != -1) {
                            taskList.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, taskList.size());
                        }
                    } else {
                        Toast.makeText(ExerciseListActivity.this, "Erro ao excluir exercício (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Erro na resposta: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<DeleteExerciseResponse> call, Throwable t) {
                Toast.makeText(ExerciseListActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Falha na conexão", t);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    private void loadPatients() {
        String token = tokenManager.getAuthToken();
        if (token == null || taskApi == null) {
            if (token == null) handleAuthError();
            return;
        }
        
        taskApi.getPatients(token).enqueue(new Callback<PatientsResponse>() {
            @Override
            public void onResponse(Call<PatientsResponse> call, Response<PatientsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PatientsResponse data = response.body();
                    patientList.clear();
                    if (data.getPatients() != null) {
                        patientList.addAll(data.getPatients());
                    }
                    
                    // Se houver pacientes, seleciona o primeiro automaticamente
                    if (!patientList.isEmpty()) {
                        selectedPatient = patientList.get(0);
                        loadPatientExercises(selectedPatient.getId());
                        updatePatientButtonText();
                    }
                } else if (response.code() == 401 || response.code() == 403) {
                    handleAuthError();
                }
            }

            @Override
            public void onFailure(Call<PatientsResponse> call, Throwable t) {
                Log.e(TAG, "Falha ao carregar pacientes: " + t.getMessage());
                Toast.makeText(ExerciseListActivity.this, "Erro ao carregar pacientes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPatientSelectionDialog() {
        if (patientList.isEmpty()) {
            Toast.makeText(this, "Nenhum paciente disponível", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] patientNames = new String[patientList.size()];
        for (int i = 0; i < patientList.size(); i++) {
            patientNames[i] = patientList.get(i).getDisplayName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione um Paciente");
        builder.setItems(patientNames, (dialog, which) -> {
            selectedPatient = patientList.get(which);
            loadPatientExercises(selectedPatient.getId());
            updatePatientButtonText();
            Toast.makeText(this, "Paciente selecionado: " + selectedPatient.getDisplayName(), Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    private void loadPatientExercises(int patientId) {
        String token = tokenManager.getAuthToken();
        if (token == null || taskApi == null) {
            if (token == null) handleAuthError();
            return;
        }
        
        if (swipeRefresh != null) swipeRefresh.setRefreshing(true);
        
        taskApi.getPatientExercises(token, patientId).enqueue(new Callback<PatientExercisesResponse>() {
            @Override
            public void onResponse(Call<PatientExercisesResponse> call, Response<PatientExercisesResponse> response) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                if (isFinishing()) return;
                
                if (response.isSuccessful() && response.body() != null) {
                    PatientExercisesResponse data = response.body();
                    List<Task> exercises = data.getExercises();
                    
                    taskList.clear();
                    if (exercises != null) {
                        taskList.addAll(exercises);
                    }
                    
                    if (rvExercises != null) {
                        adapter = new TaskWithRadioAdapter(taskList, ExerciseListActivity.this, ExerciseListActivity.this);
                        rvExercises.setAdapter(adapter);
                    }
                    
                    // Atualizar título com nome do paciente
                    if (getSupportActionBar() != null && selectedPatient != null) {
                        getSupportActionBar().setTitle("Exercícios: " + selectedPatient.getDisplayName());
                    }
                } else if (response.code() == 401 || response.code() == 403) {
                    handleAuthError();
                }
            }

            @Override
            public void onFailure(Call<PatientExercisesResponse> call, Throwable t) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                Log.e(TAG, "Falha ao carregar exercícios do paciente: " + t.getMessage());
                Toast.makeText(ExerciseListActivity.this, "Erro ao carregar exercícios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePatientButtonText() {
        if (btnSelectPatient != null && selectedPatient != null) {
            btnSelectPatient.setText(selectedPatient.getDisplayName());
        }
    }
}
