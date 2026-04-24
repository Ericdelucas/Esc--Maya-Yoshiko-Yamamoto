# # **CORRIGIR UPLOAD DE EXERCÍCIOS - GEMINI**

## # **PROBLEMA IDENTIFICADO:**

### # **Sintomas:**
- # **Não consegue criar exercício** com imagem ou vídeo
- # **Upload de arquivos** não funciona
- # **Botões de seleção** funcionam mas upload falha
- # **Backend de exercícios** exige autenticação

### # **Causa Principal:**
- # **Backend de exercícios (porta 8081)** requer autenticação
- # **adb reverse** não configurado para porta 8081
- # **Token JWT** pode não estar sendo enviado corretamente
- # **Endpoints de upload** podem não existir ou estar incorretos

## # **DIAGNÓSTICO E SOLUÇÃO:**

### # **PASSO 1: Verificar Backend de Exercícios**

#### # **Testar endpoints:**
```bash
# # Verificar se backend está rodando:
curl -v http://localhost:8081/health

# # Configurar adb reverse para exercícios:
adb reverse tcp:8081 tcp:8081

# # Verificar se endpoints existem:
curl -X POST http://localhost:8081/exercises/upload/image -F "file=@/dev/null"
# # Se retornar "Authentication required", backend está OK
```

### # **PASSO 2: Corrigir Constants.java**

#### # **Adicionar log para exercícios:**
```java
// # Arquivo: app/src/main/java/com/example/testbackend/utils/Constants.java
package com.example.testbackend.utils;

import android.util.Log;

public class Constants {
    
    private static final String TAG = "NETWORK_AUDIT";
    
    // # FORÇANDO 127.0.0.1 (Requer adb reverse)
    public static final String HOST = "127.0.0.1";
    
    public static final String AUTH_BASE_URL = "http://" + HOST + ":8080/";
    public static final String PACIENTES_BASE_URL = "http://" + HOST + ":8080/";
    
    // # EXERCÍCIOS - PORTA 8081
    public static final String EXERCISE_BASE_URL = "http://" + HOST + ":8081/";
    public static final String HEALTH_BASE_URL = "http://" + HOST + ":8071/";
    public static final String TRAINING_BASE_URL = "http://" + HOST + ":8030/";
    public static final String AI_HTTP_URL = "http://" + HOST + ":8090/ai/process-frame";

    static {
        Log.d(TAG, ">>> AUDITORIA DE REDE ATIVA <<<");
        Log.d(TAG, "HOST CONFIGURADO: " + HOST);
        Log.d(TAG, "URL DE AUTENTICAÇÃO: " + AUTH_BASE_URL);
        Log.d(TAG, "URL DE EXERCÍCIOS: " + EXERCISE_BASE_URL);
        Log.d(TAG, "IMPORTANTE: Execute 'adb reverse tcp:8080 tcp:8080' e 'adb reverse tcp:8081 tcp:8081'");
    }
}
```

### # **PASSO 3: Corrigir AddExerciseActivity.java**

#### # **Problemas identificados:**
1. # **Token pode estar vazio ou inválido**
2. # **URL base pode estar incorreta**
3. # **Endpoints podem não existir**

