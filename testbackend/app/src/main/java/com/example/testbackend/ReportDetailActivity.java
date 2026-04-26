package com.example.testbackend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.adapters.ReportAttachmentAdapter;
import com.example.testbackend.models.PatientReport;
import com.example.testbackend.models.ReportAttachment;
import com.example.testbackend.models.ReportAttachmentList;
import com.example.testbackend.models.ReportUpdate;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.PatientReportApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportDetailActivity extends AppCompatActivity {
    private static final String TAG = "REPORT_DETAIL";
    private TextView tvReportId;
    private TextView tvPatientId;
    private TextView tvCreatedAt;
    private TextView tvUpdatedAt;
    private EditText editTitle;
    private EditText editContent;
    private EditText editClinicalEvolution;
    private EditText editObjectiveData;
    private EditText editSubjectiveData;
    private EditText editTreatmentPlan;
    private EditText editRecommendations;
    private EditText editNextSteps;
    private Spinner spinnerFunctionalStatus;
    private SeekBar seekBarPainScale;
    private TextView tvPainScaleValue;
    private Button btnSave;
    private Button btnCancel;
    private ProgressBar progressBar;

    // Anexos
    private RecyclerView recyclerAttachments;
    private TextView tvNoAttachments;
    private Button btnAddAttachment;
    private ReportAttachmentAdapter attachmentAdapter;
    private List<ReportAttachment> attachments = new ArrayList<>();
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private PatientReportApi api;
    private PatientReport currentReport;
    private int reportId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        Log.d(TAG, "onCreate chamado");

        reportId = getIntent().getIntExtra("report_id", -1);
        Log.d(TAG, "reportId recebido: " + reportId);

        if (reportId == -1) {
            Log.w(TAG, "reportId inválido, usando padrão 1 para teste");
            Toast.makeText(this, "Usando relatório padrão para teste", Toast.LENGTH_SHORT).show();
            reportId = 1; // ✅ ID padrão para teste conforme o guia
        }

        api = ApiClient.getAuthClient().create(PatientReportApi.class);
        setupViews();
        setupSpinners();
        setupPainScale();
        setupAttachmentPicker();
        loadReport();
    }

    private void setupViews() {
        tvReportId = findViewById(R.id.tvReportId);
        tvPatientId = findViewById(R.id.tvPatientId);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvUpdatedAt = findViewById(R.id.tvUpdatedAt);
        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        editClinicalEvolution = findViewById(R.id.editClinicalEvolution);
        editObjectiveData = findViewById(R.id.editObjectiveData);
        editSubjectiveData = findViewById(R.id.editSubjectiveData);
        editTreatmentPlan = findViewById(R.id.editTreatmentPlan);
        editRecommendations = findViewById(R.id.editRecommendations);
        editNextSteps = findViewById(R.id.editNextSteps);
        spinnerFunctionalStatus = findViewById(R.id.spinnerFunctionalStatus);
        seekBarPainScale = findViewById(R.id.seekBarPainScale);
        tvPainScaleValue = findViewById(R.id.tvPainScaleValue);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        recyclerAttachments = findViewById(R.id.recyclerAttachments);
        tvNoAttachments = findViewById(R.id.tvNoAttachments);
        btnAddAttachment = findViewById(R.id.btnAddAttachment);

        btnSave.setOnClickListener(v -> saveReport());
        btnCancel.setOnClickListener(v -> finish());
        btnAddAttachment.setOnClickListener(v -> openImagePicker());

        // Setup RecyclerView
        recyclerAttachments.setLayoutManager(new LinearLayoutManager(this));
        attachmentAdapter = new ReportAttachmentAdapter(this, attachments, 
            new ReportAttachmentAdapter.OnAttachmentClickListener() {
                @Override
                public void onViewClick(ReportAttachment attachment) {
                    openAttachment(attachment);
                }
                
                @Override
                public void onDeleteClick(ReportAttachment attachment) {
                    deleteAttachment(attachment);
                }
            });
        recyclerAttachments.setAdapter(attachmentAdapter);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
            this, R.array.functional_status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFunctionalStatus.setAdapter(adapter);
    }

    private void setupPainScale() {
        seekBarPainScale.setMax(10);
        seekBarPainScale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPainScaleValue.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupAttachmentPicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    List<Uri> selectedImages = new ArrayList<>();
                    
                    if (data.getClipData() != null) {
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            selectedImages.add(data.getClipData().getItemAt(i).getUri());
                        }
                    } else if (data.getData() != null) {
                        selectedImages.add(data.getData());
                    }
                    
                    uploadImages(selectedImages);
                }
            }
        );
    }

    private void loadReport() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        
        // Tentar usar endpoint básico primeiro
        api.getReport(reportId).enqueue(new Callback<PatientReport>() {
            @Override
            public void onResponse(Call<PatientReport> call, Response<PatientReport> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    currentReport = response.body();
                    populateFields();
                    loadAttachments();
                } else {
                    Log.e(TAG, "Erro ao carregar relatório: " + response.code());
                    // Se falhar, tentar buscar da lista de relatórios do profissional
                    loadReportFromProfessionalList();
                }
            }

            @Override
            public void onFailure(Call<PatientReport> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Falha na conexão", t);
                // Se falhar, tentar buscar da lista
                loadReportFromProfessionalList();
            }
        });
    }
    
    private void loadReportFromProfessionalList() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        
        // Buscar todos os relatórios do profissional e encontrar o específico
        api.getProfessionalReports(37).enqueue(new Callback<List<PatientReport>>() {
            @Override
            public void onResponse(Call<List<PatientReport>> call, Response<List<PatientReport>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<PatientReport> reports = response.body();
                    PatientReport foundReport = null;
                    
                    // Procurar o relatório com o ID específico
                    for (PatientReport report : reports) {
                        if (report.getId() == reportId) {
                            foundReport = report;
                            break;
                        }
                    }
                    
                    if (foundReport != null) {
                        currentReport = foundReport;
                        populateFields();
                        loadAttachments();
                    } else {
                        Log.e(TAG, "Relatório ID " + reportId + " não encontrado na lista");
                        Toast.makeText(ReportDetailActivity.this, "Relatório não encontrado", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Log.e(TAG, "Erro ao carregar lista de relatórios: " + response.code());
                    Toast.makeText(ReportDetailActivity.this, "Erro ao carregar relatórios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PatientReport>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Falha na conexão", t);
                Toast.makeText(ReportDetailActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAttachments() {
        api.getReportAttachments(reportId).enqueue(new Callback<ReportAttachmentList>() {
            @Override
            public void onResponse(Call<ReportAttachmentList> call, Response<ReportAttachmentList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    attachments.clear();
                    attachments.addAll(response.body().getAttachments());
                    attachmentAdapter.updateAttachments(attachments);
                    
                    if (attachments.isEmpty()) {
                        tvNoAttachments.setVisibility(View.VISIBLE);
                        recyclerAttachments.setVisibility(View.GONE);
                    } else {
                        tvNoAttachments.setVisibility(View.GONE);
                        recyclerAttachments.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onFailure(Call<ReportAttachmentList> call, Throwable t) {}
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Selecione imagens"));
    }

    private void uploadImages(List<Uri> imageUris) {
        if (imageUris.isEmpty()) return;
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        List<MultipartBody.Part> fileParts = new ArrayList<>();
        
        for (int i = 0; i < imageUris.size(); i++) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUris.get(i));
                File tempFile = new File(getCacheDir(), "temp_image_" + i + ".jpg");
                FileOutputStream outputStream = new FileOutputStream(tempFile);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                outputStream.close();
                RequestBody requestFile = RequestBody.create(tempFile, MediaType.parse("image/*"));
                MultipartBody.Part part = MultipartBody.Part.createFormData("files", tempFile.getName(), requestFile);
                fileParts.add(part);
            } catch (Exception e) { e.printStackTrace(); }
        }
        
        RequestBody description = RequestBody.create("Anexo de relatório", MediaType.parse("text/plain"));
        api.uploadAttachments(reportId, fileParts, description).enqueue(new Callback<List<ReportAttachment>>() {
            @Override
            public void onResponse(Call<List<ReportAttachment>> call, Response<List<ReportAttachment>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(ReportDetailActivity.this, "Enviado com sucesso!", Toast.LENGTH_SHORT).show();
                    loadAttachments();
                }
            }
            @Override
            public void onFailure(Call<List<ReportAttachment>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void openAttachment(ReportAttachment attachment) {
        String downloadUrl = "http://localhost:8080/reports/" + attachment.getReportId() + "/attachments/" + attachment.getId() + "/download";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
        startActivity(intent);
    }

    private void deleteAttachment(ReportAttachment attachment) {
        api.deleteAttachment(reportId, attachment.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    loadAttachments();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    private String formatDateTimeBrazilian(String dateString) {
        if (dateString == null) return "Não informado";
        try {
            // Tentar parse da string de data ISO
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat brazilianFormat = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault());
            Date date = isoFormat.parse(dateString);
            return brazilianFormat.format(date);
        } catch (Exception e) {
            return dateString; // Retorna a string original se não conseguir parsear
        }
    }

    private void populateFields() {
        if (tvReportId != null) tvReportId.setText("Relatório #" + currentReport.getId());
        if (tvPatientId != null) tvPatientId.setText("Paciente ID: " + currentReport.getPatientId());
        
        if (tvCreatedAt != null) tvCreatedAt.setText("Criado em: " + formatDateTimeBrazilian(currentReport.getCreatedAt()));
        if (tvUpdatedAt != null) tvUpdatedAt.setText("Atualizado em: " + formatDateTimeBrazilian(currentReport.getUpdatedAt()));

        if (editTitle != null) editTitle.setText(currentReport.getTitle());
        if (editContent != null) editContent.setText(currentReport.getContent());
        if (editClinicalEvolution != null) editClinicalEvolution.setText(currentReport.getClinicalEvolution());
        if (editObjectiveData != null) editObjectiveData.setText(currentReport.getObjectiveData());
        if (editSubjectiveData != null) editSubjectiveData.setText(currentReport.getSubjectiveData());
        if (editTreatmentPlan != null) editTreatmentPlan.setText(currentReport.getTreatmentPlan());
        if (editRecommendations != null) editRecommendations.setText(currentReport.getRecommendations());
        if (editNextSteps != null) editNextSteps.setText(currentReport.getNextSteps());

        if (currentReport.getPainScale() != null) {
            if (seekBarPainScale != null) seekBarPainScale.setProgress(currentReport.getPainScale());
            if (tvPainScaleValue != null) tvPainScaleValue.setText(String.valueOf(currentReport.getPainScale()));
        }

        if (currentReport.getFunctionalStatus() != null && spinnerFunctionalStatus != null) {
            ArrayAdapter adapter = (ArrayAdapter) spinnerFunctionalStatus.getAdapter();
            int position = adapter.getPosition(currentReport.getFunctionalStatus());
            if (position >= 0) {
                spinnerFunctionalStatus.setSelection(position);
            }
        }
    }

    private void saveReport() {
        if (editTitle != null && editTitle.getText().toString().trim().isEmpty()) {
            editTitle.setError("Título obrigatório");
            return;
        }

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        ReportUpdate update = new ReportUpdate();
        if (editTitle != null) update.setTitle(editTitle.getText().toString());
        if (editContent != null) update.setContent(editContent.getText().toString());
        if (editClinicalEvolution != null) update.setClinicalEvolution(editClinicalEvolution.getText().toString());
        if (editObjectiveData != null) update.setObjectiveData(editObjectiveData.getText().toString());
        if (editSubjectiveData != null) update.setSubjectiveData(editSubjectiveData.getText().toString());
        if (editTreatmentPlan != null) update.setTreatmentPlan(editTreatmentPlan.getText().toString());
        if (editRecommendations != null) update.setRecommendations(editRecommendations.getText().toString());
        if (editNextSteps != null) update.setNextSteps(editNextSteps.getText().toString());
        if (seekBarPainScale != null) update.setPainScale(seekBarPainScale.getProgress());
        if (spinnerFunctionalStatus != null) update.setFunctionalStatus(spinnerFunctionalStatus.getSelectedItem().toString());

        api.updateReport(reportId, update).enqueue(new Callback<PatientReport>() {
            @Override
            public void onResponse(Call<PatientReport> call, Response<PatientReport> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    currentReport = response.body();
                    populateFields();
                    Toast.makeText(ReportDetailActivity.this, "Relatório atualizado com sucesso", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ReportDetailActivity.this, "Erro ao atualizar relatório", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PatientReport> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(ReportDetailActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
