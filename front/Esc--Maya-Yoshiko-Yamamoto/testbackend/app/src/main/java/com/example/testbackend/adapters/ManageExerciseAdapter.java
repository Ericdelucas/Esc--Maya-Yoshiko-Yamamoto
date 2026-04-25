package com.example.testbackend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.R;
import com.example.testbackend.models.ManageExercisesResponse;

import java.util.List;

public class ManageExerciseAdapter extends RecyclerView.Adapter<ManageExerciseAdapter.ExerciseViewHolder> {
    
    private List<ManageExercisesResponse.ManageExerciseItem> exerciseList;
    private OnDeleteClickListener onDeleteClick;
    private CanDeleteChecker canDelete;
    
    public interface OnDeleteClickListener {
        void onDelete(int exerciseId);
    }
    
    public interface CanDeleteChecker {
        boolean check(int exerciseId);
    }
    
    public ManageExerciseAdapter(List<ManageExercisesResponse.ManageExerciseItem> exerciseList, 
                              OnDeleteClickListener onDeleteClick,
                              CanDeleteChecker canDelete) {
        this.exerciseList = exerciseList;
        this.onDeleteClick = onDeleteClick;
        this.canDelete = canDelete;
    }
    
    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_manage_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        ManageExercisesResponse.ManageExerciseItem exercise = exerciseList.get(position);
        
        holder.tvTitle.setText(exercise.getTitle());
        holder.tvDescription.setText(exercise.getDescription());
        holder.tvPoints.setText(exercise.getPointsValue() + " pontos");
        holder.tvFrequency.setText(exercise.getFrequencyPerWeek() + "x/semana");
        
        // Controlar visibilidade do botão deletar
        boolean canDeleteThis = canDelete.check(exercise.getId());
        holder.btnDelete.setVisibility(canDeleteThis ? View.VISIBLE : View.GONE);
        
        // Configurar clique do botão
        holder.btnDelete.setOnClickListener(v -> {
            if (canDeleteThis) {
                onDeleteClick.onDelete(exercise.getId());
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return exerciseList != null ? exerciseList.size() : 0;
    }
    
    public void updateExercises(List<ManageExercisesResponse.ManageExerciseItem> newExercises) {
        this.exerciseList = newExercises;
        notifyDataSetChanged();
    }
    
    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvPoints, tvFrequency;
        ImageButton btnDelete;
        
        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvExerciseTitle);
            tvDescription = itemView.findViewById(R.id.tvExerciseDescription);
            tvPoints = itemView.findViewById(R.id.tvExercisePoints);
            tvFrequency = itemView.findViewById(R.id.tvExerciseFrequency);
            btnDelete = itemView.findViewById(R.id.btnDeleteExercise);
        }
    }
}
