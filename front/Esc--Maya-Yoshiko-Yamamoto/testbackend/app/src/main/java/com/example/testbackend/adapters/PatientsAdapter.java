package com.example.testbackend.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.testbackend.ReportDetailActivity;
import com.example.testbackend.R;
import com.example.testbackend.models.Patient;
import java.util.List;

public class PatientsAdapter extends RecyclerView.Adapter<PatientsAdapter.PatientViewHolder> {

    private final List<Patient> patients;
    private final OnPatientClickListener listener;
    private final Context context;

    public interface OnPatientClickListener {
        void onPatientClick(Patient patient);
        void onPatientLongClick(Patient patient);
    }

    public PatientsAdapter(Context context, List<Patient> patients, OnPatientClickListener listener) {
        this.context = context;
        this.patients = patients;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_patient, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient patient = patients.get(position);
        
        String name = patient.getDisplayName();
        holder.tvName.setText(name);
        holder.tvEmail.setText(patient.getEmail());
        
        if (name != null && !name.isEmpty()) {
            holder.tvInitial.setText(name.substring(0, 1).toUpperCase());
        }

        // 🔥 CORREÇÃO DEFINITIVA: Clique no paciente leva direto para os Detalhes do Relatório
        holder.itemView.setOnClickListener(v -> {
            openPatientDetail(patient);
            if (listener != null) listener.onPatientClick(patient);
        });

        // O botão de ícone agora também leva para a página de detalhes (activity_report_detail)
        holder.btnReports.setOnClickListener(v -> openPatientDetail(patient));
    }

    private void openPatientDetail(Patient patient) {
        // Direcionando para ReportDetailActivity (que usa activity_report_detail.xml)
        Intent intent = new Intent(context, ReportDetailActivity.class);
        
        // Passamos o ID do paciente. Na tela de detalhes, ele carregará o relatório 
        // ou ficha de avaliação deste paciente específico.
        intent.putExtra("report_id", patient.getId()); 
        intent.putExtra("patient_name", patient.getDisplayName());
        
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return patients == null ? 0 : patients.size();
    }

    static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvInitial;
        ImageButton btnReports;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPatientName);
            tvEmail = itemView.findViewById(R.id.tvPatientEmail);
            tvInitial = itemView.findViewById(R.id.tvPatientInitial);
            btnReports = itemView.findViewById(R.id.btnReports);
        }
    }
}
