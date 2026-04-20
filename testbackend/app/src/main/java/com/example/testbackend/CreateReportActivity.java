package com.example.testbackend;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testbackend.models.PatientReport;
import com.example.testbackend.models.ReportCreate;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.PatientReportApi;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateReportActivity extends AppCompatActivity {
    private Spinner spinnerReportType;
    private EditText editTitle;
    private EditText editContent;
    private EditText editClinicalEvolution;
    private SeekBar seekBarPainScale;
    private TextView tvPainScaleValue;
    private Button btnSave;
    private Button btnCancel;

    private PatientReportApi api;
    private int professionalId = 37; // Default
    private int patientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);

        patientId = getIntent().getIntExtra("patient_id", -1);
        api = ApiClient.getAuthClient(this).create(PatientReportApi.class);

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

        btnSave.setOnClickListener(v -> saveReport());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void setupSpinners() {
        String[] types = {"EVOLUTION", "ASSESSMENT", "DISCHARGE", "PROGRESS"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
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

    private void saveReport() {
        if (editTitle.getText().toString().trim().isEmpty()) {
            editTitle.setError("Título obrigatório");
            return;
        }

        ReportCreate report = new ReportCreate();
        report.setPatientId(patientId);
        report.setProfessionalId(professionalId);
        report.setReportDate(new Date());
        report.setReportType(spinnerReportType.getSelectedItem().toString());
        report.setTitle(editTitle.getText().toString());
        report.setContent(editContent.getText().toString());
        report.setClinicalEvolution(editClinicalEvolution.getText().toString());
        report.setPainScale(seekBarPainScale.getProgress());
        report.setCreatedBy("professional");

        api.createReport(report).enqueue(new Callback<PatientReport>() {
            @Override
            public void onResponse(Call<PatientReport> call, Response<PatientReport> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateReportActivity.this, "Relatório criado com sucesso", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CreateReportActivity.this, "Erro ao criar relatório", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PatientReport> call, Throwable t) {
                Toast.makeText(CreateReportActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
