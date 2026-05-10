package com.example.testbackend;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.BoxChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BoxData;
import com.github.mikephil.charting.data.BoxDataSet;
import com.github.mikephil.charting.data.BoxEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBoxDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.example.testbackend.models.ChartDataResponse;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.ChartDataApi;
import com.example.testbackend.utils.TokenManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsChartsActivity extends AppCompatActivity {

    private BoxChart boxChart;
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
        boxChart = findViewById(R.id.boxChart);
        lineChart = findViewById(R.id.lineChart);
        pieChart = findViewById(R.id.pieChart);
        progressBar = findViewById(R.id.progressBar);
        tokenManager = new TokenManager(this);

        setupBoxChart();
        setupLineChart();
        setupPieChart();
    }

    private void setupBoxChart() {
        boxChart.getDescription().setEnabled(false);
        boxChart.setDrawGridBackground(false);
        boxChart.getAxisLeft().setAxisMinimum(0f);
        boxChart.getAxisLeft().setAxisMaximum(10f);
        boxChart.getAxisRight().setEnabled(false);
        boxChart.getXAxis().setGranularity(1f);
        boxChart.animateY(1000);
    }

    private void setupLineChart() {
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
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setUsePercentValues(true);
        pieChart.animateY(1000);
    }

    private void loadChartData() {
        progressBar.setVisibility(View.VISIBLE);
        
        int professionalId = tokenManager.getUserId();
        ChartDataApi api = ApiClient.getAuthClient().create(ChartDataApi.class);
        
        api.getChartData(professionalId).enqueue(new Callback<ChartDataResponse>() {
            @Override
            public void onResponse(Call<ChartDataResponse> call, Response<ChartDataResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    ChartDataResponse chartData = response.body();
                    setupBoxChartData(chartData.getPainDistribution());
                    setupLineChartData(chartData.getMonthlyReports());
                    setupPieChartData(chartData.getFunctionalStatus());
                } else {
                    Toast.makeText(StatisticsChartsActivity.this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show();
                    Log.e("ChartsActivity", "Error loading chart data: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ChartDataResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(StatisticsChartsActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
                Log.e("ChartsActivity", "Network error", t);
            }
        });
    }

    private void setupBoxChartData(List<ChartDataResponse.PainDistributionData> painData) {
        if (painData == null || painData.isEmpty()) return;

        Map<String, List<Float>> groupedData = new HashMap<>();
        for (ChartDataResponse.PainDistributionData data : painData) {
            String type = data.getReportType();
            if (!groupedData.containsKey(type)) {
                groupedData.put(type, new ArrayList<>());
            }
            groupedData.get(type).add((float) data.getPainScale());
        }

        ArrayList<IBoxDataSet> dataSets = new ArrayList<>();
        int[] colors = {0xFF4CAF50, 0xFFFF9800, 0xFF2196F3, 0xFFF44336};
        int colorIndex = 0;

        for (Map.Entry<String, List<Float>> entry : groupedData.entrySet()) {
            List<Float> values = entry.getValue();
            if (values.size() < 2) continue;

            float min = Float.MAX_VALUE;
            float max = Float.MIN_VALUE;
            float sum = 0;
            for (float val : values) {
                min = Math.min(min, val);
                max = Math.max(max, val);
                sum += val;
            }
            float mean = sum / values.size();

            // Calculate quartiles
            values.sort(Float::compare);
            float q1 = values.get(values.size() / 4);
            float q3 = values.get(3 * values.size() / 4);

            ArrayList<BoxEntry> boxEntries = new ArrayList<>();
            boxEntries.add(new BoxEntry(colorIndex, min, q1, mean, q3, max));

            BoxDataSet boxDataSet = new BoxDataSet(boxEntries, entry.getKey());
            boxDataSet.setColor(colors[colorIndex % colors.length]);
            boxDataSet.setFillColor(colors[colorIndex % colors.length]);
            boxDataSet.setValueTextSize(10f);
            dataSets.add(boxDataSet);
            colorIndex++;
        }

        BoxData boxData = new BoxData(dataSets);
        boxChart.setData(boxData);
        boxChart.invalidate();
    }

    private void setupLineChartData(List<ChartDataResponse.MonthlyReportData> monthlyData) {
        if (monthlyData == null || monthlyData.isEmpty()) return;

        Map<String, Map<String, Integer>> groupedData = new HashMap<>();
        for (ChartDataResponse.MonthlyReportData data : monthlyData) {
            String month = data.getMonth();
            String type = data.getReportType();
            
            if (!groupedData.containsKey(type)) {
                groupedData.put(type, new HashMap<>());
            }
            groupedData.get(type).put(month, data.getCount());
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
        if (statusData == null || statusData.isEmpty()) return;

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (ChartDataResponse.FunctionalStatusData data : statusData) {
            entries.add(new PieEntry(data.getCount(), data.getStatus()));
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "Status Funcional");
        pieDataSet.setColors(new int[] {0xFF4CAF50, 0xFFFF9800, 0xFF2196F3, 0xFFF44336, 0xFF9C27B0});
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueTextColor(0xFFFFFFFF);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private String formatMonthLabel(int index) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        Date date = new Date();
        date.setMonth(date.getMonth() - (12 - index));
        return sdf.format(date);
    }
}
