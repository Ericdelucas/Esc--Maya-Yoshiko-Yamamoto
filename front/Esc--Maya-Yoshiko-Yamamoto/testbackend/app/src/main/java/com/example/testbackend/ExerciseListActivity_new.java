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

public class ExerciseListActivity_new extends AppCompatActivity implements TaskWithRadioAdapter.OnTaskCompleteListener, TaskWithRadioAdapter.OnTaskLongClickListener {
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
            Log.d(TAG, "🔥 DEBUG: Iniciando onCreate da ExerciseListActivity_new");
            
            setContentView(R.layout.activity_exercise_list);
            Log.d(TAG, "🔥 DEBUG: setContentView concluído");

            tokenManager = new TokenManager(this);
            taskApi = ApiClient.getTaskClient().create(TaskApi.class);
            Log.d(TAG, "🔥 DEBUG: TokenManager e TaskApi criados");
            
            setupToolbar();
            Log.d(TAG, "🔥 DEBUG: setupToolbar concluído");
            
            initViews();
            Log.d(TAG, "🔥 DEBUG: initViews concluído");
            
            // 🔥 VERIFICAR ROLE DO USUÁRIO
            checkUserRoleAndSetupUI();
            Log.d(TAG, "🔥 DEBUG: checkUserRoleAndSetupUI concluído");
            
