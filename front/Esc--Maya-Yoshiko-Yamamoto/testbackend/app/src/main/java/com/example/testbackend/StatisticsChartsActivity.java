package com.example.testbackend;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.testbackend.utils.LocaleHelper;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.example.testbackend.models.ChartDataResponse;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.ChartDataApi;
import com.example.testbackend.utils.TokenManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsChartsActivity extends AppCompatActivity {

    private BarChart barChart;
    private LineChart lineChart;
    private PieChart pieChart;
    private ProgressBar progressBar;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_charts);

        setupToolbar();
        setupViews();
        loadChartData();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Gráficos Estatísticos");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupViews() {
        barChart = findViewById(R.id.barChart);
        lineChart = findViewById(R.id.lineChart);
        pieChart = findViewById(R.id.pieChart);
        progressBar = findViewById(R.id.progressBar);
        tokenManager = new TokenManager(this);

        setupBarChart();
        setupLineChart();
        setupPieChart();
    }

    private void setupBarChart() {
        if (barChart == null) return;
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisLeft().setAxisMaximum(10f);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setGranularity(1f);
        barChart.animateY(1000);
    }

    private void setupLineChart() {
        if (lineChart == null) return;
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getXAxis().setGranularity(1f);
        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return formatMonthLabel((int) value);
            }
        });
        lineChart.animateY(1000);
    }

    private void setupPieChart() {
        if (pieChart == null) return;
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setUsePercentValues(true);
        pieChart.animateY(1000);
    }

    private void loadChartData() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        
        int userId = tokenManager.getUserId();
        String token = tokenManager.getAuthToken();
        ChartDataApi api = ApiClient.getAuthClient().create(ChartDataApi.class);
        
        api.getChartData(token, userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ChartDataResponse> call, @NonNull Response<ChartDataResponse> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    ChartDataResponse chartData = response.body();
                    setupBarChartData(chartData.getPainDistribution());
                    setupLineChartData(chartData.getMonthlyReports());
                    setupPieChartData(chartData.getFunctionalStatus());
                } else {
                    Toast.makeText(StatisticsChartsActivity.this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show();
                    Log.e("ChartsActivity", "Error loading chart data: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChartDataResponse> call, @NonNull Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(StatisticsChartsActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
                Log.e("ChartsActivity", "Network error", t);
            }
        });
    }

    private void setupBarChartData(List<ChartDataResponse.PainDistributionData> painData) {
        if (barChart == null || painData == null || painData.isEmpty()) return;

        Map<String, List<Integer>> groupedData = new HashMap<>();
        for (ChartDataResponse.PainDistributionData data : painData) {
            String type = data.getReportType();
            List<Integer> list = groupedData.get(type);
            if (list == null) {
                list = new ArrayList<>();
                groupedData.put(type, list);
            }
            list.add(data.getPainScale());
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, List<Integer>> entry : groupedData.entrySet()) {
            float sum = 0;
            for (int val : entry.getValue()) {
                sum += val;
            }
            float avg = sum / entry.getValue().size();
            entries.add(new BarEntry(index, avg));
            labels.add(entry.getKey());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Média de Dor por Tipo");
        dataSet.setColors(0xFF4CAF50, 0xFFFF9800, 0xFF2196F3, 0xFFF44336);
        dataSet.setValueTextSize(10f);

        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int idx = (int) value;
                return (idx >= 0 && idx < labels.size()) ? labels.get(idx) : "";
            }
        });

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.invalidate();
    }

    private void setupLineChartData(List<ChartDataResponse.MonthlyReportData> monthlyData) {
        if (lineChart == null || monthlyData == null || monthlyData.isEmpty()) return;

        Map<String, Map<String, Integer>> groupedData = new HashMap<>();
        for (ChartDataResponse.MonthlyReportData data : monthlyData) {
            String month = data.getMonth();
            String type = data.getReportType();
            
            Map<String, Integer> monthMap = groupedData.get(type);
            if (monthMap == null) {
                monthMap = new HashMap<>();
                groupedData.put(type, monthMap);
            }
            monthMap.put(month, data.getCount());
        }

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        int[] colors = {0xFF4CAF50, 0xFFFF9800, 0xFF2196F3, 0xFFF44336};
        int colorIndex = 0;

        for (Map.Entry<String, Map<String, Integer>> entry : groupedData.entrySet()) {
            ArrayList<Entry> entries = new ArrayList<>();
            int index = 0;
            for (Map.Entry<String, Integer> monthEntry : entry.getValue().entrySet()) {
                entries.add(new Entry(index, monthEntry.getValue()));
                index++;
            }

            LineDataSet lineDataSet = new LineDataSet(entries, entry.getKey());
            lineDataSet.setColor(colors[colorIndex % colors.length]);
            lineDataSet.setCircleColor(colors[colorIndex % colors.length]);
            lineDataSet.setValueTextSize(10f);
            lineDataSet.setLineWidth(2f);
            lineDataSet.setCircleRadius(4f);
            dataSets.add(lineDataSet);
            colorIndex++;
        }

        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private void setupPieChartData(List<ChartDataResponse.FunctionalStatusData> statusData) {
        if (pieChart == null || statusData == null || statusData.isEmpty()) return;

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (ChartDataResponse.FunctionalStatusData data : statusData) {
            entries.add(new PieEntry(data.getCount(), data.getStatus()));
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "Status Funcional");
        pieDataSet.setColors(0xFF4CAF50, 0xFFFF9800, 0xFF2196F3, 0xFFF44336, 0xFF9C27B0);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueTextColor(0xFFFFFFFF);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private String formatMonthLabel(int index) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -(12 - index));
        return sdf.format(cal.getTime());
    }
}
