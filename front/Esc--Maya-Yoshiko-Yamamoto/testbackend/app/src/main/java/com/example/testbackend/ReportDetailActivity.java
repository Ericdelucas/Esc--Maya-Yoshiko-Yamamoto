package com.example.testbackend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.util.Log;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportDetailActivity extends AppCompatActivity {
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

        // 🔥 DEBUG LOGS
        Log.d("REPORT_DETAIL", "onCreate chamado");
        
        reportId = getIntent().getIntExtra("report_id", -1);
        Log.d("REPORT_DETAIL", "reportId recebido: " + reportId);
        
        if (reportId == -1) {
            Log.w("REPORT_DETAIL", "reportId inválido, usando padrão");
            Toast.makeText(this, "Usando relatório padrão para teste", Toast.LENGTH_SHORT).show();
            reportId = 1; // Usar ID padrão para teste em vez de fechar
        }
        
        Log.d("REPORT_DETAIL", "reportId final: " + reportId);

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
        progressBar.setVisibility(View.VISIBLE);
        
        api.getReportWithAttachments(reportId).enqueue(new Callback<PatientReport>() {
            @Override
            public void onResponse(Call<PatientReport> call, Response<PatientReport> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    currentReport = response.body();
                    populateFields();
                    loadAttachments();
                } else {
                    String errorMessage = getString(R.string.erro_carregar);
                    Toast.makeText(ReportDetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PatientReport> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ReportDetailActivity.this, getString(R.string.erro_conexao), Toast.LENGTH_LONG).show();
                finish();
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
        progressBar.setVisibility(View.VISIBLE);
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
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(ReportDetailActivity.this, "Enviado com sucesso!", Toast.LENGTH_SHORT).show();
                    loadAttachments();
                }
            }
            @Override
            public void onFailure(Call<List<ReportAttachment>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
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

    private String formatDateTimeBrazilian(Date date) {
        if (date == null) return "Não informado";
        try {
            SimpleDateFormat brazilianFormat = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault());
            return brazilianFormat.format(date);
        } catch (Exception e) {
            return date.toString();
        }
    }

    private void populateFields() {
        tvReportId.setText(getString(R.string.relatorio_numero, currentReport.getId()));
        tvPatientId.setText(getString(R.string.paciente) + ": " + currentReport.getPatientId());
        
        tvCreatedAt.setText(getString(R.string.criado_em) + ": " + formatDateTimeBrazilian(currentReport.getCreatedAt()));
        tvUpdatedAt.setText(getString(R.string.atualizado_em) + ": " + formatDateTimeBrazilian(currentReport.getUpdatedAt()));

        editTitle.setText(currentReport.getTitle());
        editContent.setText(currentReport.getContent());
        editClinicalEvolution.setText(currentReport.getClinicalEvolution());
        editObjectiveData.setText(currentReport.getObjectiveData());
        editSubjectiveData.setText(currentReport.getSubjectiveData());
        editTreatmentPlan.setText(currentReport.getTreatmentPlan());
        editRecommendations.setText(currentReport.getRecommendations());
        editNextSteps.setText(currentReport.getNextSteps());

        if (currentReport.getPainScale() != null) {
            seekBarPainScale.setProgress(currentReport.getPainScale());
            tvPainScaleValue.setText(String.valueOf(currentReport.getPainScale()));
        }

        if (currentReport.getFunctionalStatus() != null) {
            ArrayAdapter adapter = (ArrayAdapter) spinnerFunctionalStatus.getAdapter();
            int position = adapter.getPosition(currentReport.getFunctionalStatus());
            if (position >= 0) {
                spinnerFunctionalStatus.setSelection(position);
            }
        }
    }

    private void saveReport() {
        if (editTitle.getText().toString().trim().isEmpty()) {
            editTitle.setError(getString(R.string.titulo_obrigatorio));
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        ReportUpdate update = new ReportUpdate();
        update.setTitle(editTitle.getText().toString());
        update.setContent(editContent.getText().toString());
        update.setClinicalEvolution(editClinicalEvolution.getText().toString());
        update.setObjectiveData(editObjectiveData.getText().toString());
        update.setSubjectiveData(editSubjectiveData.getText().toString());
        update.setTreatmentPlan(editTreatmentPlan.getText().toString());
        update.setRecommendations(editRecommendations.getText().toString());
        update.setNextSteps(editNextSteps.getText().toString());
        update.setPainScale(seekBarPainScale.getProgress());
        update.setFunctionalStatus(spinnerFunctionalStatus.getSelectedItem().toString());

        api.updateReport(reportId, update).enqueue(new Callback<PatientReport>() {
            @Override
            public void onResponse(Call<PatientReport> call, Response<PatientReport> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    currentReport = response.body();
                    populateFields();
                    Toast.makeText(ReportDetailActivity.this, getString(R.string.relatorio_atualizado_sucesso), Toast.LENGTH_SHORT).show();
                } else {
                    String errorMessage = getString(R.string.erro_atualizar_relatorio);
                    Toast.makeText(ReportDetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PatientReport> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ReportDetailActivity.this, getString(R.string.erro_conexao), Toast.LENGTH_LONG).show();
            }
        });
    }
}
