package com.example.testbackend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.testbackend.R;
import com.example.testbackend.models.PatientHealthResponse;
import java.util.List;

public class QuestionnaireAdapter extends RecyclerView.Adapter<QuestionnaireAdapter.ViewHolder> {
    
    private List<PatientHealthResponse.QuestionnaireData> questionnaires;
    
    public QuestionnaireAdapter(List<PatientHealthResponse.QuestionnaireData> questionnaires) {
        this.questionnaires = questionnaires;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_questionnaire, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PatientHealthResponse.QuestionnaireData q = questionnaires.get(position);
        holder.tvScore.setText("Pontuação: " + q.getTotalScore() + "/" + q.getMaxScore());
        holder.tvRisk.setText("Risco: " + q.getRiskLevel());
        holder.tvDate.setText("Data: " + q.getCreatedAt().substring(0, 10));
        
        // Cores de risco
        if (q.getRiskLevel().equalsIgnoreCase("Alto")) holder.tvRisk.setTextColor(0xFFFF0000);
        else if (q.getRiskLevel().equalsIgnoreCase("Moderado")) holder.tvRisk.setTextColor(0xFFFFA500);
        else holder.tvRisk.setTextColor(0xFF008000);
    }
    
    @Override
    public int getItemCount() {
        return questionnaires != null ? questionnaires.size() : 0;
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvScore, tvRisk, tvDate;
        public ViewHolder(View itemView) {
            super(itemView);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvRisk = itemView.findViewById(R.id.tvRisk);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
