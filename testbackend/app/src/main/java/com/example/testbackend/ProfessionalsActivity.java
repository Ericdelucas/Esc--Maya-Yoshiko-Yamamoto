package com.example.testbackend;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.testbackend.adapters.ProfessionalAdapter;
import com.example.testbackend.models.Professional;
import java.util.ArrayList;
import java.util.List;

public class ProfessionalsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professionals);

        setupToolbar();
        setupProfessionals();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupProfessionals() {
        recyclerView = findViewById(R.id.recycler_professionals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Professional> professionals = new ArrayList<>();
        professionals.add(new Professional("Dr. Ricardo Oliveira", "Fisioterapeuta", "● Disponível", ""));
        professionals.add(new Professional("Dra. Ana Beatriz", "Fisioterapeuta Esportiva", "● Disponível", ""));
        professionals.add(new Professional("Dr. Marcos Vinícius", "Ortopedista", "● Em atendimento", ""));

        ProfessionalAdapter adapter = new ProfessionalAdapter(professionals);
        recyclerView.setAdapter(adapter);
    }
}