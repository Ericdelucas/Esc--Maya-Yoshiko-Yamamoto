package com.example.testbackend;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.testbackend.adapters.GoalsAdapter;
import com.example.testbackend.models.Goal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class GoalsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        setupToolbar();
        setupGoals();
        setupFab();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupGoals() {
        recyclerView = findViewById(R.id.recycler_goals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Goal> goals = new ArrayList<>();
        goals.add(new Goal("Sessões Semanais", 4, 2, "Em andamento"));
        goals.add(new Goal("Minutos de Exercício", 120, 80, "Ativa"));
        goals.add(new Goal("Streak de Dias", 7, 6, "Quase concluída"));
        goals.add(new Goal("Frequência Mensal", 20, 15, "Faltam 5 sessões"));

        GoalsAdapter adapter = new GoalsAdapter(goals);
        recyclerView.setAdapter(adapter);
    }

    private void setupFab() {
        FloatingActionButton fab = findViewById(R.id.fab_add_goal);
        fab.setOnClickListener(v -> {
            Toast.makeText(this, "Adicionar nova meta em breve!", Toast.LENGTH_SHORT).show();
        });
    }
}