package com.example.testbackend;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.testbackend.models.PatientReport;
import com.example.testbackend.models.ReportCreate;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.PatientReportApi;
import com.example.testbackend.utils.TokenManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateReportActivity extends AppCompatActivity {
    private static final String TAG = "CreateReportActivity";
    private static final int REQUEST_PERMISSION_CODE = 100;
    private static final int REQUEST_PICK_IMAGES_CODE = 101;
    private static final int MAX_IMAGES = 10;
    
    private Spinner spinnerReportType;
    private EditText editTitle;
    private EditText editContent;
    private EditText editClinicalEvolution;
    private SeekBar seekBarPainScale;
    private TextView tvPainScaleValue;
    private Button btnSave;
    private Button btnCancel;
    private Button btnAddImages;
    private LinearLayout llImagesContainer;
    private TextView tvImageCount;

    private PatientReportApi api;
    private TokenManager tokenManager;
    private int patientId = -1;
    
    private List<Uri> selectedImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);

        patientId = getIntent().getIntExtra("patient_id", -1);
        
        api = ApiClient.getAuthClient().create(PatientReportApi.class);
        tokenManager = new TokenManager(this);

        setupViews();
        setupSpinners();
        setupPainScale();
    }

    private void setupViews() {
        spinnerReportType = findViewById(R.id.spinnerReportType);
        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        editClinicalEvolution = findViewById(R.id.editClinicalEvolution);
        seekBarPainScale = findViewById(R.id.seekBarPainScale);
        tvPainScaleValue = findViewById(R.id.tvPainScaleValue);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnAddImages = findViewById(R.id.btnAddImages);
        llImagesContainer = findViewById(R.id.llImagesContainer);
        tvImageCount = findViewById(R.id.tvImageCount);

        btnSave.setOnClickListener(v -> saveReport());
        btnCancel.setOnClickListener(v -> finish());
        btnAddImages.setOnClickListener(v -> checkPermissionAndPickImages());
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.tipos_relatorio_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReportType.setAdapter(adapter);
    }

    private void setupPainScale() {
        seekBarPainScale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPainScaleValue.setText(progress + "/10");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private String getBackendType(String localizedType) {
        switch (localizedType) {
            case "Evolução": return "EVOLUTION";
            case "Avaliação": return "ASSESSMENT";
            case "Alta": return "DISCHARGE";
            case "Progresso": return "PROGRESS";
            case "Anamnese": return "ANAMNESIS";
            case "Reavaliação": return "REASSESSMENT";
            default: return localizedType;
        }
    }

    private void saveReport() {
        if (patientId == -1) {
            Toast.makeText(this, "ID do paciente não informado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerReportType.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione o tipo de relatório", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editTitle.getText().toString().trim().isEmpty()) {
            editTitle.setError("Título obrigatório");
            return;
        }

        ReportCreate report = new ReportCreate();
        report.setPatientId(patientId);
        
        // 🔥 OBTER ID DO PROFISSIONAL LOGADO
        int professionalId = tokenManager.getUserId();
        Log.d(TAG, "👤 Criando relatório para profissional ID: " + professionalId);
        
        report.setProfessionalId(professionalId);
        report.setReportDate(new Date());
        
        String selectedType = spinnerReportType.getSelectedItem().toString();
        report.setReportType(getBackendType(selectedType));
        
        report.setTitle(editTitle.getText().toString());
        report.setContent(editContent.getText().toString());
        report.setClinicalEvolution(editClinicalEvolution.getText().toString());
        report.setPainScale(seekBarPainScale.getProgress());
        report.setCreatedBy("professional");

        Log.d(TAG, "Enviando relatório para o paciente ID: " + patientId);

        api.createReport(report).enqueue(new Callback<PatientReport>() {
            @Override
            public void onResponse(Call<PatientReport> call, Response<PatientReport> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateReportActivity.this, "Relatório criado com sucesso", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMessage = "Erro ao criar relatório";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {}
                    Toast.makeText(CreateReportActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PatientReport> call, Throwable t) {
                Toast.makeText(CreateReportActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    // Métodos para lidar com imagens
    private void checkPermissionAndPickImages() {
        // Para Android 13+, usar READ_MEDIA_IMAGES
        // Para versões anteriores, usar READ_EXTERNAL_STORAGE
        String permission;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        
        if (ContextCompat.checkSelfPermission(this, permission) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                    new String[]{permission}, 
                    REQUEST_PERMISSION_CODE);
        } else {
            pickImages();
        }
    }
    
    private void pickImages() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Selecione imagens"), REQUEST_PICK_IMAGES_CODE);
        } catch (Exception e) {
            // Fallback para ACTION_PICK se ACTION_GET_CONTENT falhar
            try {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), REQUEST_PICK_IMAGES_CODE);
            } catch (Exception e2) {
                Toast.makeText(this, "Não foi possível abrir a galeria de imagens", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImages();
            } else {
                Toast.makeText(this, "Permissão necessária para acessar imagens", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_PICK_IMAGES_CODE && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                // Múltiplas imagens selecionadas
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count && selectedImages.size() < MAX_IMAGES; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    if (!selectedImages.contains(imageUri)) {
                        selectedImages.add(imageUri);
                    }
                }
            } else if (data.getData() != null) {
                // Uma imagem selecionada
                Uri imageUri = data.getData();
                if (!selectedImages.contains(imageUri) && selectedImages.size() < MAX_IMAGES) {
                    selectedImages.add(imageUri);
                }
            }
            
            updateImagesDisplay();
        }
    }
    
    private void updateImagesDisplay() {
        // Limpar container
        llImagesContainer.removeAllViews();
        
        // Adicionar miniaturas das imagens
        for (int i = 0; i < selectedImages.size(); i++) {
            View imageItem = createImageItem(selectedImages.get(i), i);
            llImagesContainer.addView(imageItem);
        }
        
        // Atualizar contador
        tvImageCount.setText(selectedImages.size() + " imagens selecionadas");
        
        // Habilitar/desabilitar botão se atingiu limite
        btnAddImages.setEnabled(selectedImages.size() < MAX_IMAGES);
    }
    
    private View createImageItem(Uri imageUri, int index) {
        // Criar layout para a imagem
        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                120, 120);
        params.setMargins(8, 0, 8, 0);
        
        android.widget.FrameLayout frameLayout = new android.widget.FrameLayout(this);
        frameLayout.setLayoutParams(params);
        
        // ImageView para a miniatura
        ImageView imageView = new ImageView(this);
        android.widget.FrameLayout.LayoutParams imageParams = new 
                android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageURI(imageUri);
        
        // Botão para remover imagem
        Button btnRemove = new Button(this);
        android.widget.FrameLayout.LayoutParams removeParams = new 
                android.widget.FrameLayout.LayoutParams(
                24, 24);
        removeParams.gravity = android.view.Gravity.TOP | android.view.Gravity.END;
        removeParams.setMargins(0, 4, 4, 0);
        btnRemove.setLayoutParams(removeParams);
        btnRemove.setText("×");
        btnRemove.setTextSize(12);
        btnRemove.setBackgroundColor(0xFFFF0000);
        btnRemove.setTextColor(0xFFFFFFFF);
        
        final int imageIndex = index;
        btnRemove.setOnClickListener(v -> {
            selectedImages.remove(imageIndex);
            updateImagesDisplay();
        });
        
        frameLayout.addView(imageView);
        frameLayout.addView(btnRemove);
        
        return frameLayout;
    }
}
