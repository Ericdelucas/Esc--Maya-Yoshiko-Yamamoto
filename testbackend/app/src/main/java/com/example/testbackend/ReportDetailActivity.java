package com.example.testbackend;

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

import androidx.appcompat.app.AppCompatActivity;

import com.example.testbackend.models.PatientReport;
import com.example.testbackend.models.ReportUpdate;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.PatientReportApi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    private PatientReportApi api;
    private PatientReport currentReport;
    private int reportId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        reportId = getIntent().getIntExtra("report_id", -1);
        if (reportId == -1) {
            Toast.makeText(this, "ID do relatório não informado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        api = ApiClient.getAuthClient().create(PatientReportApi.class);
        setupViews();
        setupSpinners();
        setupPainScale();
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

        btnSave.setOnClickListener(v -> saveReport());
        btnCancel.setOnClickListener(v -> finish());
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

    private void loadReport() {
        progressBar.setVisibility(View.VISIBLE);
        
        api.getReport(reportId).enqueue(new Callback<PatientReport>() {
            @Override
            public void onResponse(Call<PatientReport> call, Response<PatientReport> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    currentReport = response.body();
                    populateFields();
                } else {
                    String errorMessage = "Erro ao carregar relatório";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = "Erro: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        // # Manter mensagem padrão
                    }
                    Toast.makeText(ReportDetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PatientReport> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ReportDetailActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void populateFields() {
        tvReportId.setText("Relatório #" + currentReport.getId());
        tvPatientId.setText("Paciente ID: " + currentReport.getPatientId());
        
        // Handling both possible String or Date types for CreatedAt/UpdatedAt
        tvCreatedAt.setText("Criado em: " + currentReport.getCreatedAt());
        tvUpdatedAt.setText("Atualizado em: " + currentReport.getUpdatedAt());

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
            editTitle.setError("Título obrigatório");
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
                    Toast.makeText(ReportDetailActivity.this, "Relatório atualizado com sucesso", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMessage = "Erro ao atualizar relatório";
                    try {
                        if (response.code() == 422) {
                            errorMessage = "Erro de validação: " + response.errorBody().string();
                        } else if (response.errorBody() != null) {
                            errorMessage = "Erro: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        // # Manter mensagem padrão
                    }
                    Toast.makeText(ReportDetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PatientReport> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ReportDetailActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
