package com.example.testbackend;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.testbackend.adapters.LeaderboardAdapter;
import com.example.testbackend.models.LeaderboardEntry;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        setupToolbar();
        setupLeaderboard();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupLeaderboard() {
        recyclerView = findViewById(R.id.recycler_leaderboard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<LeaderboardEntry> entries = new ArrayList<>();
        entries.add(new LeaderboardEntry(1, "João Silva", 1200));
        entries.add(new LeaderboardEntry(2, "Maria Oliveira", 980));
        entries.add(new LeaderboardEntry(3, "Carlos Santos", 910));
        entries.add(new LeaderboardEntry(4, "Você", 860));
        entries.add(new LeaderboardEntry(5, "Beatriz Costa", 750));
        entries.add(new LeaderboardEntry(6, "Ricardo Souza", 620));

        LeaderboardAdapter adapter = new LeaderboardAdapter(entries);
        recyclerView.setAdapter(adapter);
    }
}