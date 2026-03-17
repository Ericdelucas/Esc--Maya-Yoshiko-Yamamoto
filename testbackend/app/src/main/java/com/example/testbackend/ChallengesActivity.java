package com.example.testbackend;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.testbackend.adapters.ChallengesAdapter;
import com.example.testbackend.models.Challenge;
import java.util.ArrayList;
import java.util.List;

public class ChallengesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);

        setupToolbar();
        setupChallenges();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupChallenges() {
        recyclerView = findViewById(R.id.recycler_challenges);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Challenge> challenges = new ArrayList<>();
        challenges.add(new Challenge("Desafio da Semana", "Complete 5 sessões de treino", 3, 5, 150));
        challenges.add(new Challenge("Joelho Forte", "Faça 10 execuções do exercício", 6, 10, 250));
        challenges.add(new Challenge("Sequência Ativa", "Treine 3 dias seguidos", 2, 3, 100));
        challenges.add(new Challenge("Superação Mensal", "Complete 20 sessões este mês", 12, 20, 500));

        ChallengesAdapter adapter = new ChallengesAdapter(challenges);
        recyclerView.setAdapter(adapter);
    }
}