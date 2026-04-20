package com.example.testbackend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testbackend.models.PatientEvaluation;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.PatientEvaluationApi;
import com.example.testbackend.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientListActivity extends AppCompatActivity {
    
    private static final String TAG = "PATIENT_LIST_DEBUG";
    private ListView listViewPatients;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private ArrayAdapter<PatientEvaluation> adapter;
    private List<PatientEvaluation> evaluations = new ArrayList<>();
    private TokenManager tokenManager;
    private PatientEvaluationApi api;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);
        
        tokenManager = new TokenManager(this);
        api = ApiClient.getAuthClient().create(PatientEvaluationApi.class);
        
        setupViews();
        loadEvaluations();
    }
    
    private void setupViews() {
        listViewPatients = findViewById(R.id.listViewPatients);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, evaluations);
        listViewPatients.setAdapter(adapter);
        
        listViewPatients.setOnItemClickListener((parent, view, position, id) -> {
            PatientEvaluation evaluation = evaluations.get(position);
            openEvaluationActivity(evaluation);
        });
    }
    
    private void loadEvaluations() {
        String token = tokenManager.getAuthToken();
        if (token == null) {
            Toast.makeText(this, "Faça login primeiro", Toast.LENGTH_SHORT).show();
            return;
        }
        
        showLoading(true);
        
        // Obter ID do profissional (temporário hardcoded)
        int professionalId = getCurrentProfessionalId();
        
        Log.d(TAG, "Carregando avaliações para profissional ID: " + professionalId);
        
        api.getEvaluationsByProfessional(professionalId).enqueue(new Callback<List<PatientEvaluation>>() {
            @Override
            public void onResponse(Call<List<PatientEvaluation>> call, Response<List<PatientEvaluation>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    evaluations.clear();
                    evaluations.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    
                    Log.d(TAG, "✅ " + evaluations.size() + " avaliações carregadas");
                    updateEmptyView();
                    
                } else {
                    Log.e(TAG, "❌ Erro ao carregar avaliações: " + response.code());
                    Toast.makeText(PatientListActivity.this, "Erro ao carregar avaliações", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<List<PatientEvaluation>> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "❌ Falha na conexão", t);
                Toast.makeText(PatientListActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void openEvaluationActivity(PatientEvaluation evaluation) {
        Intent intent = new Intent(this, PatientEvaluationActivity.class);
        intent.putExtra("evaluation_id", evaluation.getId());
        intent.putExtra("patient_name", evaluation.getFullName());
        startActivity(intent);
    }
    
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        listViewPatients.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private void updateEmptyView() {
        if (tvEmpty != null) {
            tvEmpty.setVisibility(evaluations.isEmpty() ? View.VISIBLE : View.GONE);
            tvEmpty.setText(evaluations.isEmpty() ? 
                "Nenhuma ficha de avaliação encontrada.\n\nClique em '+' para adicionar nova ficha." : "");
        }
    }
    
    private int getCurrentProfessionalId() {
        // Por enquanto, usar ID fixo. Depois extrair do token
        return 37; // ID do profissional@novo.com
    }
}
