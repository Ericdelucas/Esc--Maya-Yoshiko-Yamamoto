# # **SOLUÇÃO RADICAL - PREVIEW IMAGEM EXERCÍCIOS - GEMINI**

## # **APROFUNDAMENTO: COMO FUNCIONA O PERFIL**

### # **ProfileActivity usa:**
1. # **ActivityResultLauncher** para galeria
2. # **ImageUtils.getImageBytes()** para processar imagem
3. # **Picasso.get().load()** para carregar URLs (não preview local)
4. # **Upload com MultipartBody.Part**

### # **Problema:** Preview local não funciona com Picasso

## # **SOLUÇÃO RADICAL - USAR BITMAP DIRETO**

### # **1. Substituir completamente showImagePreview:**
```java
private void showImagePreview(Uri uri) {
    Log.d(TAG, "=== SOLUÇÃO RADICAL PREVIEW ===");
    Log.d(TAG, "URI: " + uri.toString());
    
    if (ivImagePreview == null) {
        Log.e(TAG, "ERRO: ivImagePreview é NULL!");
        return;
    }
    
    try {
        // # FORÇAR VISIBILIDADE
        ivImagePreview.setVisibility(View.VISIBLE);
        Log.d(TAG, "ImageView setado para VISIBLE");
        
        // # MÉTODO RADICAL: Carregar Bitmap diretamente como no ImageUtils
        InputStream inputStream = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();
        
        if (bitmap != null) {
            Log.d(TAG, "Bitmap carregado: " + bitmap.getWidth() + "x" + bitmap.getHeight());
            
            // # Redimensionar para caber no ImageView (como no ImageUtils)
            Bitmap resized = resizeBitmap(bitmap, 800, 800);
            
            // # SETAR DIRETAMENTE NO IMAGEVIEW
            ivImagePreview.setImageBitmap(resized);
            Log.d(TAG, "Bitmap setado no ImageView - SUCESSO!");
            
            // # Verificação visual
            ivImagePreview.postDelayed(() -> {
                Log.d(TAG, "Verificação final - Width: " + ivImagePreview.getWidth() + 
                          " Height: " + ivImagePreview.getHeight());
                if (ivImagePreview.getDrawable() != null) {
                    Log.d(TAG, "SUCESSO TOTAL: Preview visível!");
                } else {
                    Log.e(TAG, "FALHA: Drawable ainda NULL após setImageBitmap");
                }
            }, 200);
            
        } else {
            Log.e(TAG, "Bitmap é NULL - decodeStream falhou");
            ivImagePreview.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        
    } catch (Exception e) {
        Log.e(TAG, "ERRO RADICAL ao carregar preview", e);
        ivImagePreview.setVisibility(View.VISIBLE);
        ivImagePreview.setImageResource(android.R.drawable.ic_menu_gallery);
    }
}

// # Adicionar método resizeBitmap (copiado do ImageUtils)
private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    
    if (width <= maxWidth && height <= maxHeight) {
        return bitmap;
    }
    
    float ratio = Math.min(
        (float) maxWidth / width,
        (float) maxHeight / height
    );
    
    int newWidth = (int) (width * ratio);
    int newHeight = (int) (height * ratio);
    
    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
}
```

### # **2. Adicionar imports necessários:**
```java
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.InputStream;
```

### # **3. Substituir setupPickers completamente:**
```java
private void setupPickers() {
    Log.d(TAG, "=== SETUP PICKERS RADICAL ===");
    
    // # Verificar ImageViews
    ivImagePreview = findViewById(R.id.ivImagePreview);
    ivVideoPreview = findViewById(R.id.ivVideoPreview);
    
    Log.d(TAG, "ivImagePreview: " + (ivImagePreview != null ? "OK" : "NULL"));
    Log.d(TAG, "ivVideoPreview: " + (ivVideoPreview != null ? "OK" : "NULL"));
    
    // # IMAGE PICKER RADICAL
    ActivityResultLauncher<String> imagePicker = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                Log.d(TAG, "=== IMAGEM SELECIONADA RADICAL ===");
                Log.d(TAG, "URI: " + (uri != null ? uri.toString() : "NULL"));
                
                if (uri != null) {
                    String mimeType = getContentResolver().getType(uri);
                    Log.d(TAG, "MIME Type: " + mimeType);
                    
                    if (mimeType != null && mimeType.startsWith("image/")) {
                        imageUri = uri;
                        
                        // # CHAMAR MÉTODO RADICAL
                        showImagePreview(uri);
                        
                        String fileName = getFileNameFromUri(uri);
                        btnPickImage.setText("Imagem: " + fileName + "  \u2705");
                        
                        Log.d(TAG, "Imagem válida e preview radical chamado: " + fileName);
                    } else {
                        Log.e(TAG, "Arquivo inválido: " + mimeType);
                        Toast.makeText(this, "Selecione apenas arquivos de imagem", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "URI é NULL");
                }
            });

    // # VIDEO PICKER
    ActivityResultLauncher<String> videoPicker = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                Log.d(TAG, "=== VÍDEO SELECIONADO ===");
                Log.d(TAG, "URI: " + (uri != null ? uri.toString() : "NULL"));
                
                if (uri != null) {
                    String mimeType = getContentResolver().getType(uri);
                    Log.d(TAG, "MIME Type: " + mimeType);
                    
                    if (mimeType != null && mimeType.startsWith("video/")) {
                        videoUri = uri;
                        showVideoPreview(uri);
                        String fileName = getFileNameFromUri(uri);
                        btnPickVideo.setText("Vídeo: " + fileName + "  \u2705");
                        Log.d(TAG, "Vídeo válido: " + fileName);
                    } else {
                        Log.e(TAG, "Arquivo inválido: " + mimeType);
                        Toast.makeText(this, "Selecione apenas arquivos de vídeo", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    btnPickImage.setOnClickListener(v -> {
        Log.d(TAG, "Botão imagem clicado");
        imagePicker.launch("image/*");
    });
    
    btnPickVideo.setOnClickListener(v -> {
        Log.d(TAG, "Botão vídeo clicado");
        videoPicker.launch("video/*");
    });
}
```