#### # **Versão corrigida:**
```java
// # Arquivo: app/src/main/java/com/example/testbackend/AddExerciseActivity.java
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

    private static final String TAG = "EXERCISE_UPLOAD_DEBUG";
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
        
        // # DEBUG: Log configuração inicial
        Log.d(TAG, ">>> INICIANDO ACTIVITY DE EXERCÍCIOS <<<");
        Log.d(TAG, "EXERCISE_BASE_URL: " + Constants.EXERCISE_BASE_URL);
    }

    private void initViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etInstructions = findViewById(R.id.etInstructions);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnPickVideo = findViewById(R.id.btnPickVideo);
        btnSave = findViewById(R.id.btnSaveExercise);
        pbLoading = findViewById(R.id.pbLoading);

        // # Obter token JWT
        SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
        token = prefs.getString("jwt_token", "");
        String role = prefs.getString("user_role", "Unknown");
        
        Log.d(TAG, "Token JWT: " + (token.isEmpty() ? "VAZIO" : "OK"));
        Log.d(TAG, "User Role: " + role);
        Log.d(TAG, "Exercise URL: " + Constants.EXERCISE_BASE_URL);
        
        if (token.isEmpty()) {
            Log.e(TAG, "ERRO: Token JWT está vazio!");
            Toast.makeText(this, "Erro: Usuário não autenticado", Toast.LENGTH_LONG).show();
        }
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
                        btnPickImage.setText("Imagem: " + uri.getLastPathSegment() + "  \u2705");
                        Log.d(TAG, "Imagem selecionada: " + uri.toString());
                    }
                });

        ActivityResultLauncher<String> videoPicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null) {
                        videoUri = uri;
                        btnPickVideo.setText("Vídeo: " + uri.getLastPathSegment() + "  \u2705");
                        Log.d(TAG, "Vídeo selecionado: " + uri.toString());
                    }
                });

        btnPickImage.setOnClickListener(v -> {
            Log.d(TAG, "Clique no botão de imagem");
            imagePicker.launch("image/*");
        });
        
        btnPickVideo.setOnClickListener(v -> {
            Log.d(TAG, "Clique no botão de vídeo");
            videoPicker.launch("video/*");
        });
    }

    private void startUploadAndCreation() {
        Log.d(TAG, ">>> INICIANDO PROCESSO DE UPLOAD <<<");
        
        if (token.isEmpty()) {
            Toast.makeText(this, "Erro: Usuário não autenticado. Faça login novamente.", Toast.LENGTH_LONG).show();
            return;
        }
        
        if (etTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Título é obrigatório", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Título: " + etTitle.getText().toString());
        Log.d(TAG, "Imagem URI: " + (imageUri != null ? "SIM" : "NÃO"));
        Log.d(TAG, "Vídeo URI: " + (videoUri != null ? "SIM" : "NÃO"));

        setLoading(true);
        
        if (imageUri != null) {
            Log.d(TAG, "Iniciando upload da imagem...");
            uploadImage();
        } else if (videoUri != null) {
            Log.d(TAG, "Iniciando upload do vídeo...");
            uploadVideo();
        } else {
            Log.d(TAG, "Criando exercício sem arquivos...");
            createExerciseFinal();
        }
    }

    private void uploadImage() {
        try {
            Log.d(TAG, "Processando upload de imagem...");
            
            File file = getFileFromUri(imageUri, "img_upload");
            String mimeType = getContentResolver().getType(imageUri);
            Log.d(TAG, "MIME Type: " + mimeType);
            
            RequestBody requestFile = RequestBody.create(file, MediaType.parse(mimeType));
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            ExerciseApi api = ApiClient.getExerciseClient().create(ExerciseApi.class);
            String authToken = "Bearer " + token;
            
            Log.d(TAG, "URL do upload: " + Constants.EXERCISE_BASE_URL + "exercises/upload/image");
            Log.d(TAG, "Token: " + (token.length() > 20 ? token.substring(0, 20) + "..." : token));
            
            api.uploadImage(authToken, body).enqueue(new Callback<FileUploadResponse>() {
                @Override
                public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                    Log.d(TAG, "Resposta do upload de imagem - Code: " + response.code());
                    
                    if (response.isSuccessful() && response.body() != null) {
                        uploadedImagePath = response.body().getFileUrl();
                        Log.d(TAG, "SUCESSO - Imagem: " + uploadedImagePath);
                        
                        if (videoUri != null) {
                            uploadVideo();
                        } else {
                            createExerciseFinal();
                        }
                    } else {
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (Exception e) {}
                        
                        Log.e(TAG, "ERRO NO UPLOAD DE IMAGEM:");
                        Log.e(TAG, "Code: " + response.code());
                        Log.e(TAG, "Message: " + response.message());
                        Log.e(TAG, "Body: " + errorBody);
                        
                        handleError("Erro no upload da imagem: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                    Log.e(TAG, "FALHA DE CONEXÃO - UPLOAD IMAGEM", t);
                    handleError("Falha de rede: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "ERRO AO PROCESSAR IMAGEM", e);
            handleError("Erro ao processar imagem: " + e.getMessage());
        }
    }

    private void uploadVideo() {
        try {
            Log.d(TAG, "Processando upload de vídeo...");
            
            File file = getFileFromUri(videoUri, "vid_upload");
            String mimeType = getContentResolver().getType(videoUri);
            Log.d(TAG, "MIME Type: " + mimeType);
            
            RequestBody requestFile = RequestBody.create(file, MediaType.parse(mimeType));
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            ExerciseApi api = ApiClient.getExerciseClient().create(ExerciseApi.class);
            String authToken = "Bearer " + token;
            
            Log.d(TAG, "URL do upload: " + Constants.EXERCISE_BASE_URL + "exercises/upload/video");
            
            api.uploadVideo(authToken, body).enqueue(new Callback<FileUploadResponse>() {
                @Override
                public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                    Log.d(TAG, "Resposta do upload de vídeo - Code: " + response.code());
                    
                    if (response.isSuccessful() && response.body() != null) {
                        uploadedVideoPath = response.body().getFileUrl();
                        Log.d(TAG, "SUCESSO - Vídeo: " + uploadedVideoPath);
                        createExerciseFinal();
                    } else {
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (Exception e) {}
                        
                        Log.e(TAG, "ERRO NO UPLOAD DE VÍDEO:");
                        Log.e(TAG, "Code: " + response.code());
                        Log.e(TAG, "Message: " + response.message());
                        Log.e(TAG, "Body: " + errorBody);
                        
                        handleError("Erro no upload do vídeo: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                    Log.e(TAG, "FALHA DE CONEXÃO - UPLOAD VÍDEO", t);
                    handleError("Falha de rede (Vídeo): " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "ERRO AO PROCESSAR VÍDEO", e);
            handleError("Erro ao processar vídeo: " + e.getMessage());
        }
    }

    private void createExerciseFinal() {
        Log.d(TAG, ">>> CRIANDO EXERCÍCIO FINAL <<<");
        
        Exercise exercise = new Exercise(
                etTitle.getText().toString().trim(),
                etDescription.getText().toString().trim(),
                etInstructions.getText().toString().trim(),
                uploadedImagePath,
                uploadedVideoPath
        );

        Log.d(TAG, "Dados do exercício:");
        Log.d(TAG, "Título: " + exercise.getTitle());
        Log.d(TAG, "Descrição: " + exercise.getDescription());
        Log.d(TAG, "Instruções: " + exercise.getInstructions());
        Log.d(TAG, "Imagem: " + exercise.getImageUrl());
        Log.d(TAG, "Vídeo: " + exercise.getVideoUrl());

        ExerciseApi api = ApiClient.getExerciseClient().create(ExerciseApi.class);
        String authToken = "Bearer " + token;
        
        api.createExercise(authToken, exercise).enqueue(new Callback<Exercise>() {
            @Override
            public void onResponse(Call<Exercise> call, Response<Exercise> response) {
                Log.d(TAG, "Resposta da criação - Code: " + response.code());
                setLoading(false);
                
                if (response.isSuccessful()) {
                    Log.d(TAG, "SUCESSO - Exercício criado!");
                    Toast.makeText(AddExerciseActivity.this, "Exercício criado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {}
                    
                    Log.e(TAG, "ERRO AO CRIAR EXERCÍCIO:");
                    Log.e(TAG, "Code: " + response.code());
                    Log.e(TAG, "Message: " + response.message());
                    Log.e(TAG, "Body: " + errorBody);
                    
                    handleError("Erro ao salvar exercício: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Exercise> call, Throwable t) {
                Log.e(TAG, "FALHA DE CONEXÃO - CRIAÇÃO", t);
                setLoading(false);
                handleError("Falha ao criar exercício: " + t.getMessage());
            }
        });
    }

    private File getFileFromUri(Uri uri, String prefix) throws Exception {
        Log.d(TAG, "Convertendo URI para arquivo: " + uri.toString());
        
        InputStream is = getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile(prefix, null, getCacheDir());
        FileOutputStream fos = new FileOutputStream(tempFile);
        byte[] buffer = new byte[1024];
        int read;
        while ((read = is.read(buffer)) != -1) fos.write(buffer, 0, read);
        fos.close();
        is.close();
        
        Log.d(TAG, "Arquivo temporário criado: " + tempFile.getAbsolutePath() + " (" + tempFile.length() + " bytes)");
        return tempFile;
    }

    private void setLoading(boolean loading) {
        pbLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!loading);
    }

    private void handleError(String msg) {
        Log.e(TAG, "ERRO: " + msg);
        setLoading(false);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
```

