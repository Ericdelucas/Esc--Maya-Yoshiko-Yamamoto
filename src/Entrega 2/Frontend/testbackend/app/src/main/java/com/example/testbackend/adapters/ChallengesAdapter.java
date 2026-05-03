package com.example.testbackend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.R;
import com.example.testbackend.models.Challenge;

import java.util.List;

public class ChallengesAdapter extends RecyclerView.Adapter<ChallengesAdapter.ViewHolder> {

    private final List<Challenge> challenges;

    public ChallengesAdapter(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_challenge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Challenge challenge = challenges.get(position);
        holder.title.setText(challenge.getTitle());
        holder.description.setText(challenge.getDescription());
        holder.progressText.setText("Progresso: " + challenge.getCurrentProgress() + "/" + challenge.getTargetProgress());
        holder.reward.setText("Recompensa: " + challenge.getRewardPoints() + " pontos");
        
        holder.progressBar.setMax(challenge.getTargetProgress());
        holder.progressBar.setProgress(challenge.getCurrentProgress());
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, progressText, reward;
        ProgressBar progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_title);
            description = itemView.findViewById(R.id.text_description);
            progressText = itemView.findViewById(R.id.text_progress);
            reward = itemView.findViewById(R.id.text_reward);
            progressBar = itemView.findViewById(R.id.progress_bar_challenge);
        }
    }
}