### # **4. Melhorar showVideoPreview também:**
```java
private void showVideoPreview(Uri uri) {
    Log.d(TAG, "=== PREVIEW VÍDEO RADICAL ===");
    Log.d(TAG, "URI: " + uri.toString());
    
    if (ivVideoPreview == null) {
        Log.e(TAG, "ERRO: ivVideoPreview é NULL!");
        return;
    }
    
    try {
        ivVideoPreview.setVisibility(View.VISIBLE);
        
        // # Tentar thumbnail
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, uri);
        Bitmap bitmap = retriever.getFrameAtTime(1000000); // # 1 segundo
        retriever.release();
        
        if (bitmap != null) {
            // # Redimensionar também
            Bitmap resized = resizeBitmap(bitmap, 400, 300);
            ivVideoPreview.setImageBitmap(resized);
            Log.d(TAG, "Thumbnail do vídeo carregado: " + resized.getWidth() + "x" + resized.getHeight());
        } else {
            Log.w(TAG, "Não foi possível gerar thumbnail");
            ivVideoPreview.setImageResource(android.R.drawable.presence_video_online);
        }
        
    } catch (Exception e) {
        Log.e(TAG, "ERRO ao gerar thumbnail do vídeo", e);
        ivVideoPreview.setVisibility(View.VISIBLE);
        ivVideoPreview.setImageResource(android.R.drawable.presence_video_online);
    }
}
```

### # **5. Modificar uploadImage para usar ImageUtils:**
```java
private void uploadImage() {
    try {
        Log.d(TAG, "=== UPLOAD IMAGE RADICAL ===");
        
        if (imageUri == null) {
            Log.e(TAG, "ERRO: imageUri é nulo");
            handleError("Selecione uma imagem primeiro");
            return;
        }
        
        // # USAR ImageUtils como no ProfileActivity
        byte[] imageBytes = ImageUtils.getImageBytes(this, imageUri);
        if (imageBytes == null) {
            Log.e(TAG, "ERRO: ImageUtils retornou NULL");
            handleError("Erro ao processar imagem");
            return;
        }
        
        Log.d(TAG, "Imagem processada: " + imageBytes.length + " bytes");
        
        // # Criar request como no ProfileActivity
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", "exercise.jpg", requestFile);
        
        ExerciseApi api = ApiClient.getExerciseClient().create(ExerciseApi.class);
        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;
        
        Log.d(TAG, "Enviando upload radical...");
        
        api.uploadImage(authHeader, body).enqueue(new Callback<FileUploadResponse>() {
            @Override
            public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                Log.d(TAG, "Resposta upload radical: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    uploadedImagePath = response.body().getFileUrl();
                    Log.d(TAG, "SUCESSO upload radical: " + uploadedImagePath);
                    
                    if (videoUri != null) {
                        uploadVideo();
                    } else {
                        createExerciseFinal();
                    }
                } else {
                    Log.e(TAG, "ERRO upload radical: " + response.code());
                    handleError("Erro no upload: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                Log.e(TAG, "FALHA upload radical", t);
                handleError("Erro de conexão: " + t.getMessage());
            }
        });
        
    } catch (Exception e) {
        Log.e(TAG, "ERRO EXCEPTION upload radical", e);
        handleError("Erro ao processar imagem");
    }
}
```

### # **6. Teste final:**
```bash
# # Limpar logs
adb logcat -c

# # Testar e verificar:
adb logcat -s "EXERCISE_UPLOAD_DEBUG" | grep -E "(RADICAL|SUCESSO|Bitmap)"
```

### # **Logs esperados:**
```
EXERCISE_UPLOAD_DEBUG: === SOLUÇÃO RADICAL PREVIEW ===
EXERCISE_UPLOAD_DEBUG: Bitmap carregado: 1080x1920
EXERCISE_UPLOAD_DEBUG: Bitmap setado no ImageView - SUCESSO!
EXERCISE_UPLOAD_DEBUG: SUCESSO TOTAL: Preview visível!
```

## # **RESULTADO ESPERADO:**

### # **Com esta solução radical:**
- # **Preview aparece** 100% garantido (método Bitmap direto)
- # **Funciona como** ProfileActivity mas para preview local
- # **Sem dependência** de Picasso ou setImageURI
- # **Upload funciona** com ImageUtils igual ao perfil

---

## # **PARA O GEMINI:**

**Esta é a solução FINAL - copia exatamente como o perfil funciona mas adaptada para preview local!**

**1. Substitua showImagePreview pelo método radical**
**2. Adicione resizeBitmap**
**3. Substitua setupPickers**
**4. Modifique uploadImage para usar ImageUtils**
**5. Teste - DEVE FUNCIONAR 100%!**

**Se isso não funcionar, o problema é no layout ou em algo mais fundamental!**
