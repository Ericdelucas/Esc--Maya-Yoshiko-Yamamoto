package com.example.testbackend.fragments;

import android.os.Bundle;
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
    private int professionalId = 37;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_statistics, container, false);
        
        setupViews(view);
        loadStatistics();
        
        return view;
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
                    
                    // Simple average pain logic or from backend if available
                    tvAvgPainScale.setText("4.2"); // Placeholder or calculate if needed
                    
                    if (stats.getRecentReports() != null) {
                        recentReports.clear();
                        recentReports.addAll(stats.getRecentReports());
                        recentAdapter.notifyDataSetChanged();
                    }
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
