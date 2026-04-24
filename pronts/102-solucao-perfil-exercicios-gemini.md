# # **SOLUÇÃO DEFINITIVA - COPIAR PERFIL EXATO - EXERCÍCIOS - GEMINI**

## # **DESCOBERTA CRUCIAL:**

### # **O PERFIL NÃO TEM PREVIEW LOCAL!**
- # **Perfil:** Seleciona imagem -> Upload direto -> Carrega do servidor com Picasso
- # **Exercícios:** Tentando fazer preview local (por isso não funciona)

### # **Como o perfil funciona:**
1. # **galleryLauncher.launch("image/*")** - seleciona URI
2. # **uploadProfilePhoto(uri)** - faz upload IMEDIATO
3. # **loadUserProfile()** - recarrega dados do servidor
4. # **Picasso.get().load(fullImageUrl)** - carrega do servidor

## # **SOLUÇÃO: COPIAR EXATAMENTE O PERFIL**

### # **1. Remover preview local - fazer upload direto:**
```java
private void setupPickers() {
    Log.d(TAG, "=== SETUP PICKERS ESTILO PERFIL ===");
    
    // # Verificar ImageViews
    ivImagePreview = findViewById(R.id.ivImagePreview);
    ivVideoPreview = findViewById(R.id.ivVideoPreview);
    
    Log.d(TAG, "ivImagePreview: " + (ivImagePreview != null ? "OK" : "NULL"));
    
    // # IMAGE PICKER ESTILO PERFIL - Upload direto sem preview
    ActivityResultLauncher<String> imagePicker = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                Log.d(TAG, "=== IMAGEM SELECIONADA ESTILO PERFIL ===");
                Log.d(TAG, "URI: " + (uri != null ? uri.toString() : "NULL"));
                
                if (uri != null) {
                    String mimeType = getContentResolver().getType(uri);
                    Log.d(TAG, "MIME Type: " + mimeType);
                    
                    if (mimeType != null && mimeType.startsWith("image/")) {
                        imageUri = uri;
                        
                        // # ESTILO PERFIL: Upload IMEDIATO sem preview local
                        uploadImageDirectly(uri);
                        
                        String fileName = getFileNameFromUri(uri);
                        btnPickImage.setText("Imagem: " + fileName + "  \u2705 Enviando...");
                        
                        Log.d(TAG, "Upload iniciado estilo perfil: " + fileName);
                    } else {
                        Log.e(TAG, "Arquivo inválido: " + mimeType);
                        Toast.makeText(this, "Selecione apenas arquivos de imagem", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    // # VIDEO PICKER - manter lógica similar
    ActivityResultLauncher<String> videoPicker = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                Log.d(TAG, "=== VÍDEO SELECIONADO ===");
                Log.d(TAG, "URI: " + (uri != null ? uri.toString() : "NULL"));
                
                if (uri != null) {
                    String mimeType = getContentResolver().getType(uri);
                    Log.d(TAG, "MIME Type: " + mimeType);
                    
                    if (mimeType != null && mimeType.startsWith("video/")) {
                        videoUri = uri;
                        uploadVideoDirectly(uri);
                        String fileName = getFileNameFromUri(uri);
                        btnPickVideo.setText("Vídeo: " + fileName + "  \u2705 Enviando...");
                        Log.d(TAG, "Upload vídeo iniciado: " + fileName);
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

### # **2. uploadImageDirectly (estilo ProfileActivity):**
```java
private void uploadImageDirectly(Uri uri) {
    Log.d(TAG, "=== UPLOAD IMAGE DIRETO ESTILO PERFIL ===");
    
    try {
        // # Exatamente como no ProfileActivity
        byte[] imageBytes = ImageUtils.getImageBytes(this, uri);
        if (imageBytes == null) {
            Log.e(TAG, "ERRO: ImageUtils retornou NULL");
            btnPickImage.setText("Selecionar Imagem");
            Toast.makeText(this, "Erro ao processar imagem", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Log.d(TAG, "Imagem processada: " + imageBytes.length + " bytes");
        
        // # Criar request como no ProfileActivity
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", "exercise.jpg", requestFile);
        
        ExerciseApi api = ApiClient.getExerciseClient().create(ExerciseApi.class);
        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;
        
        Log.d(TAG, "Enviando upload direto...");
        
        api.uploadImage(authHeader, body).enqueue(new Callback<FileUploadResponse>() {
            @Override
            public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                Log.d(TAG, "Resposta upload direto: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    uploadedImagePath = response.body().getFileUrl();
                    Log.d(TAG, "SUCESSO upload direto: " + uploadedImagePath);
                    
                    // # ESTILO PERFIL: Mostrar preview do SERVIDOR
                    showImageFromServer(uploadedImagePath);
                    
                    btnPickImage.setText("Imagem: " + getFileNameFromUri(uri) + "  \u2705");
                    
                } else {
                    Log.e(TAG, "ERRO upload direto: " + response.code());
                    btnPickImage.setText("Selecionar Imagem");
                    Toast.makeText(this, "Erro no upload: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                Log.e(TAG, "FALHA upload direto", t);
                btnPickImage.setText("Selecionar Imagem");
                Toast.makeText(this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        
    } catch (Exception e) {
        Log.e(TAG, "ERRO EXCEPTION upload direto", e);
        btnPickImage.setText("Selecionar Imagem");
        Toast.makeText(this, "Erro ao processar imagem", Toast.LENGTH_SHORT).show();
    }
}
```

### # **3. showImageFromServer (estilo ProfileActivity.loadUserProfile):**
```java
private void showImageFromServer(String imageUrl) {
    Log.d(TAG, "=== CARREGANDO IMAGEM DO SERVIDOR ===");
    Log.d(TAG, "URL: " + imageUrl);
    
    if (ivImagePreview == null) {
        Log.e(TAG, "ivImagePreview é NULL");
        return;
    }
    
    if (imageUrl == null || imageUrl.isEmpty()) {
        Log.e(TAG, "URL da imagem é vazia");
        return;
    }
    
    try {
        // # Construir URL completa como no ProfileActivity
        String baseUrl = Constants.EXERCISE_BASE_URL;
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        String fullImageUrl = baseUrl + imageUrl;
        Log.d(TAG, "URL completa: " + fullImageUrl);
        
        // # Mostrar ImageView
        ivImagePreview.setVisibility(View.VISIBLE);
        
        // # Carregar com Picasso EXATAMENTE como no perfil
        Picasso.get()
            .load(fullImageUrl)
            .placeholder(R.drawable.bg_preview)
            .error(R.drawable.bg_preview)
            .into(ivImagePreview, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "SUCESSO: Imagem do servidor carregada!");
                    ivImagePreview.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "ERRO Picasso servidor: " + e.getMessage());
                    ivImagePreview.setVisibility(View.GONE);
                }
            });
            
    } catch (Exception e) {
        Log.e(TAG, "ERRO ao carregar imagem do servidor", e);
        ivImagePreview.setVisibility(View.GONE);
    }
}
```

### # **4. uploadVideoDirectly (similar):**
```java
private void uploadVideoDirectly(Uri uri) {
    Log.d(TAG, "=== UPLOAD VIDEO DIRETO ===");
    
    try {
        // # Processar vídeo (similar ao ImageUtils mas para vídeo)
        byte[] videoBytes = getVideoBytes(this, uri);
        if (videoBytes == null) {
            Log.e(TAG, "ERRO: getVideoBytes retornou NULL");
            btnPickVideo.setText("Selecionar Vídeo");
            Toast.makeText(this, "Erro ao processar vídeo", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Log.d(TAG, "Vídeo processado: " + videoBytes.length + " bytes");
        
        RequestBody requestFile = RequestBody.create(MediaType.parse("video/mp4"), videoBytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", "exercise.mp4", requestFile);
        
        ExerciseApi api = ApiClient.getExerciseClient().create(ExerciseApi.class);
        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;
        
        api.uploadVideo(authHeader, body).enqueue(new Callback<FileUploadResponse>() {
            @Override
            public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                Log.d(TAG, "Resposta upload vídeo: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    uploadedVideoPath = response.body().getFileUrl();
                    Log.d(TAG, "SUCESSO upload vídeo: " + uploadedVideoPath);
                    
                    // # Mostrar thumbnail do vídeo
                    showVideoThumbnail(uri);
                    
                    btnPickVideo.setText("Vídeo: " + getFileNameFromUri(uri) + "  \u2705");
                    
                } else {
                    Log.e(TAG, "ERRO upload vídeo: " + response.code());
                    btnPickVideo.setText("Selecionar Vídeo");
                    Toast.makeText(this, "Erro no upload: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                Log.e(TAG, "FALHA upload vídeo", t);
                btnPickVideo.setText("Selecionar Vídeo");
                Toast.makeText(this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        
    } catch (Exception e) {
        Log.e(TAG, "ERRO EXCEPTION upload vídeo", e);
        btnPickVideo.setText("Selecionar Vídeo");
        Toast.makeText(this, "Erro ao processar vídeo", Toast.LENGTH_SHORT).show();
    }
}
```

### # **5. getVideoBytes (similar ao ImageUtils):**
```java
private byte[] getVideoBytes(Context context, Uri videoUri) {
    try {
        InputStream inputStream = context.getContentResolver().openInputStream(videoUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        
        inputStream.close();
        return baos.toByteArray();
    } catch (Exception e) {
        Log.e(TAG, "Erro ao processar vídeo", e);
        return null;
    }
}
```

### # **6. showVideoThumbnail (mantém lógica local):**
```java
private void showVideoThumbnail(Uri uri) {
    Log.d(TAG, "Mostrando thumbnail do vídeo");
    
    if (ivVideoPreview == null) return;
    
    try {
        ivVideoPreview.setVisibility(View.VISIBLE);
        
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, uri);
        Bitmap bitmap = retriever.getFrameAtTime(1000000);
        retriever.release();
        
        if (bitmap != null) {
            Bitmap resized = resizeBitmap(bitmap, 400, 300);
            ivVideoPreview.setImageBitmap(resized);
            Log.d(TAG, "Thumbnail do vídeo mostrado");
        } else {
            ivVideoPreview.setImageResource(android.R.drawable.presence_video_online);
        }
        
    } catch (Exception e) {
        Log.e(TAG, "Erro ao mostrar thumbnail vídeo", e);
        ivVideoPreview.setImageResource(android.R.drawable.presence_video_online);
    }
}
```

### # **7. Modificar startUploadAndCreation:**
```java
private void startUploadAndCreation() {
    if (etTitle.getText() == null || etTitle.getText().toString().trim().isEmpty()) {
        etTitle.setError("Título é obrigatório");
        return;
    }

    if (token.isEmpty()) {
        Toast.makeText(this, "Sessão expirada. Faça login novamente.", Toast.LENGTH_LONG).show();
        return;
    }

    // # ESTILO PERFIL: Se já tem upload feito, só criar exercício
    if (uploadedImagePath != null || uploadedVideoPath != null) {
        Log.d(TAG, "Upload já feito - criando exercício diretamente");
        createExerciseFinal();
        return;
    }

    // # Se não tem upload, mostrar erro
    if (imageUri == null && videoUri == null) {
        Toast.makeText(this, "Selecione uma imagem ou vídeo", Toast.LENGTH_SHORT).show();
        return;
    }

    Log.d(TAG, "Nenhum upload feito - aguardando upload direto");
}
```

## # **RESULTADO ESPERADO:**

### # **Fluxo exatamente como o perfil:**
1. # **Seleciona imagem** -> Upload imediato
2. # **Upload concluído** -> Carrega do servidor com Picasso
3. # **Preview aparece** -> Imagem do servidor (não local)
4. # **Funciona 100%** -> Mesmo método que funciona no perfil

---

## # **PARA O GEMINI:**

**Esta é a solução DEFINITIVA - copia exatamente o fluxo do perfil!**

**1. Remover preview local**
**2. Fazer upload direto ao selecionar**
**3. Carregar do servidor com Picasso**
**4. Usar exatamente o mesmo fluxo do ProfileActivity**

**Se o perfil funciona, isso vai funcionar!**
