package com.example.testbackend.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.R;
import com.example.testbackend.adapters.ReportAdapter;
import com.example.testbackend.models.PatientReport;
import com.example.testbackend.models.ReportStatistics;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.PatientReportApi;
import com.example.testbackend.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportStatisticsFragment extends Fragment {
    private TextView tvTotalReports;
    private TextView tvEvolutionReports;
    private TextView tvAssessmentReports;
    private TextView tvAvgPainScale;
    private RecyclerView recyclerViewRecent;
    private ReportAdapter recentAdapter;
    private List<PatientReport> recentReports = new ArrayList<>();
    private TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_statistics, container, false);
        
        tokenManager = new TokenManager(getContext());
        setupViews(view);
        loadStatistics();
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 🔥 REFRESH ESTATÍSTICAS QUANDO RETORNAR
        loadStatistics();
    }

    private void setupViews(View view) {
        tvTotalReports = view.findViewById(R.id.tvTotalReports);
        tvEvolutionReports = view.findViewById(R.id.tvEvolutionReports);
        tvAssessmentReports = view.findViewById(R.id.tvAssessmentReports);
        tvAvgPainScale = view.findViewById(R.id.tvAvgPainScale);
        recyclerViewRecent = view.findViewById(R.id.recyclerViewRecent);

        recentAdapter = new ReportAdapter(recentReports, report -> {}, report -> {});
        recyclerViewRecent.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewRecent.setAdapter(recentAdapter);
    }

    private void loadStatistics() {
        PatientReportApi api = ApiClient.getAuthClient().create(PatientReportApi.class);
        
        // 🔥 OBTER ID DO PROFISSIONAL LOGADO
        int professionalId = tokenManager.getUserId();
        android.util.Log.d("ReportStatistics", "🔍 Carregando estatísticas para profissional ID: " + professionalId);
        
        api.getStatistics(professionalId).enqueue(new Callback<ReportStatistics>() {
            @Override
            public void onResponse(Call<ReportStatistics> call, Response<ReportStatistics> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReportStatistics stats = response.body();
                    
                    tvTotalReports.setText(String.valueOf(stats.getTotalReports()));
                    
                    Map<String, Object> reportTypes = stats.getReportTypes();
                    if (reportTypes != null) {
                        if (reportTypes.containsKey("EVOLUTION")) {
                            Object evolutionObj = reportTypes.get("EVOLUTION");
                            if (evolutionObj instanceof Map) {
                                tvEvolutionReports.setText(String.valueOf(((Map<?, ?>) evolutionObj).get("count")));
                            }
                        }
                        if (reportTypes.containsKey("ASSESSMENT")) {
                            Object assessmentObj = reportTypes.get("ASSESSMENT");
                            if (assessmentObj instanceof Map) {
                                tvAssessmentReports.setText(String.valueOf(((Map<?, ?>) assessmentObj).get("count")));
                            }
                        }
                    }
                    
                    // 🔥 CALCULAR MÉDIA DE DOR CORRETAMENTE
                    double totalPain = 0;
                    int totalReportsWithPain = 0;
                    
                    if (reportTypes != null) {
                        for (Map.Entry<String, Object> entry : reportTypes.entrySet()) {
                            Object typeData = entry.getValue();
                            if (typeData instanceof Map) {
                                Map<?, ?> typeMap = (Map<?, ?>) typeData;
                                Object avgPain = typeMap.get("avg_pain");
                                Object count = typeMap.get("count");
                                
                                if (avgPain != null && count != null) {
                                    totalPain += ((Number) avgPain).doubleValue() * ((Number) count).intValue();
                                    totalReportsWithPain += ((Number) count).intValue();
                                }
                            }
                        }
                    }
                    
                    double avgPain = totalReportsWithPain > 0 ? totalPain / totalReportsWithPain : 0;
                    tvAvgPainScale.setText(String.format("%.1f", avgPain));
                    
                    android.util.Log.d("ReportStatistics", "📊 Estatísticas - Total: " + stats.getTotalReports() + 
                        ", Média Dor: " + avgPain + ", Relatórios Recentes: " + 
                        (stats.getRecentReports() != null ? stats.getRecentReports().size() : 0));
                    
                    if (stats.getRecentReports() != null) {
                        recentReports.clear();
                        recentReports.addAll(stats.getRecentReports());
                        recentAdapter.notifyDataSetChanged();
                    }
                } else {
                    android.util.Log.e("ReportStatistics", "❌ Erro ao carregar estatísticas: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ReportStatistics> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Erro ao carregar estatísticas", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
