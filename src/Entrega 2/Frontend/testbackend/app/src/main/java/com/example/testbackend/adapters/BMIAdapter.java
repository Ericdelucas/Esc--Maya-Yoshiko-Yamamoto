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

public class BMIAdapter extends RecyclerView.Adapter<BMIAdapter.ViewHolder> {
    
    private List<PatientHealthResponse.BmiData> bmis;
    
    public BMIAdapter(List<PatientHealthResponse.BmiData> bmis) {
        this.bmis = bmis;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bmi, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PatientHealthResponse.BmiData b = bmis.get(position);
        holder.tvBmiValue.setText(String.format("IMC: %.2f", b.getBmi()));
        holder.tvBmiCategory.setText(b.getCategory());
        holder.tvBmiWeight.setText(String.format("Peso: %.1fkg | Altura: %.2fm", b.getWeight(), b.getHeight()));
        holder.tvDate.setText("Data: " + b.getCreatedAt().substring(0, 10));
    }
    
    @Override
    public int getItemCount() {
        return bmis != null ? bmis.size() : 0;
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBmiValue, tvBmiCategory, tvBmiWeight, tvDate;
        public ViewHolder(View itemView) {
            super(itemView);
            tvBmiValue = itemView.findViewById(R.id.tvBmiValue);
            tvBmiCategory = itemView.findViewById(R.id.tvBmiCategory);
            tvBmiWeight = itemView.findViewById(R.id.tvBmiWeight);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