### # **PASSO 4: Verificar Models**

#### # **Verificar se models existem:**
```java
// # Verificar se estes arquivos existem:
// app/src/main/java/com/example/testbackend/models/Exercise.java
// app/src/main/java/com/example/testbackend/models/FileUploadResponse.java
```

#### # **Se não existirem, criar:**
```java
// # Exercise.java
package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class Exercise {
    @SerializedName("title")
    private String title;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("instructions")
    private String instructions;
    
    @SerializedName("image_url")
    private String imageUrl;
    
    @SerializedName("video_url")
    private String videoUrl;
    
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("created_at")
    private String createdAt;
    
    public Exercise() {}
    
    public Exercise(String title, String description, String instructions, String imageUrl, String videoUrl) {
        this.title = title;
        this.description = description;
        this.instructions = instructions;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
    }
    
    // Getters e Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

// # FileUploadResponse.java
package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class FileUploadResponse {
    @SerializedName("file_url")
    private String fileUrl;
    
    @SerializedName("message")
    private String message;
    
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
```

### # **PASSO 5: Teste e Validação**

#### # **Passos para testar:**
1. # **Configurar adb reverse:**
```bash
adb reverse tcp:8080 tcp:8080
adb reverse tcp:8081 tcp:8081
```

2. # **Verificar logs em tempo real:**
```bash
adb logcat -s "EXERCISE_UPLOAD_DEBUG"
```

