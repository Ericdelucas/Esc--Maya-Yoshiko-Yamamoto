package com.example.testbackend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testbackend.models.DashboardStats;
import com.example.testbackend.models.Patient;
import com.example.testbackend.models.Task;
import com.example.testbackend.models.TaskCreateRequest;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.AuthApi;
import com.example.testbackend.network.PatientApi;
import com.example.testbackend.network.TaskApi;
import com.example.testbackend.storage.SessionManager;
import com.example.testbackend.utils.TokenManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfessionalMainActivity extends AppCompatActivity {
    
    private static final String TAG = "PROF_DEBUG";
    private TokenManager tokenManager;
    private SessionManager sessionManager;
    
    private TextView tvGreeting, tvWelcome, tvUserInitial, tvTotalPacientes, tvConsultasHoje, tvExerciciosAtivos;
    private ImageView ivUserPhoto;
    private MaterialCardView cardAccountAvatar, cardPatients, cardAddPatient, cardExercises, cardReports;
    private FloatingActionButton fabLogout, fabAddTask;
    private ImageButton btnSettings;
    private Button btnProfile, btnCalendar;
    
    private final List<Patient> patientsList = new ArrayList<>();
    private AlertDialog taskDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        tokenManager = new TokenManager(this);
        sessionManager = new SessionManager(this);
        
        if (!isProfessionalUser()) {
            redirectToCorrectActivity();
            return;
        }

        setContentView(R.layout.activity_main_professional);
        
        setupViews();
        setupClickListeners();
        loadUserInfo();
        loadPatientsForSpinner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDadosPainel();
    }
    
    private boolean isProfessionalUser() {
        String role = tokenManager.getUserRole();
        if (role == null) return false;
        
        String normalizedRole = role.trim().toLowerCase();
        return normalizedRole.contains("prof") || 
               normalizedRole.contains("doc") || 
               normalizedRole.contains("admin");
    }
    
    private void redirectToCorrectActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void setupViews() {
        tvGreeting = findViewById(R.id.tvGreeting);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvUserInitial = findViewById(R.id.tvUserInitial);
        ivUserPhoto = findViewById(R.id.ivUserPhoto);
        cardAccountAvatar = findViewById(R.id.cardAccountAvatar);
        btnSettings = findViewById(R.id.btnSettings);
        
        tvTotalPacientes = findViewById(R.id.tvTotalPacientes);
        tvConsultasHoje = findViewById(R.id.tvConsultasHoje);
        tvExerciciosAtivos = findViewById(R.id.tvExerciciosAtivos);
        
        cardPatients = findViewById(R.id.cardPatients);
        cardAddPatient = findViewById(R.id.cardAddPatient);
        cardExercises = findViewById(R.id.cardExercises);
        cardReports = findViewById(R.id.cardReports);
        
        btnProfile = findViewById(R.id.btnProfile);
        btnCalendar = findViewById(R.id.btnCalendar);
        fabLogout = findViewById(R.id.fabLogout);
        fabAddTask = findViewById(R.id.fabAddTask);
    }

    private void carregarDadosPainel() {
        AuthApi authApi = ApiClient.getAuthClient().create(AuthApi.class);
        authApi.getDashboardStats(tokenManager.getAuthToken()).enqueue(new Callback<DashboardStats>() {
            @Override
            public void onResponse(Call<DashboardStats> call, Response<DashboardStats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DashboardStats stats = response.body();
                    if (tvTotalPacientes != null) tvTotalPacientes.setText(String.valueOf(stats.getTotalPatients()));
                    if (tvConsultasHoje != null) tvConsultasHoje.setText(String.valueOf(stats.getAppointmentsToday()));
                    if (tvExerciciosAtivos != null) tvExerciciosAtivos.setText(String.valueOf(stats.getActiveExercises()));
                }
            }

            @Override
            public void onFailure(Call<DashboardStats> call, Throwable t) {
                if (tvTotalPacientes != null) tvTotalPacientes.setText("-");
            }
        });
    }

    private void loadPatientsForSpinner() {
        String token = tokenManager.getAuthToken();
        PatientApi patientApi = ApiClient.getPatientClient().create(PatientApi.class);
        
        patientApi.getPatients(token).enqueue(new Callback<List<Patient>>() {
            @Override
            public void onResponse(Call<List<Patient>> call, Response<List<Patient>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    patientsList.clear();
                    patientsList.addAll(response.body());
                    Log.d(TAG, "Pacientes carregados para o spinner: " + patientsList.size());
                } else {
                    Log.e(TAG, "Erro ao carregar pacientes: " + response.code());
                    setupPatientSpinnerFallback();
                }
            }
            
            @Override
            public void onFailure(Call<List<Patient>> call, Throwable t) {
                Log.e(TAG, "Falha na rede ao carregar pacientes", t);
                setupPatientSpinnerFallback();
            }
        });
    }

    private void setupPatientSpinnerFallback() {
        patientsList.clear();
        // Fallback apenas para não deixar o spinner vazio em caso de erro na API
        patientsList.add(new Patient(2, "aaaaa", "aaaaa@hotmail.com", "patient"));
        patientsList.add(new Patient(3, "cria", "cria@gmail.com", "patient"));
    }
    
    private void setupClickListeners() {
        if (cardAccountAvatar != null) {
            cardAccountAvatar.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        }
        if (cardPatients != null) {
            cardPatients.setOnClickListener(v -> startActivity(new Intent(this, PatientsListActivity.class)));
        }
        if (cardExercises != null) {
            cardExercises.setOnClickListener(v -> startActivity(new Intent(this, ExerciseListActivity.class)));
        }
        
        if (cardReports != null) {
            cardReports.setOnClickListener(v -> {
                Log.d(TAG, "Abrindo Relatórios...");
                startActivity(new Intent(this, PatientReportsActivity.class));
            });
        }

        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }
        if (btnCalendar != null) {
            btnCalendar.setOnClickListener(v -> startActivity(new Intent(this, CalendarActivity.class)));
        }
        if (fabLogout != null) {
            fabLogout.setOnClickListener(v -> logout());
        }
        if (fabAddTask != null) {
            fabAddTask.setOnClickListener(v -> showAddTaskDialog());
        }
    }

    private void showAddTaskDialog() {
        if (patientsList.isEmpty()) {
            loadPatientsForSpinner();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        
        // IDs sincronizados com o seu dialog_add_task.xml
        final EditText etTitle = view.findViewById(R.id.etTaskTitle);
        final EditText etDescription = view.findViewById(R.id.etTaskDescription);
        final EditText etPoints = view.findViewById(R.id.etTaskPoints);
        final Spinner spPatient = view.findViewById(R.id.spPatient);
        final Spinner spFrequency = view.findViewById(R.id.spFrequency);
        Button btnSave = view.findViewById(R.id.btnSaveTask);
        
        if (etTitle == null || etDescription == null || etPoints == null || spPatient == null || spFrequency == null || btnSave == null) {
            Log.e(TAG, "Componentes do diálogo não encontrados. Verifique IDs no XML.");
            Toast.makeText(this, "Erro ao carregar o formulário", Toast.LENGTH_SHORT).show();
            return;
        }

        // Setup Patients Spinner
        ArrayAdapter<Patient> patientAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, patientsList);
        patientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPatient.setAdapter(patientAdapter);

        // Setup Frequency Spinner
        String[] frequencyOptions = {"1 vez por semana", "2 vezes por semana", 
                                     "3 vezes por semana", "4 vezes por semana", 
                                     "5 vezes por semana", "6 vezes por semana", 
                                     "Todos os dias"};
        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, frequencyOptions);
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrequency.setAdapter(frequencyAdapter);

        builder.setView(view);
        taskDialog = builder.create();
        
        btnSave.setOnClickListener(v -> {
            String titleStr = etTitle.getText().toString().trim();
            String descStr = etDescription.getText().toString().trim();
            String pointsStr = etPoints.getText().toString().trim();
            
            if (titleStr.isEmpty()) {
                etTitle.setError("Título obrigatório");
                return;
            }
            if (descStr.isEmpty()) {
                etDescription.setError("Descrição obrigatória");
                return;
            }

            int points;
            try {
                points = Integer.parseInt(pointsStr);
            } catch (NumberFormatException e) {
                etPoints.setError("Pontos inválidos");
                return;
            }

            if (spPatient.getSelectedItem() == null) {
                Toast.makeText(this, "Selecione um paciente", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Patient selectedPatient = (Patient) spPatient.getSelectedItem();
            
            TaskCreateRequest request = new TaskCreateRequest();
            request.setPatient_id(selectedPatient.getId());
            request.setTitle(titleStr);
            request.setDescription(descStr);
            request.setPoints_value(points);
            request.setFrequency_per_week(spFrequency.getSelectedItemPosition() + 1);
            request.setStart_date(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
            
            Log.d(TAG, "Criando tarefa para paciente: " + selectedPatient.getId());
            saveTask(request);
        });
        
        taskDialog.show();
    }

    private void saveTask(TaskCreateRequest request) {
        TaskApi api = ApiClient.getTaskClient().create(TaskApi.class);
        api.createTask(tokenManager.getAuthToken(), request).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfessionalMainActivity.this, "Tarefa criada com sucesso!", Toast.LENGTH_SHORT).show();
                    if (taskDialog != null && taskDialog.isShowing()) {
                        taskDialog.dismiss();
                    }
                    carregarDadosPainel();
                } else {
                    Toast.makeText(ProfessionalMainActivity.this, "Erro API: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Toast.makeText(ProfessionalMainActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadUserInfo() {
        String email = tokenManager.getUserEmail();
        if (email != null && !email.isEmpty()) {
            String name = email.split("@")[0];
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            if (tvWelcome != null) tvWelcome.setText("Dr(a). " + name);
            if (tvUserInitial != null) tvUserInitial.setText(name.substring(0, 1).toUpperCase());
        }
    }
    
    private void logout() {
        tokenManager.clearToken();
        sessionManager.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
