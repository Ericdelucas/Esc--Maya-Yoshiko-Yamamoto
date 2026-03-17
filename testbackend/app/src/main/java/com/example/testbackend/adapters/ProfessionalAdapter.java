package com.example.testbackend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.testbackend.R;
import com.example.testbackend.models.Professional;
import java.util.List;

public class ProfessionalAdapter extends RecyclerView.Adapter<ProfessionalAdapter.ViewHolder> {

    private final List<Professional> professionals;

    public ProfessionalAdapter(List<Professional> professionals) {
        this.professionals = professionals;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_professional, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Professional professional = professionals.get(position);
        holder.name.setText(professional.getName());
        holder.specialty.setText(professional.getSpecialty());
        holder.status.setText(professional.getStatus());
    }

    @Override
    public int getItemCount() {
        return professionals.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, specialty, status;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_prof_name);
            specialty = itemView.findViewById(R.id.text_prof_specialty);
            status = itemView.findViewById(R.id.text_prof_status);
        }
    }
}