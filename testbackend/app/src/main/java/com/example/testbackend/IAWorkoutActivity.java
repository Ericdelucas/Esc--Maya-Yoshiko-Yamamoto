package com.example.testbackend;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.testbackend.models.AIResponse;
import com.example.testbackend.network.AiApi;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.views.OverlayView;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IAWorkoutActivity extends AppCompatActivity {

    private static final String TAG = "IAWorkout";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{android.Manifest.permission.CAMERA};

    private PreviewView viewFinder;
    private OverlayView overlayView;
    private TextView tvValidation;
    private TextView tvCounter;
    private View vFeedbackBorder;
    private ExecutorService cameraExecutor;
    private AiApi aiApi;
    private long lastAnalysisTimestamp = 0;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ia_workout);

        // Inicializar Views do novo Layout
        viewFinder = findViewById(R.id.viewFinder);
        overlayView = findViewById(R.id.overlayView);
        tvValidation = findViewById(R.id.tvValidation);
        tvCounter = findViewById(R.id.tvCounter);
        vFeedbackBorder = findViewById(R.id.vFeedbackBorder);

        aiApi = ApiClient.getAiClient().create(AiApi.class);
        cameraExecutor = Executors.newSingleThreadExecutor();
        mediaPlayer = new MediaPlayer();

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
        
        findViewById(R.id.btnStopIA).setOnClickListener(v -> finish());
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::processImageProxy);

                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA; // Usar frontal para IA de pose

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Falha ao iniciar a câmera", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void processImageProxy(ImageProxy image) {
        long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp - lastAnalysisTimestamp < 300) {
            image.close();
            return;
        }
        lastAnalysisTimestamp = currentTimestamp;

        Bitmap bitmap = imageToBitmap(image);
        image.close();

        if (bitmap != null) {
            sendFrameToAi(bitmap);
        }
    }

    private Bitmap imageToBitmap(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        
        if (bitmap == null) return null;

        Matrix matrix = new Matrix();
        matrix.postRotate(image.getImageInfo().getRotationDegrees());
        // Inverter se usar frontal
        matrix.postScale(-1, 1, bitmap.getWidth() / 2f, bitmap.getHeight() / 2f);
        
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return Bitmap.createScaledBitmap(rotatedBitmap, 480, 640, true);
    }

    private void sendFrameToAi(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteArray = stream.toByteArray();

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "frame.jpg", requestFile);

        aiApi.processFrame(body).enqueue(new Callback<AIResponse>() {
            @Override
            public void onResponse(Call<AIResponse> call, Response<AIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        AIResponse aiRes = response.body();
                        updateUI(aiRes);
                    });
                }
            }

            @Override
            public void onFailure(Call<AIResponse> call, Throwable t) {
                Log.e(TAG, "Erro no AI Service", t);
            }
        });
    }

    private void updateUI(AIResponse aiRes) {
        overlayView.setLandmarks(aiRes.getLandmarks());
        tvValidation.setText(aiRes.getValidationStatus());
        
        if (aiRes.getRepCount() != null) {
            tvCounter.setText(String.valueOf(aiRes.getRepCount()));
        }

        // Feedback de borda
        if (aiRes.getValidationStatus() != null) {
            if (aiRes.getValidationStatus().contains("CORRETA")) {
                vFeedbackBorder.setBackgroundResource(R.drawable.shape_feedback_border_green);
            } else if (aiRes.getValidationStatus().contains("ERRO") || aiRes.getValidationStatus().contains("CORRIJA")) {
                vFeedbackBorder.setBackgroundResource(R.drawable.shape_feedback_border_red);
            } else {
                vFeedbackBorder.setBackgroundResource(R.drawable.shape_feedback_border); // Transparente
            }
        }

        if (aiRes.getAudioFeedbackUrl() != null && !aiRes.getAudioFeedbackUrl().isEmpty()) {
            playAudioFeedback(aiRes.getAudioFeedbackUrl());
        }
    }

    private void playAudioFeedback(String url) {
        try {
            if (mediaPlayer.isPlaying()) return;
            mediaPlayer.reset();
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                    .build());
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
        } catch (IOException e) {
            Log.e(TAG, "Erro ao reproduzir áudio", e);
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissão de câmera negada", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}