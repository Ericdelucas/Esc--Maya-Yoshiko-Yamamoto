package com.example.testbackend;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.testbackend.adapters.AchievementAdapter;
import com.example.testbackend.models.Achievement;
import java.util.ArrayList;
import java.util.List;

public class ProgressActivity extends AppCompatActivity {

    private RecyclerView achievementsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        setupToolbar();
        setupAchievements();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupAchievements() {
        achievementsRecyclerView = findViewById(R.id.recycler_achievements);
        achievementsRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new Achievement("Primeiro Treino", "Você concluiu seu primeiro treino!"));
        achievements.add(new Achievement("5 Sessões", "Você completou 5 sessões de treino."));
        achievements.add(new Achievement("Streak 3 Dias", "Treinou 3 dias seguidos!"));
        achievements.add(new Achievement("Joelho de Aço", "Completou 10 exercícios de joelho."));

        AchievementAdapter adapter = new AchievementAdapter(achievements);
        achievementsRecyclerView.setAdapter(adapter);
    }
}