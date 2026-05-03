package com.example.testbackend;

import android.content.Intent;
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
import com.example.testbackend.utils.TokenManager;

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
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        tokenManager = new TokenManager(this);
        setupToolbar();
        initViews();
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
            if (tokenManager != null) {
                adapter.setCurrentUserInfo(tokenManager.getUserId(), tokenManager.getUserName());
            }
            recyclerView.setAdapter(adapter);
        }
        
        progressBar = findViewById(R.id.progressBar);
    }

    private void fetchLeaderboard() {
        String token = tokenManager.getAuthToken();
        if (token == null || token.isEmpty()) {
            handleAuthError();
            return;
        }
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        
        TaskApi api = ApiClient.getTaskClient().create(TaskApi.class);
        api.getLeaderboard(token).enqueue(new Callback<List<LeaderboardEntry>>() {
            @Override
            public void onResponse(Call<List<LeaderboardEntry>> call, Response<List<LeaderboardEntry>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    entries.clear();
                    entries.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else if (response.code() == 401 || response.code() == 403) {
                    handleAuthError();
                } else {
                    Toast.makeText(LeaderboardActivity.this, "Erro ao carregar ranking", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LeaderboardEntry>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(LeaderboardActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleAuthError() {
        if (tokenManager != null) {
            tokenManager.clearToken();
        }
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
