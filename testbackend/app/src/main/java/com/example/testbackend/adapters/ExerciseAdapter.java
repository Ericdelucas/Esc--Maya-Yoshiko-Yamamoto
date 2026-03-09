package com.example.testbackend.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.ExerciseDetailActivity;
import com.example.testbackend.R;
import com.example.testbackend.models.Exercise;

import java.util.List;

/**
 * Adaptador para a lista de exercícios.
 * Gerencia a exibição e a navegação para os detalhes.
 */
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private List<Exercise> exercises;

    public ExerciseAdapter(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.tvName.setText(exercise.getName());
        holder.tvCategory.setText(exercise.getCategory());
        holder.tvDescription.setText(exercise.getDescription());

        // Navegação via Intent Explícita ao clicar no item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ExerciseDetailActivity.class);
            intent.putExtra("exercise_data", exercise);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvDescription;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvExerciseName);
            tvCategory = itemView.findViewById(R.id.tvExerciseCategory);
            tvDescription = itemView.findViewById(R.id.tvExerciseDescription);
        }
    }
}