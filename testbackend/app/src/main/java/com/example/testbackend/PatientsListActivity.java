package com.example.testbackend;

import android.os.Bundle;
import android.util.Log;
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
import com.example.testbackend.utils.TokenManager;

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
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_list);
        
        tokenManager = new TokenManager(this);
        setupViews();
        api = ApiClient.getPatientClient().create(PatientApi.class);
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
        String token = tokenManager.getAuthToken();
        if (token == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        api.getPatients(token).enqueue(new Callback<List<Patient>>() {
            @Override
            public void onResponse(@NonNull Call<List<Patient>> call, @NonNull Response<List<Patient>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    patientList.clear();
                    patientList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Log.d("PATIENTS_LIST", "Carregados " + patientList.size() + " pacientes");
                } else {
                    Log.e("PATIENTS_LIST", "Erro: " + response.code());
                    Toast.makeText(PatientsListActivity.this, "Erro ao carregar pacientes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Patient>> call, @NonNull Throwable t) {
                Log.e("PATIENTS_LIST", "Falha na rede", t);
                Toast.makeText(PatientsListActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
