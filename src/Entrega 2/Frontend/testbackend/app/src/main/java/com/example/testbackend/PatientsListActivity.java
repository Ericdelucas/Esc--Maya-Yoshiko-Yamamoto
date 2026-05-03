package com.example.testbackend;

import android.content.Intent;
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
    private TextView tvEmptyState;

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
        if (tvTitle != null) tvTitle.setText("Meus Pacientes");

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvPatients = findViewById(R.id.rvPatients);
        rvPatients.setLayoutManager(new LinearLayoutManager(this));
        
        // 🔥 REFERÊNCIA AO ESTADO VAZIO
        tvEmptyState = findViewById(R.id.tvEmptyState);
        
        // 🔥 CORREÇÃO: Implementando a navegação no clique
        adapter = new PatientsAdapter(this, patientList, new PatientsAdapter.OnPatientClickListener() {
            @Override
            public void onPatientClick(Patient patient) {
                // AGORA AO CLICAR, ABRE A TELA DE DADOS DE SAÚDE
                Intent intent = new Intent(PatientsListActivity.this, PatientHealthDetailsActivity.class);
                intent.putExtra("patient_id", patient.getId());
                intent.putExtra("patient_name", patient.getDisplayName());
                intent.putExtra("patient_email", patient.getEmail());
                startActivity(intent);
            }

            @Override
            public void onPatientLongClick(Patient patient) {
                // 🔥 IMPLEMENTAR DELEÇÃO DE PACIENTE
                showDeletePatientDialog(patient);
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
                    
                    // 🔥 ATUALIZAR ESTADO VAZIO
                    if (tvEmptyState != null) {
                        tvEmptyState.setVisibility(patientList.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                    
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
    
    private void showDeletePatientDialog(Patient patient) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Deletar Paciente")
            .setMessage("Tem certeza que deseja deletar o paciente \"" + patient.getDisplayName() + "\"?\n\nEsta ação não pode ser desfeita.")
            .setPositiveButton("Deletar", (dialog, which) -> deletePatient(patient))
            .setNegativeButton("Cancelar", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }
    
    private void deletePatient(Patient patient) {
        String token = tokenManager.getAuthToken();
        if (token == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Mostrar progresso
        android.app.AlertDialog progressDialog = new android.app.AlertDialog.Builder(this)
            .setTitle("Deletando...")
            .setMessage("Aguarde enquanto o paciente é deletado.")
            .setCancelable(false)
            .create();
        progressDialog.show();
        
        api.deletePatient(token, patient.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                progressDialog.dismiss();
                
                if (response.isSuccessful()) {
                    // 🔥 REMOVER PACIENTE DA LISTA COM ANIMAÇÃO
                    int position = patientList.indexOf(patient);
                    if (position != -1) {
                        patientList.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, patientList.size());
                    }
                    
                    Toast.makeText(PatientsListActivity.this, 
                        "Paciente \"" + patient.getDisplayName() + "\" deletado com sucesso!", 
                        Toast.LENGTH_LONG).show();
                    
                    Log.d("PATIENTS_LIST", "Paciente deletado: " + patient.getDisplayName());
                    
                    // 🔥 VERIFICAR ESTADO VAZIO
                    if (tvEmptyState != null) {
                        tvEmptyState.setVisibility(patientList.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                } else {
                    // 🔥 TRATAMENTO DE ERROS ESPECÍFICOS
                    String errorMsg = "Erro ao deletar paciente";
                    if (response.code() == 404) {
                        errorMsg = "Paciente não encontrado";
                    } else if (response.code() == 403) {
                        errorMsg = "Sem permissão para deletar paciente";
                    } else if (response.code() == 500) {
                        errorMsg = "Erro interno do servidor";
                    }
                    
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = "Erro: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("PATIENTS_LIST", "Erro ao parsear resposta", e);
                    }
                    
                    Toast.makeText(PatientsListActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e("PATIENTS_LIST", "Erro ao deletar: " + response.code());
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(PatientsListActivity.this, 
                    "Erro de conexão: " + t.getMessage(), 
                    Toast.LENGTH_LONG).show();
                Log.e("PATIENTS_LIST", "Falha na rede ao deletar paciente", t);
            }
        });
    }
}
