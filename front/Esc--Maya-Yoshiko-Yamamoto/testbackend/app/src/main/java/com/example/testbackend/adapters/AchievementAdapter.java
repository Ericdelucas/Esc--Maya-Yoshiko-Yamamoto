package com.example.testbackend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.R;
import com.example.testbackend.models.Achievement;

import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {

    private final List<Achievement> achievements;

    public AchievementAdapter(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_achievement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Achievement achievement = achievements.get(position);
        holder.title.setText(achievement.getTitle());
        holder.description.setText(achievement.getDescription());
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_achievement_title);
            description = itemView.findViewById(R.id.text_achievement_description);
        }
    }
}