package com.example.testbackend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
        // Usando layout padrão do Android para teste rápido; pode ser personalizado depois.
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient patient = patients.get(position);
        holder.tvName.setText(patient.getDisplayName());
        holder.tvEmail.setText(patient.getEmail());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPatientClick(patient);
        });
    }

    @Override
    public int getItemCount() {
        return patients == null ? 0 : patients.size();
    }

    static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(android.R.id.text1);
            tvEmail = itemView.findViewById(android.R.id.text2);
        }
    }
}
