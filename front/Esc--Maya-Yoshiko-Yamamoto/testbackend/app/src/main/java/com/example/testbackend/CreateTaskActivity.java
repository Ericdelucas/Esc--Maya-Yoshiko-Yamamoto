package com.example.testbackend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testbackend.models.Patient;
import com.example.testbackend.models.Task;
import com.example.testbackend.models.TaskCreateRequest;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.PatientApi;
import com.example.testbackend.network.TaskApi;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTaskActivity extends AppCompatActivity {
    private TextInputEditText etTitle, etDescription;
    private Spinner spPatient, spFrequency;
    private Button btnSave, btnSelectImage, btnSelectVideo;
    private ImageView ivImagePreview;
    private TextView tvVideoSelected;
    private TaskApi taskApi;
    private PatientApi patientApi;
    private List<Patient> patientsList = new ArrayList<>();
    private String token;
    
    // 🔥 VARIÁVEIS PARA MÍDIA
    private Uri selectedImageUri;
    private Uri selectedVideoUri;
    
    // 🔥 REQUEST CODES
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
        token = prefs.getString("jwt_token", "");

        taskApi = ApiClient.getTaskClient().create(TaskApi.class);
        // CORREÇÃO: Usar getPatientClient e AUTH_BASE_URL (porta 8080)
        patientApi = ApiClient.getPatientClient().create(PatientApi.class);

        initViews();
        setupFrequencySpinner();
        loadPatients();
        setupClickListeners();
    }

    private void initViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        spPatient = findViewById(R.id.spPatient);
        spFrequency = findViewById(R.id.spFrequency);
        btnSave = findViewById(R.id.btnSave);
        
        // 🔥 INICIAR VIEWS DE MÍDIA
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSelectVideo = findViewById(R.id.btnSelectVideo);
        ivImagePreview = findViewById(R.id.ivImagePreview);
        tvVideoSelected = findViewById(R.id.tvVideoSelected);
    }

    private void setupFrequencySpinner() {
        String[] frequencyOptions = {
                "1 vez por semana",
                "2 vezes por semana",
                "3 vezes por semana",
                "4 vezes por semana",
                "5 vezes por semana",
                "6 vezes por semana",
                "Todos os dias"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, frequencyOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrequency.setAdapter(adapter);
    }

    private void loadPatients() {
        // CORREÇÃO: Usar getPatients (endpoint /professional/pacientes)
        patientApi.getPatients("Bearer " + token).enqueue(new Callback<List<Patient>>() {
            @Override
            public void onResponse(Call<List<Patient>> call, Response<List<Patient>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    patientsList = response.body();
                    
                    Log.d("PATIENTS_LOADED", "Carregados " + patientsList.size() + " pacientes:");
                    for (Patient p : patientsList) {
                        Log.d("PATIENT", "ID: " + p.getId() + ", Nome: " + p.getDisplayName());
                    }
                    
                    // Configurar spinner diretamente com lista de pacientes (usa o toString() do model)
                    ArrayAdapter<Patient> adapter = new ArrayAdapter<>(CreateTaskActivity.this,
                            android.R.layout.simple_spinner_item, patientsList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPatient.setAdapter(adapter);
                } else {
                    Log.e("PATIENTS_ERROR", "Erro ao carregar pacientes: " + response.code());
                    Toast.makeText(CreateTaskActivity.this, "Erro ao carregar pacientes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Patient>> call, Throwable t) {
                Log.e("PATIENTS_ERROR", "Falha na requisição", t);
                Toast.makeText(CreateTaskActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> createTask());
        
        // 🔥 LISTENERS DE MÍDIA
        btnSelectImage.setOnClickListener(v -> selectImage());
        btnSelectVideo.setOnClickListener(v -> selectVideo());
    }

    private void createTask() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        int points = 15; // 🔥 PONTOS FIXOS AUTOMÁTICOS

        // VALIDAÇÃO DETALHADA:
        if (title.isEmpty()) {
            etTitle.setError("Título obrigatório");
            return;
        }
        if (title.length() > 120) {
            etTitle.setError("Título muito longo (máx 120)");
            return;
        }
        
        if (description.isEmpty()) {
            etDescription.setError("Descrição obrigatória");
            return;
        }
        if (description.length() > 500) {
            etDescription.setError("Descrição muito longa (máx 500)");
            return;
        }

        if (patientsList.isEmpty() || spPatient.getSelectedItemPosition() == -1) {
            Toast.makeText(this, "Selecione um paciente", Toast.LENGTH_SHORT).show();
            return;
        }

        Patient selectedPatient = (Patient) spPatient.getSelectedItem();
        int frequency = spFrequency.getSelectedItemPosition() + 1;
        String startDateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        TaskCreateRequest request = new TaskCreateRequest();
        request.setPatient_id(selectedPatient.getId());
        request.setTitle(title);
        request.setDescription(description);
        request.setPoints_value(points);
        request.setFrequency_per_week(frequency);
        request.setStart_date(startDateStr);
        
        // 🔥 ADICIONAR URLs DE MÍDIA
        if (selectedImageUri != null) {
            request.setExerciseImageUrl(selectedImageUri.toString());
            Log.d("TASK_DEBUG", "Imagem URL: " + selectedImageUri.toString());
        }
        
        if (selectedVideoUri != null) {
            request.setExerciseVideoUrl(selectedVideoUri.toString());
            Log.d("TASK_DEBUG", "Vídeo URL: " + selectedVideoUri.toString());
        }

        // DEBUG: mostrar o que está sendo enviado para identificar Erro 422
        Log.d("TASK_DEBUG", "=== DADOS DA TAREFA ===");
        Log.d("TASK_DEBUG", "patient_id: " + request.getPatient_id());
        Log.d("TASK_DEBUG", "title: '" + request.getTitle() + "'");
        Log.d("TASK_DEBUG", "description: '" + request.getDescription() + "'");
        Log.d("TASK_DEBUG", "points_value: " + request.getPoints_value());
        Log.d("TASK_DEBUG", "frequency_per_week: " + request.getFrequency_per_week());
        Log.d("TASK_DEBUG", "start_date: " + request.getStart_date());
        Log.d("TASK_DEBUG", "exercise_image_url: " + request.getExerciseImageUrl());
        Log.d("TASK_DEBUG", "exercise_video_url: " + request.getExerciseVideoUrl());
        Log.d("TASK_DEBUG", "========================");

        taskApi.createTask("Bearer " + token, request).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                Log.d("TASK_RESPONSE", "Response code: " + response.code());

                if (response.isSuccessful()) {
                    Toast.makeText(CreateTaskActivity.this, "Tarefa criada com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e("TASK_ERROR", "=== ERRO DETALHADO ===");
                    Log.e("TASK_ERROR", "Code: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e("TASK_ERROR", "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e("TASK_ERROR", "Failed to read error body", e);
                    }
                    Log.e("TASK_ERROR", "======================");
                    
                    Toast.makeText(CreateTaskActivity.this, 
                        "Erro " + response.code() + ": Verifique os logs no Logcat", 
                        Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Log.e("TASK_ERROR", "Falha na conexão", t);
                Toast.makeText(CreateTaskActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    // 🔥 MÉTODOS PARA SELECIONAR MÍDIA
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    
    private void selectVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            
            if (requestCode == PICK_IMAGE_REQUEST) {
                selectedImageUri = uri;
                // Mostrar preview da imagem
                ivImagePreview.setVisibility(View.VISIBLE);
                ivImagePreview.setImageURI(uri);
                Toast.makeText(this, "Imagem selecionada", Toast.LENGTH_SHORT).show();
                
            } else if (requestCode == PICK_VIDEO_REQUEST) {
                selectedVideoUri = uri;
                // Mostrar nome do vídeo
                tvVideoSelected.setVisibility(View.VISIBLE);
                String videoName = "Vídeo selecionado: " + uri.getLastPathSegment();
                tvVideoSelected.setText(videoName);
                Toast.makeText(this, "Vídeo selecionado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