            Log.d(TAG, "🔥 DEBUG: onCreate concluído com sucesso");
            
        } catch (Exception e) {
            Log.e(TAG, "🔥 ERRO FATAL no onCreate: " + e.getMessage(), e);
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
        }
        
        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this::refreshData);
        }
        
        if (btnSelectPatient != null) {
            btnSelectPatient.setOnClickListener(v -> showPatientSelectionDialog());
        }
    }
    
    private void checkUserRoleAndSetupUI() {
        try {
            String userRole = getUserRole();
            Log.d(TAG, "🔥 DEBUG: Role do usuário: " + userRole);
            
            if (userRole != null && userRole.equals("patient")) {
                Log.d(TAG, "🔥 DEBUG: Usuário é PACIENTE - configurando interface de paciente");
                setupPatientUI();
            } else {
                Log.d(TAG, "🔥 DEBUG: Usuário é PROFISSIONAL - configurando interface de profissional");
                setupProfessionalUI();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "🔥 ERRO em checkUserRoleAndSetupUI: " + e.getMessage(), e);
            setupProfessionalUI();
        }
    }
    
    private String getUserRole() {
        try {
            if (tokenManager != null) {
                String role = tokenManager.getUserRole();
                if (role != null && !role.isEmpty()) {
                    Log.d(TAG, "🔥 DEBUG: Role obtido do TokenManager: " + role);
                    return role;
                }
            }
            
            Log.w(TAG, "🔥 DEBUG: Não foi possível determinar o role - assumindo paciente como padrão");
            return "patient";
            
        } catch (Exception e) {
            Log.e(TAG, "🔥 ERRO ao obter role: " + e.getMessage(), e);
            return "patient";
        }
    }
    
    private void setupPatientUI() {
        Log.d(TAG, "🔥 DEBUG: Configurando UI para PACIENTE");
        
        if (btnSelectPatient != null) {
            btnSelectPatient.setVisibility(View.GONE);
            Log.d(TAG, "🔥 DEBUG: Botão de seleção de paciente escondido");
        }
        
        if (getSupportActionBar() != null) {
            String patientName = tokenManager != null ? tokenManager.getUserName() : "Paciente";
            if (patientName == null || patientName.isEmpty()) {
                patientName = "Paciente";
            }
            getSupportActionBar().setTitle("Exercícios: " + patientName);
            Log.d(TAG, "🔥 DEBUG: Toolbar configurada para paciente: " + patientName);
        }
        
        loadTestTasks();
    }
    
    private void setupProfessionalUI() {
        Log.d(TAG, "🔥 DEBUG: Configurando UI para PROFISSIONAL");
        
        if (btnSelectPatient != null) {
            btnSelectPatient.setVisibility(View.VISIBLE);
            btnSelectPatient.setText("Selecionar Paciente");
            Log.d(TAG, "🔥 DEBUG: Botão de seleção de paciente visível");
        }
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Meus Exercícios");
            Log.d(TAG, "🔥 DEBUG: Toolbar configurada para profissional");
        }
        
        addHardcodedPatients();
    }
    
    private void refreshData() {
        loadTestTasks();
    }
    
    private void loadTestTasks() {
        Log.d(TAG, "🔥 DEBUG: Carregando exercícios de teste");
        
        String token = tokenManager.getAuthToken();
        if (token == null || taskApi == null) {
            Log.w(TAG, "🔥 DEBUG: Token ou taskApi null");
            return;
        }
        
        taskApi.getTestTasks(token).enqueue(new Callback<TestTasksResponse>() {
            @Override
            public void onResponse(@NonNull Call<TestTasksResponse> call, @NonNull Response<TestTasksResponse> response) {
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
                        adapter = new TaskWithRadioAdapter(taskList, ExerciseListActivity_new.this, ExerciseListActivity_new.this);
                        rvExercises.setAdapter(adapter);
                    }
                    
                    Log.d(TAG, "🔥 DEBUG: Exercícios carregados: " + taskList.size());
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<TestTasksResponse> call, @NonNull Throwable t) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                Log.e(TAG, "🔥 FALHA ao carregar exercícios: " + t.getMessage());
            }
        });
    }
    
    private void addHardcodedPatients() {
        try {
            Log.d(TAG, "🔥 DEBUG: Adicionando pacientes hardcoded");
            
            patientList.clear();
            patientList.add(new Patient(3, "cria", "cria@gmail.com", "patient"));
            patientList.add(new Patient(5, "testando", "testando@gmail.com", "patient"));
            patientList.add(new Patient(6, "aws", "aws@gmail.com", "patient"));
            patientList.add(new Patient(13, "novo.paciente", "novo.paciente@teste.com", "patient"));
            
            Log.d(TAG, "🔥 DEBUG: Pacientes hardcoded adicionados: " + patientList.size());
            
            if (!patientList.isEmpty()) {
                selectedPatient = patientList.get(0);
                updatePatientButtonText();
                loadTestTasks();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "🔥 ERRO FATAL em addHardcodedPatients: " + e.getMessage(), e);
        }
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
            loadTestTasks();
            updatePatientButtonText();
            Toast.makeText(this, "Paciente selecionado: " + selectedPatient.getDisplayName(), Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    private void updatePatientButtonText() {
        if (btnSelectPatient != null && selectedPatient != null) {
            btnSelectPatient.setText(selectedPatient.getDisplayName());
        }
    }
    
    private void handleAuthError() {
        Log.e(TAG, "🔥 ERRO DE AUTENTICAÇÃO detectado!");
        Log.w(TAG, "🔥 TEMPORÁRIO: handleAuthError() desativado para evitar redirecionamento");
        Toast.makeText(this, "Erro de autenticação detectado (mas não redirecionando)", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTaskComplete(Task task) {
        if (task != null) {
            completeTaskOnBackend(task);
        }
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
        
        taskApi.deleteExerciseProfessional(token, task.getId()).enqueue(new Callback<DeleteExerciseResponse>() {
            @Override
            public void onResponse(@NonNull Call<DeleteExerciseResponse> call, @NonNull Response<DeleteExerciseResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ExerciseListActivity_new.this, "Exercício excluído com sucesso", Toast.LENGTH_SHORT).show();
                    loadTestTasks();
                } else {
                    Toast.makeText(ExerciseListActivity_new.this, "Erro ao excluir exercício", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeleteExerciseResponse> call, @NonNull Throwable t) {
                Toast.makeText(ExerciseListActivity_new.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void completeTaskOnBackend(Task task) {
        String token = tokenManager.getAuthToken();
        if (token == null || taskApi == null) {
            handleAuthError();
            return;
        }
        
        TaskCompletionRequest request = new TaskCompletionRequest(task.getId());
        
        taskApi.completeTask(token, request).enqueue(new Callback<TaskCompletionResponse>() {
            @Override
            public void onResponse(@NonNull Call<TaskCompletionResponse> call, @NonNull Response<TaskCompletionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TaskCompletionResponse completionResponse = response.body();
                    String message = completionResponse.getMessage();
                    Toast.makeText(ExerciseListActivity_new.this, message, Toast.LENGTH_LONG).show();
                    loadTestTasks();
                } else {
                    Toast.makeText(ExerciseListActivity_new.this, "Erro ao concluir exercício", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<TaskCompletionResponse> call, @NonNull Throwable t) {
                Toast.makeText(ExerciseListActivity_new.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