3. # **Testar criação sem arquivos:**
- # Preencher apenas título
- # Clicar em "CRIAR EXERCÍCIO"
- # Verificar logs

4. # **Testar com imagem:**
- # Selecionar imagem
- # Clicar em criar
- # Verificar logs de upload

5. # **Testar com vídeo:**
- # Selecionar vídeo
- # Clicar em criar
- # Verificar logs de upload

### # **PASSO 6: Soluções Alternativas**

#### # **Se endpoints não existirem:**
```java
// # Opção 1: Criar exercício sem upload primeiro
// # Depois fazer upload separadamente

// # Opção 2: Usar endpoint único de upload
@Multipart
@POST("exercises/upload")
Call<Exercise> createExerciseWithFiles(
    @Header("Authorization") String token,
    @Part MultipartBody.Part title,
    @Part MultipartBody.Part description,
    @Part MultipartBody.Part instructions,
    @Part MultipartBody.Part image,
    @Part MultipartBody.Part video
);
```

#### # **Se autenticação falhar:**
```java
// # Verificar se token está sendo salvo corretamente
SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
String token = prefs.getString("jwt_token", "");

// # Se token estiver vazio, redirecionar para login
if (token.isEmpty()) {
    startActivity(new Intent(this, LoginActivity.class));
    finish();
    return;
}
```

## # **RESULTADO ESPERADO:**

### # **Após correção:**
- # **Logs detalhados** mostrando cada passo
- # **Upload de imagem** funcionando
- # **Upload de vídeo** funcionando
- # **Criação sem arquivos** funcionando
- # **Feedback claro** de erros

### # **Logs esperados:**
```
EXERCISE_UPLOAD_DEBUG: Token JWT: OK
EXERCISE_UPLOAD_DEBUG: Imagem selecionada: content://...
EXERCISE_UPLOAD_DEBUG: Iniciando upload da imagem...
EXERCISE_UPLOAD_DEBUG: SUCESSO - Imagem: /uploads/exercises/image_123.jpg
EXERCISE_UPLOAD_DEBUG: SUCESSO - Exercício criado!
```

---

## # **IMPORTANTE PARA O GEMINI:**

**1. Execute os comandos adb reverse primeiro**
**2. Verifique se o token JWT está sendo salvo**
**3. Use os logs detalhados para identificar o problema exato**
**4. Teste primeiro sem arquivos, depois com imagem, depois com vídeo**
**5. Se endpoints não existirem, crie-os ou use soluções alternativas**

**O problema principal é autenticação e configuração de rede - a versão corrigida tem logs detalhados para ajudar no diagnóstico!**
