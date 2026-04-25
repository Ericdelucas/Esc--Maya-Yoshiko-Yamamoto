package com.example.testbackend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.R;
import com.example.testbackend.models.PatientReport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
    private List<PatientReport> reports;
    private OnReportClickListener clickListener;
    private OnReportLongClickListener longClickListener;

    public interface OnReportClickListener {
        void onClick(PatientReport report);
    }

    public interface OnReportLongClickListener {
        void onLongClick(PatientReport report);
    }

    public ReportAdapter(List<PatientReport> reports, OnReportClickListener clickListener, OnReportLongClickListener longClickListener) {
        this.reports = reports;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PatientReport report = reports.get(position);

        holder.tvTitle.setText(report.getTitle());
        holder.tvDate.setText(formatDate(report.getReportDate()));
        holder.tvType.setText(report.getReportType());
        holder.tvPatient.setText("Paciente ID: " + report.getPatientId());

        if (report.getPainScale() != null) {
            holder.tvPainScale.setText("Dor: " + report.getPainScale() + "/10");
        } else {
            holder.tvPainScale.setVisibility(View.GONE);
        }

        // Clique no card inteiro
        holder.itemView.setOnClickListener(v -> {
            Log.d("REPORT_ADAPTER", "Clique no card - report ID: " + report.getId());
            clickListener.onClick(report);
        });
        
        // 🔥 CORREÇÃO: Clique específico no texto "Ver detalhes"
        if (holder.tvSeeDetails != null) {
            holder.tvSeeDetails.setOnClickListener(v -> {
                Log.d("REPORT_ADAPTER", "Clique em 'Ver detalhes' - report ID: " + report.getId());
                clickListener.onClick(report);
            });
        }

        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onLongClick(report);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return reports != null ? reports.size() : 0;
    }

    private String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvType, tvPatient, tvPainScale, tvSeeDetails;

        public ViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvDate = view.findViewById(R.id.tvDate);
            tvType = view.findViewById(R.id.tvType);
            tvPatient = view.findViewById(R.id.tvPatient);
            tvPainScale = view.findViewById(R.id.tvPainScale);
            // 🔥 NOVO ID sincronizado com item_report.xml
            tvSeeDetails = view.findViewById(R.id.tvSeeDetails);
        }
    }
}
