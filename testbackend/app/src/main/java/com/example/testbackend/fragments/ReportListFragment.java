package com.example.testbackend.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.testbackend.CreateReportActivity;
import com.example.testbackend.R;
import com.example.testbackend.ReportDetailActivity;
import com.example.testbackend.adapters.ReportAdapter;
import com.example.testbackend.models.PatientReport;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.PatientReportApi;
import com.example.testbackend.utils.TokenManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportListFragment extends Fragment {
    private static final String TAG = "ReportListFragment";
    private RecyclerView recyclerView;
    private ReportAdapter adapter;
    private List<PatientReport> reports = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton fabAddReport;
    private PatientReportApi api;
    private TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_list, container, false);
        
        api = ApiClient.getAuthClient().create(PatientReportApi.class);
        tokenManager = new TokenManager(getContext());
        
        setupViews(view);
        loadReports();
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadReports(); // Refresh when returning from Detail/Edit or Create activity
    }

    private void setupViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewReports);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        fabAddReport = view.findViewById(R.id.fabAddReport);

        adapter = new ReportAdapter(reports, this::onReportClick, this::onReportLongClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadReports);
        
        if (fabAddReport != null) {
            fabAddReport.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), CreateReportActivity.class);
                intent.putExtra("patient_id", 1); 
                startActivity(intent);
            });
        }
    }

    private void loadReports() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        
        // 🔥 OBTER ID DO PROFISSIONAL LOGADO
        int professionalId = tokenManager.getUserId();
        Log.d(TAG, "🔍 Carregando relatórios para profissional ID: " + professionalId);
        
        // 🔥 DEBUG: Mostrar token e ID
        String token = tokenManager.getAuthToken();
        Log.d(TAG, "🔑 Token: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
        Log.d(TAG, "👤 User ID do token: " + professionalId);
        
        api.getProfessionalReports(professionalId).enqueue(new Callback<List<PatientReport>>() {
            @Override
            public void onResponse(Call<List<PatientReport>> call, Response<List<PatientReport>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    reports.clear();
                    reports.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    
                    Log.d(TAG, "📊 Relatórios recebidos: " + reports.size());
                    for (PatientReport report : reports) {
                        Log.d(TAG, "📋 Relatório: " + report.getTitle() + ", Patient ID: " + report.getPatientId());
                    }
                    
                    if (tvEmptyState != null) {
                        tvEmptyState.setVisibility(reports.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                } else {
                    Log.e(TAG, "❌ Erro na resposta: " + response.code());
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Erro ao carregar relatórios", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PatientReport>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Erro de conexão", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onReportClick(PatientReport report) {
        Intent intent = new Intent(getContext(), ReportDetailActivity.class);
        intent.putExtra("report_id", report.getId());
        startActivity(intent);
    }

    private void onReportLongClick(PatientReport report) {
        new AlertDialog.Builder(getContext())
            .setTitle("Excluir Relatório")
            .setMessage("Tem certeza que deseja excluir o relatório \"" + report.getTitle() + "\"?")
            .setPositiveButton("Excluir", (dialog, which) -> deleteReport(report))
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void deleteReport(PatientReport report) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        
        Log.d(TAG, "Excluindo relatório ID: " + report.getId());
        
        api.deleteReport(report.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful()) {
                    int position = reports.indexOf(report);
                    if (position != -1) {
                        reports.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, reports.size());
                    }
                    
                    Toast.makeText(getContext(), "Relatório excluído com sucesso", Toast.LENGTH_SHORT).show();
                    
                    if (tvEmptyState != null) {
                        tvEmptyState.setVisibility(reports.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                } else {
                    Toast.makeText(getContext(), "Erro ao excluir relatório", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
