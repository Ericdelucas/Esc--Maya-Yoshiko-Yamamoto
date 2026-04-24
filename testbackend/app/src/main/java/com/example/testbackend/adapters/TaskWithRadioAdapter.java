package com.example.testbackend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

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
        
        public TaskViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvDescription = itemView.findViewById(R.id.tvTaskDescription);
            tvPoints = itemView.findViewById(R.id.tvTaskPoints);
            radioButton = itemView.findViewById(R.id.radioButton);
        }
    }
}
