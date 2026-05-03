package com.example.testbackend;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.testbackend.adapters.TaskWithRadioAdapter;
import com.example.testbackend.models.DeleteExerciseResponse;
import com.example.testbackend.models.Patient;
import com.example.testbackend.models.Task;
import com.example.testbackend.models.TaskCompletionRequest;
import com.example.testbackend.models.TaskCompletionResponse;
import com.example.testbackend.models.TestTasksResponse;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.TaskApi;
import com.example.testbackend.utils.LocaleHelper;
import com.example.testbackend.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Versão limpa da atividade de lista de exercícios.
 * Renomeada para ExerciseListActivity_clean para evitar conflitos de classe duplicada.
 */
public class ExerciseListActivity_clean extends AppCompatActivity implements 
        TaskWithRadioAdapter.OnTaskCompleteListener, 
        TaskWithRadioAdapter.OnTaskLongClickListener {
    
    private static final String TAG = "EXERCISE_DEBUG";
    
    private RecyclerView rvExercises;
    private SwipeRefreshLayout swipeRefresh;
    private TaskWithRadioAdapter adapter;
    private final List<Task> taskList = new ArrayList<>();
    private List<Patient> patientList = new ArrayList<>();
    private Button btnSelectPatient;
    private TextView tvUserPoints;
    private TokenManager tokenManager;
    private TaskApi taskApi;
    private Patient selectedPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            Log.d(TAG, "🔥 DEBUG: Iniciando ExerciseListActivity_clean");
            setContentView(R.layout.activity_exercise_list);

            tokenManager = new TokenManager(this);
            taskApi = ApiClient.getTaskClient().create(TaskApi.class);
            
            setupToolbar();
            initViews();
            checkUserRoleAndSetupUI();
            
        } catch (Exception e) {
            Log.e(TAG, "🔥 ERRO no onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Erro ao abrir tela: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }
    
    private void initViews() {
        rvExercises = findViewById(R.id.rvExercises);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        btnSelectPatient = findViewById(R.id.btnSelectPatient);
        tvUserPoints = findViewById(R.id.tvUserPoints);
        
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
    
    private void checkUserRoleAndSetupUI() {
        String userRole = getUserRole();
        if ("patient".equals(userRole)) {
            setupPatientUI();
        } else {
            setupProfessionalUI();
        }
    }
    
    private String getUserRole() {
        if (tokenManager != null) {
            String role = tokenManager.getUserRole();
            if (role != null && !role.isEmpty()) return role;
        }
        return "patient";
    }
    
    private void setupPatientUI() {
        if (btnSelectPatient != null) btnSelectPatient.setVisibility(View.GONE);
        
        if (getSupportActionBar() != null) {
            String name = tokenManager != null ? tokenManager.getUserName() : "Paciente";
            getSupportActionBar().setTitle("Exercícios: " + (name != null ? name : ""));
        }
        loadTestTasks();
    }
    
    private void setupProfessionalUI() {
        if (btnSelectPatient != null) {
            btnSelectPatient.setVisibility(View.VISIBLE);
            btnSelectPatient.setText("Selecionar Paciente");
        }
        addHardcodedPatients();
    }
    
    private void refreshData() {
        loadTestTasks();
    }
    
    private void loadTestTasks() {
        String token = tokenManager.getAuthToken();
        if (token == null || taskApi == null) {
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            return;
        }
        
        taskApi.getTestTasks(token).enqueue(new Callback<TestTasksResponse>() {
            @Override
            public void onResponse(@NonNull Call<TestTasksResponse> call, @NonNull Response<TestTasksResponse> response) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                if (isFinishing()) return;
                
                if (response.isSuccessful() && response.body() != null) {
                    taskList.clear();
                    List<Task> tasks = response.body().getTasks();
                    if (tasks != null) taskList.addAll(tasks);
                    if (adapter != null) adapter.notifyDataSetChanged();
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<TestTasksResponse> call, @NonNull Throwable t) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                Log.e(TAG, "🔥 FALHA: " + t.getMessage());
            }
        });
    }
    
    private void addHardcodedPatients() {
        patientList.clear();
        patientList.add(new Patient(3, "cria", "cria@gmail.com", "patient"));
        patientList.add(new Patient(5, "testando", "testando@gmail.com", "patient"));
        patientList.add(new Patient(13, "novo.paciente", "novo.paciente@teste.com", "patient"));
        
        if (!patientList.isEmpty()) {
            selectedPatient = patientList.get(0);
            if (btnSelectPatient != null) btnSelectPatient.setText(selectedPatient.getDisplayName());
            loadTestTasks();
        }
    }
    
    private void showPatientSelectionDialog() {
        if (patientList.isEmpty()) return;

        String[] patientNames = new String[patientList.size()];
        for (int i = 0; i < patientList.size(); i++) {
            patientNames[i] = patientList.get(i).getDisplayName();
        }

        new AlertDialog.Builder(this)
            .setTitle("Selecione um Paciente")
            .setItems(patientNames, (dialog, which) -> {
                selectedPatient = patientList.get(which);
                loadTestTasks();
                if (btnSelectPatient != null) btnSelectPatient.setText(selectedPatient.getDisplayName());
            })
            .show();
    }

    @Override
    public void onTaskComplete(Task task) {
        if (task != null) completeTaskOnBackend(task);
    }

    @Override
    public void onTaskLongClick(Task task) {
        new AlertDialog.Builder(this)
            .setTitle("Excluir Exercício")
            .setMessage("Deseja excluir \"" + task.getTitle() + "\"?")
            .setPositiveButton("Excluir", (dialog, which) -> deleteTask(task))
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void deleteTask(Task task) {
        String token = tokenManager.getAuthToken();
        if (token == null || taskApi == null) return;
        
        taskApi.deleteExerciseProfessional(token, task.getId()).enqueue(new Callback<DeleteExerciseResponse>() {
            @Override
            public void onResponse(@NonNull Call<DeleteExerciseResponse> call, @NonNull Response<DeleteExerciseResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ExerciseListActivity_clean.this, "Exercício excluído", Toast.LENGTH_SHORT).show();
                    loadTestTasks();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeleteExerciseResponse> call, @NonNull Throwable t) {
                Toast.makeText(ExerciseListActivity_clean.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void completeTaskOnBackend(Task task) {
        String token = tokenManager.getAuthToken();
        if (token == null || taskApi == null) return;
        
        taskApi.completeTask(token, new TaskCompletionRequest(task.getId())).enqueue(new Callback<TaskCompletionResponse>() {
            @Override
            public void onResponse(@NonNull Call<TaskCompletionResponse> call, @NonNull Response<TaskCompletionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ExerciseListActivity_clean.this, "Concluído!", Toast.LENGTH_SHORT).show();
                    loadTestTasks();
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<TaskCompletionResponse> call, @NonNull Throwable t) {
                Toast.makeText(ExerciseListActivity_clean.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
