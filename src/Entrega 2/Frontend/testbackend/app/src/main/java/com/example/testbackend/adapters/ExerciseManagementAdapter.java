package com.example.testbackend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.R;
import com.example.testbackend.models.ProfessionalExercisesResponse;

import java.util.List;
import java.util.function.Consumer;

public class ExerciseManagementAdapter extends RecyclerView.Adapter<ExerciseManagementAdapter.ExerciseViewHolder> {
    
    private List<ProfessionalExercisesResponse.ExerciseItem> exerciseList;
    private Consumer<Integer> onDeleteClick;
    
    public ExerciseManagementAdapter(List<ProfessionalExercisesResponse.ExerciseItem> exerciseList, 
                                  Consumer<Integer> onDeleteClick) {
        this.exerciseList = exerciseList;
        this.onDeleteClick = onDeleteClick;
    }
    
    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_management, parent, false);
        return new ExerciseViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        ProfessionalExercisesResponse.ExerciseItem exercise = exerciseList.get(position);
        
        // Preencher informações
        holder.tvTitle.setText(exercise.getTitle());
        holder.tvDescription.setText(exercise.getDescription());
        holder.tvPoints.setText(exercise.getPointsValue() + " pontos");
        holder.tvFrequency.setText(exercise.getFrequencyPerWeek() + "x/semana");
        holder.tvPatientId.setText("Paciente ID: " + exercise.getPatientId());
        holder.tvAssignedBy.setText("Criado por: " + exercise.getAssignedBy());
        
        // Configurar clique do botão deletar
        holder.btnDelete.setOnClickListener(v -> {
            onDeleteClick.accept(exercise.getId());
        });
    }
    
    @Override
    public int getItemCount() {
        return exerciseList != null ? exerciseList.size() : 0;
    }
    
    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvPoints, tvFrequency, tvPatientId, tvAssignedBy;
        ImageButton btnDelete;
        
        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvExerciseTitle);
            tvDescription = itemView.findViewById(R.id.tvExerciseDescription);
            tvPoints = itemView.findViewById(R.id.tvExercisePoints);
            tvFrequency = itemView.findViewById(R.id.tvExerciseFrequency);
            tvPatientId = itemView.findViewById(R.id.tvPatientId);
            tvAssignedBy = itemView.findViewById(R.id.tvAssignedBy);
            btnDelete = itemView.findViewById(R.id.btnDeleteExercise);
        }
    }
}
