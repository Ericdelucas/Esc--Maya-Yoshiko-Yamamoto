package com.example.testbackend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.R;
import com.example.testbackend.models.LeaderboardEntry;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private final List<LeaderboardEntry> entries;

    public LeaderboardAdapter(List<LeaderboardEntry> entries) {
        this.entries = entries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderboardEntry entry = entries.get(position);
        holder.position.setText("#" + entry.getPosition());
        holder.name.setText(entry.getName());
        holder.points.setText(String.valueOf(entry.getPoints()));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView position;
        TextView name;
        TextView points;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            position = itemView.findViewById(R.id.text_position);
            name = itemView.findViewById(R.id.text_name);
            points = itemView.findViewById(R.id.text_points);
        }
    }
}