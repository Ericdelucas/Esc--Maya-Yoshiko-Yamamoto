package com.example.testbackend;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.adapters.LeaderboardAdapter;
import com.example.testbackend.models.LeaderboardEntry;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.TaskApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderboardActivity extends AppCompatActivity {

    private static final String TAG = "LEADERBOARD_DEBUG";
    private RecyclerView recyclerView;
    private LeaderboardAdapter adapter;
    private List<LeaderboardEntry> entries = new ArrayList<>();
    private ProgressBar progressBar;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        setupToolbar();
        initViews();
        loadToken();
        fetchLeaderboard();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Ranking de Pontos");
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_leaderboard);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new LeaderboardAdapter(entries);
            recyclerView.setAdapter(adapter);
        }
        
        progressBar = findViewById(R.id.progressBar);
    }

    private void loadToken() {
        SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
        token = prefs.getString("jwt_token", "");
    }

    private void fetchLeaderboard() {
        if (token == null || token.isEmpty()) return;
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        
        TaskApi api = ApiClient.getTaskClient().create(TaskApi.class);
        api.getLeaderboard("Bearer " + token).enqueue(new Callback<List<LeaderboardEntry>>() {
            @Override
            public void onResponse(Call<List<LeaderboardEntry>> call, Response<List<LeaderboardEntry>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    entries.clear();
                    entries.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Ranking carregado: " + entries.size() + " entradas");
                } else {
                    Toast.makeText(LeaderboardActivity.this, "Erro ao carregar ranking", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Erro: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<LeaderboardEntry>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(LeaderboardActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Falha", t);
            }
        });
    }
}
