package com.example.testbackend.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.ExerciseDetailActivity;
import com.example.testbackend.R;
import com.example.testbackend.models.Exercise;
import com.google.android.material.button.MaterialButton;

import java.util.List;

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
        
        holder.tvName.setText(exercise.getTitle());
        holder.tvDescription.setText(exercise.getDescription());

        // Placeholder para imagem (Glide/Picasso podem ser adicionados depois para carregar exercise.getImageUrl())
        holder.ivExerciseImage.setImageResource(android.R.drawable.ic_menu_today);

        // Navegação para detalhes
        View.OnClickListener detailLauncher = v -> {
            Intent intent = new Intent(v.getContext(), ExerciseDetailActivity.class);
            // Passando o ID para carregar mídias e instruções no detalhe
            intent.putExtra("exercise_id", exercise.getId());
            v.getContext().startActivity(intent);
        };

        holder.itemView.setOnClickListener(detailLauncher);
        holder.btnStart.setOnClickListener(detailLauncher);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        ImageView ivExerciseImage;
        TextView tvName, tvDescription;
        MaterialButton btnStart;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            ivExerciseImage = itemView.findViewById(R.id.ivExerciseImage);
            tvName = itemView.findViewById(R.id.tvExerciseName);
            tvDescription = itemView.findViewById(R.id.tvExerciseDescription);
            btnStart = findViewById(R.id.btnStartExercise);
        }

        private <T extends View> T findViewById(int id) {
            return itemView.findViewById(id);
        }
    }
}