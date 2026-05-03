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
import com.example.testbackend.models.PatientExercisesResponse;
import com.example.testbackend.models.PatientsResponse;
import com.example.testbackend.models.Task;
import com.example.testbackend.models.TaskCompletionRequest;
import com.example.testbackend.models.TaskCompletionResponse;
import com.example.testbackend.models.TestTasksResponse;
import com.example.testbackend.models.UserPointsResponse;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.TaskApi;
import com.example.testbackend.utils.LocaleHelper;
import com.example.testbackend.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseListActivity extends AppCompatActivity implements TaskWithRadioAdapter.OnTaskCompleteListener, TaskWithRadioAdapter.OnTaskLongClickListener {
    private static final String TAG = "EXERCISE_DEBUG";
    
    private SwipeRefreshLayout swipeRefresh;
    private TaskWithRadioAdapter adapter;
    private final List<Task> taskList = new ArrayList<>();
    private final List<Patient> patientList = new ArrayList<>();
    private Button btnSelectPatient;
    private TextView tvUserPoints;
    private TokenManager tokenManager;
    private TaskApi taskApi;
    private UserPointsResponse currentUserPoints;
    private Patient selectedPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            Log.d(TAG, "🔥 DEBUG: Iniciando onCreate");
            setContentView(R.layout.activity_exercise_list);

            tokenManager = new TokenManager(this);
            taskApi = ApiClient.getTaskClient().create(TaskApi.class);
            
            setupToolbar();
            initViews();
            checkUserRoleAndSetupUI();
            
            Log.d(TAG, "🔥 DEBUG: onCreate concluído");
            
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
                getSupportActionBar().setTitle(R.string.my_exercises);
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }
    
    private void initViews() {
        RecyclerView rvExercises = findViewById(R.id.rvExercises);
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
        Log.d(TAG, "🔥 DEBUG: Role: " + userRole);
        
        if ("patient".equals(userRole)) {
            setupPatientUI();
        } else {
            setupProfessionalUI();
        }
    }
    
    private String getUserRole() {
        if (tokenManager != null) {
            String role = tokenManager.getUserRole();
            if (role != null && !role.isEmpty()) {
                return role;
            }
        }
        return "patient";
    }
    
    private void setupPatientUI() {
        if (btnSelectPatient != null) {
            btnSelectPatient.setVisibility(View.GONE);
        }
        updateToolbarTitle();
        loadPatientTasks();
        updateUserPoints();
    }
    
    private void setupProfessionalUI() {
        if (btnSelectPatient != null) {
            btnSelectPatient.setVisibility(View.VISIBLE);
            btnSelectPatient.setText(R.string.select_patient);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.my_exercises);
        }
        loadPatients();
    }
    
    private void refreshData() {
        String userRole = getUserRole();
        if ("patient".equals(userRole)) {
            loadPatientTasks();
        } else if (selectedPatient != null) {
            loadPatientExercises(selectedPatient.getId());
        } else {
            loadPatients();
        }
        updateUserPoints();
    }

    private void loadPatients() {
        String token = tokenManager.getAuthToken();
        if (token == null || taskApi == null) {
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            return;
        }

        taskApi.getPatients(token).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PatientsResponse> call, @NonNull Response<PatientsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    patientList.clear();
                    List<Patient> patients = response.body().getPatients();
                    if (patients != null) {
                        patientList.addAll(patients);
                    }
                    
                    if (!patientList.isEmpty() && selectedPatient == null) {
                        selectedPatient = patientList.get(0);
                        loadPatientExercises(selectedPatient.getId());
                        updatePatientButtonText();
                        updateToolbarTitle();
                    }
                } else {
                    addHardcodedPatients();
                }
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<PatientsResponse> call, @NonNull Throwable t) {
                addHardcodedPatients();
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            }
        });
    }
    
    private void loadPatientTasks() {
        String token = tokenManager.getAuthToken();
        if (token == null || taskApi == null) {
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            return;
        }
        
        taskApi.getTestTasks(token).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TestTasksResponse> call, @NonNull Response<TestTasksResponse> response) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                if (isFinishing()) return;
                
                if (response.isSuccessful() && response.body() != null) {
                    taskList.clear();
                    List<Task> tasks = response.body().getTasks();
                    if (tasks != null) {
                        taskList.addAll(tasks);
                    }
                    if (adapter != null) adapter.notifyDataSetChanged();
                } else if (response.code() == 401 || response.code() == 403) {
                    handleAuthError();
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<TestTasksResponse> call, @NonNull Throwable t) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                Log.e(TAG, "🔥 FALHA: " + t.getMessage());
            }
        });
    }

    private void loadPatientExercises(int patientId) {
        String token = tokenManager.getAuthToken();
        if (token == null || taskApi == null) {
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            return;
        }
        
        if (swipeRefresh != null) swipeRefresh.setRefreshing(true);
        
        taskApi.getPatientExercises(token, patientId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PatientExercisesResponse> call, @NonNull Response<PatientExercisesResponse> response) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                if (isFinishing()) return;
                
                if (response.isSuccessful() && response.body() != null) {
                    taskList.clear();
                    List<Task> exercises = response.body().getExercises();
                    if (exercises != null) {
                        taskList.addAll(exercises);
                    }
                    if (adapter != null) adapter.notifyDataSetChanged();
                    updateToolbarTitle();
                } else if (response.code() == 401 || response.code() == 403) {
                    handleAuthError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PatientExercisesResponse> call, @NonNull Throwable t) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                Toast.makeText(ExerciseListActivity.this, "Erro ao carregar exercícios", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void addHardcodedPatients() {
        patientList.clear();
        patientList.add(new Patient(3, "cria", "cria@gmail.com", "patient"));
        patientList.add(new Patient(5, "testando", "testando@gmail.com", "patient"));
        patientList.add(new Patient(6, "aws", "aws@gmail.com", "patient"));
        patientList.add(new Patient(13, "novo.paciente", "novo.paciente@teste.com", "patient"));
        
        if (!patientList.isEmpty() && selectedPatient == null) {
            selectedPatient = patientList.get(0);
            updatePatientButtonText();
            loadPatientExercises(selectedPatient.getId());
            updateToolbarTitle();
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

        new AlertDialog.Builder(this)
            .setTitle(R.string.select_patient)
            .setItems(patientNames, (dialog, which) -> {
                selectedPatient = patientList.get(which);
                loadPatientExercises(selectedPatient.getId());
                updatePatientButtonText();
                updateToolbarTitle();
                updateUserPoints();
                Toast.makeText(this, getString(R.string.patient_selected, selectedPatient.getDisplayName()), Toast.LENGTH_SHORT).show();
            })
            .show();
    }

    private void updatePatientButtonText() {
        if (btnSelectPatient != null && selectedPatient != null) {
            btnSelectPatient.setText(selectedPatient.getDisplayName());
        }
    }

    private void updateToolbarTitle() {
        if (getSupportActionBar() != null) {
            String role = getUserRole();
            String name = "patient".equals(role) ? tokenManager.getUserName() : 
                         (selectedPatient != null ? selectedPatient.getDisplayName() : null);
            
            if (name != null) {
                getSupportActionBar().setTitle(getString(R.string.exercises_format, name));
            } else {
                getSupportActionBar().setTitle(R.string.my_exercises);
            }
        }
    }
    
    private void handleAuthError() {
        Log.e(TAG, "🔥 ERRO DE AUTENTICAÇÃO detectado!");
        Toast.makeText(this, "Sessão expirada", Toast.LENGTH_LONG).show();
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
            .setTitle(R.string.excluir)
            .setMessage(getString(R.string.delete_exercise_confirm, task.getTitle()))
            .setPositiveButton(R.string.excluir, (dialog, which) -> deleteTask(task))
            .setNegativeButton(R.string.cancel, null)
            .show();
    }

    private void deleteTask(Task task) {
        String token = tokenManager.getAuthToken();
        if (token == null || taskApi == null) return;
        
        taskApi.deleteExerciseProfessional(token, task.getId()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<DeleteExerciseResponse> call, @NonNull Response<DeleteExerciseResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ExerciseListActivity.this, "Exercício excluído", Toast.LENGTH_SHORT).show();
                    refreshData();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeleteExerciseResponse> call, @NonNull Throwable t) {
                Toast.makeText(ExerciseListActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void completeTaskOnBackend(Task task) {
        String token = tokenManager.getAuthToken();
        if (token == null || taskApi == null) return;
        
        taskApi.completeTask(token, new TaskCompletionRequest(task.getId())).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TaskCompletionResponse> call, @NonNull Response<TaskCompletionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TaskCompletionResponse result = response.body();
                    if (result.isSuccess()) {
                        Toast.makeText(ExerciseListActivity.this, 
                            getString(R.string.task_completed_points, result.getPointsAwarded()), 
                            Toast.LENGTH_SHORT).show();
                        updateTaskAsCompleted(task);
                        updateUserPoints();
                    } else {
                        Toast.makeText(ExerciseListActivity.this, result.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == 401 || response.code() == 403) {
                    handleAuthError();
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<TaskCompletionResponse> call, @NonNull Throwable t) {
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
        
        taskApi.getUserPoints(token).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UserPointsResponse> call, @NonNull Response<UserPointsResponse> response) {
                if (!isFinishing() && response.isSuccessful() && response.body() != null) {
                    currentUserPoints = response.body();
                    updatePointsUI();
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<UserPointsResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Erro ao carregar pontos", t);
            }
        });
    }

    private void updatePointsUI() {
        if (tvUserPoints != null && currentUserPoints != null) {
            String userName = currentUserPoints.getUsername() != null ? currentUserPoints.getUsername() : tokenManager.getUserName();
            tvUserPoints.setText(getString(R.string.user_points_format, 
                userName, currentUserPoints.getTotalPoints(), currentUserPoints.getLevel()));
            tvUserPoints.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
