package com.example.testbackend;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.adapters.PatientsAdapter;
import com.example.testbackend.models.Patient;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.PatientApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientsListActivity extends AppCompatActivity {
    private RecyclerView rvPatients;
    private PatientsAdapter adapter;
    private List<Patient> patientList = new ArrayList<>();
    private PatientApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_list);
        
        setupViews();
        api = ApiClient.getAuthClient(this).create(PatientApi.class);
        loadPatients();
    }

    private void setupViews() {
        TextView tvTitle = findViewById(R.id.tvTitle);
        if (tvTitle != null) {
            tvTitle.setText("Meus Pacientes");
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvPatients = findViewById(R.id.rvPatients);
        rvPatients.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new PatientsAdapter(this, patientList, new PatientsAdapter.OnPatientClickListener() {
            @Override
            public void onPatientClick(Patient patient) {
                // Navegação opcional para perfil do paciente
            }

            @Override
            public void onPatientLongClick(Patient patient) {
                // Menu opcional
            }
        });
        rvPatients.setAdapter(adapter);
    }

    private void loadPatients() {
        api.getPacientes().enqueue(new Callback<PatientApi.PacientesResponse>() {
            @Override
            public void onResponse(@NonNull Call<PatientApi.PacientesResponse> call, @NonNull Response<PatientApi.PacientesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    patientList.clear();
                    if (response.body().pacientes != null) {
                        patientList.addAll(response.body().pacientes);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(PatientsListActivity.this, "Erro ao carregar pacientes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PatientApi.PacientesResponse> call, @NonNull Throwable t) {
                Toast.makeText(PatientsListActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
