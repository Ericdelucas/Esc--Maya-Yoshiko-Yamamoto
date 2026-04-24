package com.example.testbackend.adapters;

import android.graphics.Typeface;
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
        
        // Posição pura (1, 2, 3...)
        holder.position.setText(String.valueOf(entry.getPosition()));
        
        // Nome com destaque para o usuário real
        if (entry.isRealUser()) {
            holder.name.setText(entry.getName() + " (Você)");
            holder.name.setTypeface(null, Typeface.BOLD);
            holder.name.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.primary));
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.primary_light));
        } else {
            holder.name.setText(entry.getName());
            holder.name.setTypeface(null, Typeface.NORMAL);
            holder.name.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_main));
            holder.itemView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        }
        
        holder.points.setText(String.valueOf(entry.getPoints()));
        
        // Medalhas Top 3
        switch (entry.getPosition()) {
            case 1:
                if (entry.isRealUser()) {
                    holder.position.setText("👑");
                } else {
                    holder.position.setText("🥇");
                }
                holder.position.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
                break;
            case 2:
                holder.position.setText("🥈");
                holder.position.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
                break;
            case 3:
                holder.position.setText("🥉");
                holder.position.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_orange_light));
                break;
            default:
                holder.position.setText(String.valueOf(entry.getPosition()));
                holder.position.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_secondary));
        }
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
