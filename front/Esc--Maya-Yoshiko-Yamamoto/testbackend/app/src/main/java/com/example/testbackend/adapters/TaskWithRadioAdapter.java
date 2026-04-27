package com.example.testbackend.adapters;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.R;
import com.example.testbackend.models.Task;

import java.util.List;
import java.util.Locale;

public class TaskWithRadioAdapter extends RecyclerView.Adapter<TaskWithRadioAdapter.TaskViewHolder> {
    private final List<Task> tasks;
    private final OnTaskCompleteListener listener;

    public interface OnTaskCompleteListener {
        void onTaskComplete(Task task);
    }
    
    public TaskWithRadioAdapter(List<Task> tasks, OnTaskCompleteListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_radio, parent, false);
        return new TaskViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        
        if (task == null) return;

        holder.tvTitle.setText(task.getTitle() != null ? task.getTitle() : "Sem título");
        holder.tvDescription.setText(task.getDescription() != null ? task.getDescription() : "");
        
        int points = 0;
        if (task.getPointsValue() != null) {
            points = task.getPointsValue();
        }
        holder.tvPoints.setText(String.format(Locale.getDefault(), "+%d pts", points));
        
        // 🔥 CARREGAR IMAGEM DO EXERCÍCIO
        Log.d("TASK_MEDIA", "Task: " + task.getTitle() + ", ImageURL: " + task.getExerciseImageUrl() + ", VideoURL: " + task.getExerciseVideoUrl());
        
        if (task.getExerciseImageUrl() != null && !task.getExerciseImageUrl().isEmpty()) {
            holder.ivExerciseImage.setVisibility(View.VISIBLE);
            Picasso.get()
                .load(task.getExerciseImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.ivExerciseImage);
        } else {
            holder.ivExerciseImage.setVisibility(View.GONE);
            Log.d("TASK_MEDIA", "Escondendo imagem - URL nula ou vazia");
        }
        
        // 🔥 CONFIGURAR BOTÃO DE VÍDEO
        if (task.getExerciseVideoUrl() != null && !task.getExerciseVideoUrl().isEmpty()) {
            holder.llVideoContainer.setVisibility(View.VISIBLE);
            Log.d("TASK_MEDIA", "Mostrando vídeo: " + task.getExerciseVideoUrl());
            holder.llVideoContainer.setOnClickListener(v -> {
                String videoUrl = task.getExerciseVideoUrl();
                Log.d("TASK_MEDIA", "Clicou no vídeo: " + videoUrl);
                
                try {
                    // 🔥 PRIMEIRO: Tentar abrir em player de vídeo específico
                    Intent videoIntent = new Intent(Intent.ACTION_VIEW);
                    videoIntent.setDataAndType(Uri.parse(videoUrl), "video/mp4");
                    videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    
                    // Verificar se há algum app que pode reproduzir vídeo
                    if (videoIntent.resolveActivity(holder.itemView.getContext().getPackageManager()) != null) {
                        holder.itemView.getContext().startActivity(videoIntent);
                        Log.d("TASK_MEDIA", "Abrindo vídeo em player específico");
                    } else {
                        // 🔥 SEGUNDO: Se não tiver player, abrir no navegador
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                        holder.itemView.getContext().startActivity(webIntent);
                        Log.d("TASK_MEDIA", "Abrindo vídeo no navegador");
                    }
                } catch (Exception e) {
                    Log.e("TASK_MEDIA", "Erro ao abrir vídeo: " + e.getMessage());
                    // 🔥 TERCEIRO: Se tudo falhar, mostrar mensagem
                    Toast.makeText(holder.itemView.getContext(), "Não foi possível reproduzir o vídeo", Toast.LENGTH_SHORT).show();
                    
                    // Tentar abrir no navegador como último recurso
                    try {
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                        holder.itemView.getContext().startActivity(webIntent);
                    } catch (Exception ex) {
                        Toast.makeText(holder.itemView.getContext(), "URL do vídeo inválida", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            holder.llVideoContainer.setVisibility(View.GONE);
            Log.d("TASK_MEDIA", "Escondendo vídeo - URL nula ou vazia");
        }
        
        boolean completedToday = task.getCompletedToday() != null && task.getCompletedToday();
        
        // Evita triggers de listener durante o bind
        holder.radioButton.setOnCheckedChangeListener(null);
        holder.radioButton.setEnabled(!completedToday);
        holder.radioButton.setChecked(completedToday);
        
        holder.radioButton.setOnClickListener(v -> {
            if (!completedToday) {
                if (listener != null) {
                    listener.onTaskComplete(task);
                }
            } else {
                holder.radioButton.setChecked(true);
            }
        });
        
        holder.itemView.setOnClickListener(v -> {
            if (!completedToday) {
                if (listener != null) {
                    listener.onTaskComplete(task);
                }
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return tasks != null ? tasks.size() : 0;
    }
    
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvPoints;
        RadioButton radioButton;
        ImageView ivExerciseImage;
        LinearLayout llVideoContainer;
        
        public TaskViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvDescription = itemView.findViewById(R.id.tvTaskDescription);
            tvPoints = itemView.findViewById(R.id.tvTaskPoints);
            radioButton = itemView.findViewById(R.id.radioButton);
            ivExerciseImage = itemView.findViewById(R.id.ivExerciseImage);
            llVideoContainer = itemView.findViewById(R.id.llVideoContainer);
        }
    }
}
