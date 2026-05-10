package com.example.testbackend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.testbackend.models.Exercise;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.ExerciseApi;
import com.example.testbackend.utils.LocaleHelper;
import com.google.android.material.button.MaterialButton;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseDetailActivity extends AppCompatActivity {

    private ImageView ivExercise;
    private TextView tvName, tvDescription, tvInstructions;
    private MaterialButton btnStartIA;
    private VideoView videoView;
    private LinearLayout videoContainer;
    private Button btnPlayVideo, btnPauseVideo, btnOpenExternal;
    private String token;
    private Exercise currentExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        initViews();
        setupToolbar();

        int exerciseId = getIntent().getIntExtra("exercise_id", -1);
        if (exerciseId != -1) {
            fetchExerciseDetails(exerciseId);
        } else {
            Toast.makeText(this, "Erro ao carregar exercício", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        ivExercise = findViewById(R.id.ivExerciseDetail);
        tvName = findViewById(R.id.tvDetailName);
        tvDescription = findViewById(R.id.tvDescription);
        tvInstructions = findViewById(R.id.tvInstructions);
        btnStartIA = findViewById(R.id.btnStartIA);
        
        // Componentes de vídeo
        videoContainer = findViewById(R.id.videoContainer);
        videoView = findViewById(R.id.videoView);
        btnPlayVideo = findViewById(R.id.btnPlayVideo);
        btnPauseVideo = findViewById(R.id.btnPauseVideo);
        btnOpenExternal = findViewById(R.id.btnOpenExternal);

        SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
        token = prefs.getString("jwt_token", "");
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void fetchExerciseDetails(int id) {
        ExerciseApi api = ApiClient.getExerciseClient().create(ExerciseApi.class);
        api.getExerciseById("Bearer " + token, id).enqueue(new Callback<Exercise>() {
            @Override
            public void onResponse(Call<Exercise> call, Response<Exercise> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentExercise = response.body();
                    displayExercise(currentExercise);
                }
            }

            @Override
            public void onFailure(Call<Exercise> call, Throwable t) {
                Toast.makeText(ExerciseDetailActivity.this, "Falha ao carregar detalhes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayExercise(Exercise exercise) {
        tvName.setText(exercise.getTitle());
        tvDescription.setText(exercise.getDescription());
        tvInstructions.setText(exercise.getInstructions());

        // Placeholder para imagem
        ivExercise.setImageResource(android.R.drawable.ic_menu_today);

        // Configurar vídeo - FORÇAR VÍDEO DE EXEMPLO PARA TESTE
        String videoUrl = exercise.getVideoUrl();
        Log.d("ExerciseDetail", "Verificando vídeo do exercício...");
        Log.d("ExerciseDetail", "Video URL original: " + videoUrl);
        Log.d("ExerciseDetail", "Video URL is null: " + (videoUrl == null));
        Log.d("ExerciseDetail", "Video URL is empty: " + (videoUrl != null && videoUrl.isEmpty()));
        
        // FORÇAR USO DO VÍDEO DOS ASSETS PARA TESTE
        Log.d("ExerciseDetail", "FORÇANDO uso do vídeo de exemplo dos assets");
        String exampleVideoUrl = "file:///android_asset/videos/exemplo_exercicio.mp4";
        setupVideoPlayer(exampleVideoUrl);
        
        /*
        // LÓGICA ORIGINAL (COMENTADA PARA TESTE)
        if (videoUrl != null && !videoUrl.trim().isEmpty()) {
            Log.d("ExerciseDetail", "Tentando configurar player de vídeo...");
            setupVideoPlayer(videoUrl);
        } else {
            Log.d("ExerciseDetail", "Exercício não possui vídeo válido, usando vídeo de exemplo");
            // Usar vídeo de exemplo dos assets
            String exampleVideoUrl = "file:///android_asset/videos/exemplo_exercicio.mp4";
            setupVideoPlayer(exampleVideoUrl);
        }
        */

        btnStartIA.setOnClickListener(v -> {
            Intent intent = new Intent(this, IAWorkoutActivity.class);
            // Agora a classe Exercise implementa Serializable, então este método funciona!
            intent.putExtra("exercise_data", (Serializable) exercise);
            startActivity(intent);
        });
    }

    private void setupVideoPlayer(String videoUrl) {
        Log.d("ExerciseDetail", "Configurando vídeo com URL: " + videoUrl);
        
        // Verificar se a URL é válida
        if (videoUrl == null || videoUrl.trim().isEmpty()) {
            Log.e("ExerciseDetail", "URL do vídeo é nula ou vazia");
            showVideoError("URL do vídeo não encontrada");
            return;
        }
        
        // Processar a URL para tratar URIs locais
        String processedUrl = processVideoUrl(videoUrl);
        if (processedUrl == null) {
            Log.e("ExerciseDetail", "Falha ao processar URL do vídeo");
            showVideoError("Falha ao processar URL do vídeo");
            return;
        }
        
        // Verificar se a URL processada é válida
        if (!isValidVideoUrl(processedUrl)) {
            Log.e("ExerciseDetail", "URL processada parece inválida: " + processedUrl);
            showVideoError("URL do vídeo inválida após processamento");
            return;
        }
        
        videoContainer.setVisibility(View.VISIBLE);
        
        try {
            // Configurar VideoView
            Uri videoUri = Uri.parse(processedUrl);
            Log.d("ExerciseDetail", "URI do vídeo processada: " + videoUri.toString());
            
            videoView.setVideoURI(videoUri);
            
            // Listener para quando o vídeo estiver pronto
            videoView.setOnPreparedListener(mp -> {
                Log.d("ExerciseDetail", "Vídeo preparado com sucesso");
                Log.d("ExerciseDetail", "Duração: " + mp.getDuration() + "ms");
                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                
                // Habilitar botões
                btnPlayVideo.setEnabled(true);
                btnPauseVideo.setEnabled(true);
                btnOpenExternal.setEnabled(true);
            });
            
            // Listener para erros
            videoView.setOnErrorListener((mp, what, extra) -> {
                String errorMsg = "Erro ao carregar vídeo (" + what + ", " + extra + ")";
                Log.e("ExerciseDetail", errorMsg);
                showVideoError("Erro ao carregar vídeo: " + getErrorMessage(what));
                return true;
            });
            
            // Listener para quando o vídeo terminar
            videoView.setOnCompletionListener(mp -> {
                Log.d("ExerciseDetail", "Vídeo concluído");
                btnPlayVideo.setText("▶️ Play");
            });
            
            // Configurar botões
            btnPlayVideo.setOnClickListener(v -> {
                try {
                    if (videoView.isPlaying()) {
                        videoView.pause();
                        btnPlayVideo.setText("▶️ Play");
                    } else {
                        videoView.start();
                        btnPlayVideo.setText("⏸️ Pause");
                    }
                } catch (Exception e) {
                    Log.e("ExerciseDetail", "Erro ao controlar reprodução", e);
                    showVideoError("Erro ao controlar vídeo");
                }
            });
            
            btnPauseVideo.setOnClickListener(v -> {
                try {
                    if (videoView.isPlaying()) {
                        videoView.pause();
                        btnPlayVideo.setText("▶️ Play");
                    }
                } catch (Exception e) {
                    Log.e("ExerciseDetail", "Erro ao pausar vídeo", e);
                }
            });
            
            // Botão para abrir em player externo (backup)
            btnOpenExternal.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("ExerciseDetail", "Erro ao abrir player externo", e);
                    Toast.makeText(this, "Não foi possível abrir player externo", Toast.LENGTH_SHORT).show();
                }
            });
            
            // Tentar carregar o vídeo
            videoView.requestFocus();
            videoView.start(); // Inicia automaticamente
            
        } catch (Exception e) {
            Log.e("ExerciseDetail", "Erro ao configurar vídeo", e);
            showVideoError("Erro ao configurar vídeo: " + e.getMessage());
        }
    }
    
    private boolean isValidVideoUrl(String url) {
        if (url == null || url.trim().isEmpty()) return false;
        
        // Verificar se contém protocolos válidos
        return url.startsWith("http://") || 
               url.startsWith("https://") || 
               url.startsWith("content://") || 
               url.startsWith("file://") ||
               url.startsWith("android.resource://") ||
               url.startsWith("file:///android_asset/");
    }
    
    private String processVideoUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.trim().isEmpty()) {
            return null;
        }
        
        Log.d("ExerciseDetail", "Processando URL original: " + originalUrl);
        
        // Se for URI de asset, retornar como está (VideoView consegue acessar)
        if (originalUrl.startsWith("file:///android_asset/")) {
            Log.d("ExerciseDetail", "Usando vídeo dos assets");
            return originalUrl;
        }
        
        // Se for URI de content (local), tentar obter caminho real
        if (originalUrl.startsWith("content://")) {
            try {
                // Tentar obter o caminho real do arquivo
                String[] projection = {android.provider.MediaStore.Video.Media.DATA};
                android.database.Cursor cursor = getContentResolver().query(
                    Uri.parse(originalUrl), projection, null, null, null);
                
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Video.Media.DATA);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    
                    Log.d("ExerciseDetail", "Caminho do arquivo local: " + filePath);
                    
                    // Converter para URI de arquivo
                    if (filePath != null && !filePath.isEmpty()) {
                        return "file://" + filePath;
                    }
                } else if (cursor != null) {
                    cursor.close();
                }
                
                // Se não conseguir obter o caminho, manter a URI original
                Log.d("ExerciseDetail", "Mantendo URI content original");
                return originalUrl;
                
            } catch (Exception e) {
                Log.e("ExerciseDetail", "Erro ao processar URI content", e);
                return originalUrl;
            }
        }
        
        // Para URLs HTTP/HTTPS, retornar como está
        return originalUrl;
    }
    
    private String getErrorMessage(int what) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                return "Erro desconhecido";
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                return "Erro no servidor";
            default:
                return "Erro de mídia (" + what + ")";
        }
    }
    
    private void showVideoError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        
        // Desabilitar botões de vídeo
        btnPlayVideo.setEnabled(false);
        btnPauseVideo.setEnabled(false);
        
        // Manter apenas o botão de abrir externamente como fallback
        btnOpenExternal.setEnabled(true);
        btnOpenExternal.setText("Tentar Player Externo");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pausar vídeo quando a activity for pausada
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
            btnPlayVideo.setText("▶️ Play");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Liberar recursos do vídeo
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}