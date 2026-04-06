package com.example.testbackend;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ProgressDashboardActivity extends AppCompatActivity {

    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_dashboard);

        setupToolbar();
        setupChart();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupChart() {
        barChart = findViewById(R.id.barChart);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1, 2)); // Semana 1: 2 treinos
        entries.add(new BarEntry(2, 4)); // Semana 2: 4 treinos
        entries.add(new BarEntry(3, 3)); // Semana 3: 3 treinos
        entries.add(new BarEntry(4, 5)); // Semana 4: 5 treinos

        BarDataSet dataSet = new BarDataSet(entries, "Sessões por Semana");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Evolução Mensal");
        barChart.animateY(1000);
        barChart.invalidate();
    }
}