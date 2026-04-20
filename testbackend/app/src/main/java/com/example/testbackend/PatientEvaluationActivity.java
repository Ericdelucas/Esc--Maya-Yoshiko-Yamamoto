package com.example.testbackend;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.example.testbackend.adapters.EvaluationPagerAdapter;
import com.example.testbackend.models.PatientEvaluation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class PatientEvaluationActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private EvaluationPagerAdapter adapter;
    private PatientEvaluation currentEvaluation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_evaluation);

        setupToolbar();
        setupViewPager();
        setupFab();
        
        // Inicializa uma nova avaliação por padrão
        currentEvaluation = new PatientEvaluation();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Nova Avaliação");
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void setupViewPager() {
        viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        
        adapter = new EvaluationPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Vincula o TabLayout ao ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(adapter.getTabTitle(position));
        }).attach();
    }

    private void setupFab() {
        FloatingActionButton fabSave = findViewById(R.id.fabSave);
        fabSave.setOnClickListener(v -> saveEvaluation());
    }

    private void saveEvaluation() {
        // Lógica para coletar dados de todos os fragments e enviar via API
        Toast.makeText(this, "Salvando avaliação...", Toast.LENGTH_SHORT).show();
    }
}
