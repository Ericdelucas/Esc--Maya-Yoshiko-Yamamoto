package com.example.testbackend;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.testbackend.models.Exercise;
import com.example.testbackend.models.FileUploadResponse;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.ExerciseApi;
import com.example.testbackend.utils.Constants;
import com.example.testbackend.utils.ImageUtils;
import com.example.testbackend.utils.LocaleHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddExerciseActivity extends AppCompatActivity {

    private static final String TAG = "EXERCISE_UPLOAD_DEBUG";
    private TextInputEditText etTitle, etDescription, etInstructions;
    private MaterialButton btnPickImage, btnPickVideo, btnSave;
    private ImageView ivImagePreview, ivVideoPreview;
    private ProgressBar pbLoading;
    private Uri imageUri, videoUri;
    private String token;
    private String uploadedImagePath, uploadedVideoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        Log.d(TAG, "=== ONCREATE CALLED ===");

        initViews();
        setupToolbar();
        setupPickers();

        btnSave.setOnClickListener(v -> startUploadAndCreation());
    }

    private void initViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etInstructions = findViewById(R.id.etInstructions);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnPickVideo = findViewById(R.id.btnPickVideo);
        btnSave = findViewById(R.id.btnSaveExercise);
        pbLoading = findViewById(R.id.pbLoading);
        
        ivImagePreview = findViewById(R.id.ivImagePreview);
        ivVideoPreview = findViewById(R.id.ivVideoPreview);

        SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
        token = prefs.getString("jwt_token", "");
        String role = prefs.getString("user_role", "Unknown");
        
        if (!"professional".equalsIgnoreCase(role) && !"admin".equalsIgnoreCase(role)) {
            Toast.makeText(this, "Apenas profissionais podem criar exercícios", Toast.LENGTH_LONG).show();
            btnSave.setEnabled(false);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupPickers() {
        ActivityResultLauncher<String> imagePicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        uploadImageImmediately(uri);
                    }
                });

        ActivityResultLauncher<String> videoPicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null) {
                        videoUri = uri;
                        uploadVideoImmediately(uri);
                    }
                });

        btnPickImage.setOnClickListener(v -> imagePicker.launch("image/*"));
        btnPickVideo.setOnClickListener(v -> videoPicker.launch("video/*"));
    }

    private void uploadImageImmediately(Uri uri) {
        Log.d(TAG, "Iniciando upload imediato estilo Perfil");
        btnPickImage.setEnabled(false);
        btnPickImage.setText("Enviando...");

        byte[] imageBytes = ImageUtils.getImageBytes(this, uri);
        if (imageBytes == null) {
            Toast.makeText(this, "Erro ao processar imagem", Toast.LENGTH_SHORT).show();
            btnPickImage.setEnabled(true);
            btnPickImage.setText("Selecionar Imagem");
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", "exercise_" + System.currentTimeMillis() + ".jpg", requestFile);

        ExerciseApi api = ApiClient.getExerciseClient().create(ExerciseApi.class);
        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;

        api.uploadImage(authHeader, body).enqueue(new Callback<FileUploadResponse>() {
            @Override
            public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                btnPickImage.setEnabled(true);
                btnPickImage.setText("Alterar imagem");
                
                if (response.isSuccessful() && response.body() != null) {
                    uploadedImagePath = response.body().getFileUrl();
                    Log.d(TAG, "Upload sucesso: " + uploadedImagePath);
                    showImageFromServer(uploadedImagePath);
                } else {
                    Log.e(TAG, "Erro upload: " + response.code());
                    Toast.makeText(AddExerciseActivity.this, "Erro no upload: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                btnPickImage.setEnabled(true);
                btnPickImage.setText("Selecionar Imagem");
                Log.e(TAG, "Falha upload", t);
                Toast.makeText(AddExerciseActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImageFromServer(String imageUrl) {
        Log.d(TAG, "=== SHOW IMAGE FROM SERVER DEBUG ===");
        Log.d(TAG, "ImageUrl recebida: " + imageUrl);
        
        if (ivImagePreview == null) {
            Log.e(TAG, "ivImagePreview é NULL");
            return;
        }
        
        try {
            // # FORÇAR URL 127.0.0.1 (ignorar Constants para teste se necessário)
            String baseUrl = "http://127.0.0.1:8081";
            Log.d(TAG, "BaseUrl: " + baseUrl);
            
            if (imageUrl == null || imageUrl.isEmpty()) {
                Log.e(TAG, "URL da imagem vazia");
                return;
            }

            if (!imageUrl.startsWith("/")) {
                imageUrl = "/" + imageUrl;
            }

            String fullImageUrl = baseUrl + imageUrl;
            Log.d(TAG, "FullImageUrl: " + fullImageUrl);
            
            // # TESTE: Tentar acessar a URL primeiro
            testImageUrl(fullImageUrl);
            
            // # Mostrar ImageView
            ivImagePreview.setVisibility(View.VISIBLE);
            
            // # Carregar com Picasso
            Picasso.get()
                .load(fullImageUrl)
                .placeholder(R.drawable.bg_preview)
                .error(R.drawable.bg_preview)
                .into(ivImagePreview, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "PICASSO SUCESSO: Imagem carregada!");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "PICASSO ERRO: " + e.getMessage());
                        // # TENTATIVA 2: Fallback para download manual
                        downloadAndShowImage(fullImageUrl);
                    }
                });
            
        } catch (Exception e) {
            Log.e(TAG, "ERRO EXCEPTION: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void testImageUrl(String imageUrl) {
        Log.d(TAG, "=== TESTANDO URL ===");
        Log.d(TAG, "Testando: " + imageUrl);
        
        new Thread(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);
                
                if (responseCode == 200) {
                    Log.d(TAG, "URL ACESSÍVEL: 200 OK");
                } else {
                    Log.e(TAG, "URL INACESSÍVEL: " + responseCode);
                }
                
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "ERRO AO TESTAR URL: " + e.getMessage());
            }
        }).start();
    }

    private void downloadAndShowImage(String imageUrl) {
        Log.d(TAG, "=== DOWNLOAD MANUAL ===");
        
        new Thread(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    InputStream inputStream = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    
                    if (bitmap != null) {
                        // # Mostrar na UI thread
                        runOnUiThread(() -> {
                            ivImagePreview.setImageBitmap(bitmap);
                            ivImagePreview.setVisibility(View.VISIBLE);
                            Log.d(TAG, "Imagem carregada manualmente com sucesso!");
                        });
                    } else {
                        Log.e(TAG, "Bitmap decode falhou");
                    }
                } else {
                    Log.e(TAG, "Download falhou: " + responseCode);
                }
                
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "ERRO DOWNLOAD MANUAL: " + e.getMessage());
            }
        }).start();
    }

    private void uploadVideoImmediately(Uri uri) {
        btnPickVideo.setEnabled(false);
        btnPickVideo.setText("Enviando...");

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) baos.write(buffer, 0, len);
            byte[] videoBytes = baos.toByteArray();
            inputStream.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse("video/mp4"), videoBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", "video_" + System.currentTimeMillis() + ".mp4", requestFile);

            ExerciseApi api = ApiClient.getExerciseClient().create(ExerciseApi.class);
            String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;

            api.uploadVideo(authHeader, body).enqueue(new Callback<FileUploadResponse>() {
                @Override
                public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                    btnPickVideo.setEnabled(true);
                    btnPickVideo.setText("Alterar vídeo");
                    
                    if (response.isSuccessful() && response.body() != null) {
                        uploadedVideoPath = response.body().getFileUrl();
                        Log.d(TAG, "Upload vídeo sucesso: " + uploadedVideoPath);
                        showVideoLocalThumbnail(uri);
                    } else {
                        Log.e(TAG, "Erro upload vídeo: " + response.code());
                        Toast.makeText(AddExerciseActivity.this, "Erro no upload do vídeo", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                    btnPickVideo.setEnabled(true);
                    btnPickVideo.setText("Selecionar Vídeo");
                    Log.e(TAG, "Falha upload vídeo", t);
                }
            });
        } catch (Exception e) {
            btnPickVideo.setEnabled(true);
            btnPickVideo.setText("Selecionar Vídeo");
        }
    }

    private void showVideoLocalThumbnail(Uri uri) {
        ivVideoPreview.setVisibility(View.VISIBLE);
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, uri);
            Bitmap bitmap = retriever.getFrameAtTime(1000000);
            retriever.release();
            if (bitmap != null) ivVideoPreview.setImageBitmap(bitmap);
        } catch (Exception ignored) {}
    }

    private void startUploadAndCreation() {
        if (etTitle.getText().toString().trim().isEmpty()) {
            etTitle.setError("Título é obrigatório");
            return;
        }
        if (uploadedImagePath == null && uploadedVideoPath == null) {
            Toast.makeText(this, "Aguarde o upload da mídia", Toast.LENGTH_SHORT).show();
            return;
        }
        
        setLoading(true);
        createExerciseFinal();
    }

    private void createExerciseFinal() {
        Exercise exercise = new Exercise(
                etTitle.getText().toString().trim(),
                etDescription.getText().toString().trim(),
                etInstructions.getText().toString().trim(),
                uploadedImagePath,
                uploadedVideoPath
        );

        ExerciseApi api = ApiClient.getExerciseClient().create(ExerciseApi.class);
        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;
        
        api.createExercise(authHeader, exercise).enqueue(new Callback<Exercise>() {
            @Override
            public void onResponse(Call<Exercise> call, Response<Exercise> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(AddExerciseActivity.this, "Exercício criado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddExerciseActivity.this, "Erro ao salvar exercício", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Exercise> call, Throwable t) {
                setLoading(false);
                Toast.makeText(AddExerciseActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) result = cursor.getString(nameIndex);
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        if (result == null) result = uri.getPath();
        return result;
    }

    private void setLoading(boolean loading) {
        pbLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!loading);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
