package com.example.testbackend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.R;
import com.example.testbackend.models.Goal;

import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private final List<Goal> goals;

    public GoalsAdapter(List<Goal> goals) {
        this.goals = goals;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Goal goal = goals.get(position);
        holder.title.setText(goal.getTitle());
        holder.progressText.setText("Progresso: " + goal.getCurrentValue() + "/" + goal.getTargetValue());
        holder.status.setText(goal.getStatus());
        
        holder.progressBar.setMax(goal.getTargetValue());
        holder.progressBar.setProgress(goal.getCurrentValue());
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, progressText, status;
        ProgressBar progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_goal_title);
            progressText = itemView.findViewById(R.id.text_goal_progress);
            status = itemView.findViewById(R.id.text_goal_status);
            progressBar = itemView.findViewById(R.id.progress_bar_goal);
        }
    }
}