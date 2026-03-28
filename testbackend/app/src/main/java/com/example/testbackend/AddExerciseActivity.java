package com.example.testbackend;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.testbackend.utils.LocaleHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddExerciseActivity extends AppCompatActivity {

    private static final String TAG = "UPLOAD_DEBUG";
    private TextInputEditText etTitle, etDescription, etInstructions;
    private MaterialButton btnPickImage, btnPickVideo, btnSave;
    private ProgressBar pbLoading;
    private Uri imageUri, videoUri;
    private String token;
    private String uploadedImagePath, uploadedVideoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

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

        SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
        token = prefs.getString("jwt_token", "");
        String role = prefs.getString("user_role", "Unknown");
        
        Log.d(TAG, "Config: BaseURL=" + Constants.EXERCISE_BASE_URL + " | Role=" + role);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupPickers() {
        ActivityResultLauncher<String> imagePicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        btnPickImage.setText("Imagem: Selecionada ✅");
                    }
                });

        ActivityResultLauncher<String> videoPicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null) {
                        videoUri = uri;
                        btnPickVideo.setText("Vídeo: Selecionado ✅");
                    }
                });

        btnPickImage.setOnClickListener(v -> imagePicker.launch("image/*"));
        btnPickVideo.setOnClickListener(v -> videoPicker.launch("video/*"));
    }

    private void startUploadAndCreation() {
        if (etTitle.getText().toString().isEmpty()) {
            Toast.makeText(this, "Título é obrigatório", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        if (imageUri != null) {
            uploadImage();
        } else if (videoUri != null) {
            uploadVideo();
        } else {
            createExerciseFinal();
        }
    }

    private void uploadImage() {
        try {
            File file = getFileFromUri(imageUri, "img_upload");
            RequestBody requestFile = RequestBody.create(file, MediaType.parse(getContentResolver().getType(imageUri)));
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            ExerciseApi api = ApiClient.getExerciseClient().create(ExerciseApi.class);
            api.uploadImage("Bearer " + token, body).enqueue(new Callback<FileUploadResponse>() {
                @Override
                public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        uploadedImagePath = response.body().getFileUrl();
                        Log.d(TAG, "Sucesso Imagem: " + uploadedImagePath);
                        if (videoUri != null) uploadVideo();
                        else createExerciseFinal();
                    } else {
                        Log.e(TAG, "Erro Imagem: Code=" + response.code() + " Message=" + response.message());
                        handleError("Erro no upload da imagem: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                    Log.e(TAG, "FALHA CONEXÃO IMAGEM", t);
                    handleError("Falha de rede: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            handleError("Erro ao processar imagem");
        }
    }

    private void uploadVideo() {
        try {
            File file = getFileFromUri(videoUri, "vid_upload");
            RequestBody requestFile = RequestBody.create(file, MediaType.parse(getContentResolver().getType(videoUri)));
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            ExerciseApi api = ApiClient.getExerciseClient().create(ExerciseApi.class);
            api.uploadVideo("Bearer " + token, body).enqueue(new Callback<FileUploadResponse>() {
                @Override
                public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        uploadedVideoPath = response.body().getFileUrl();
                        Log.d(TAG, "Sucesso Vídeo: " + uploadedVideoPath);
                        createExerciseFinal();
                    } else {
                        Log.e(TAG, "Erro Vídeo: Code=" + response.code());
                        handleError("Erro no upload do vídeo: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                    Log.e(TAG, "FALHA CONEXÃO VÍDEO", t);
                    handleError("Falha de rede (Vídeo)");
                }
            });
        } catch (Exception e) {
            handleError("Erro ao processar vídeo");
        }
    }

    private void createExerciseFinal() {
        Exercise exercise = new Exercise(
                etTitle.getText().toString(),
                etDescription.getText().toString(),
                etInstructions.getText().toString(),
                uploadedImagePath,
                uploadedVideoPath
        );

        ExerciseApi api = ApiClient.getExerciseClient().create(ExerciseApi.class);
        api.createExercise("Bearer " + token, exercise).enqueue(new Callback<Exercise>() {
            @Override
            public void onResponse(Call<Exercise> call, Response<Exercise> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(AddExerciseActivity.this, "Exercício criado!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    handleError("Erro ao salvar: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Exercise> call, Throwable t) {
                handleError("Falha ao salvar");
            }
        });
    }

    private File getFileFromUri(Uri uri, String prefix) throws Exception {
        InputStream is = getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile(prefix, null, getCacheDir());
        FileOutputStream fos = new FileOutputStream(tempFile);
        byte[] buffer = new byte[1024];
        int read;
        while ((read = is.read(buffer)) != -1) fos.write(buffer, 0, read);
        fos.close();
        is.close();
        return tempFile;
    }

    private void setLoading(boolean loading) {
        pbLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!loading);
    }

    private void handleError(String msg) {
        setLoading(false);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}