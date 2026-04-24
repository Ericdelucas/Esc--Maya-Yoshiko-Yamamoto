# # **CORRIGIR getFileFromUri - EXERCÍCIOS - GEMINI**

## # **PROBLEMA IDENTIFICADO:**

### # **Log do Android:**
```
EXERCISE_UPLOAD_DEBUG: Erro no upload da imagem (403 geralmente indica permissão): 400
```

### # **Causa Principal:**
- # **getFileFromUri()** não preserva extensão do arquivo
- # **Backend recebe arquivo sem extensão** (ex: "img_upload123.tmp")
- # **Validação de imagem falha** por falta de extensão

### # **Problema no Código Atual:**
```java
private File getFileFromUri(Uri uri, String prefix) throws Exception {
    InputStream is = getContentResolver().openInputStream(uri);
    File tempFile = File.createTempFile(prefix, null, getCacheDir()); // # SEM EXTENSÃO!
    // ...
    return tempFile; // # Retorna "img_upload123.tmp" sem extensão
}
```

## # **SOLUÇÃO COMPLETA:**

### # **1. Corrigir getFileFromUri com extensão:**
```java
private File getFileFromUri(Uri uri, String prefix) throws Exception {
    Log.d(TAG, "Convertendo URI: " + uri.toString());
    
    InputStream is = getContentResolver().openInputStream(uri);
    if (is == null) {
        throw new Exception("Não foi possível abrir URI: " + uri);
    }
    
    // # Obter nome e extensão do arquivo original
    String originalFileName = getFileNameFromUri(uri);
    String extension = getFileExtension(originalFileName);
    
    // # Criar arquivo temporário COM EXTENSÃO
    File tempFile = File.createTempFile(prefix + "_", extension, getCacheDir());
    
    Log.d(TAG, "Arquivo original: " + originalFileName);
    Log.d(TAG, "Extensão: " + extension);
    Log.d(TAG, "Arquivo temporário: " + tempFile.getName());
    
    // # Copiar conteúdo
    FileOutputStream fos = new FileOutputStream(tempFile);
    byte[] buffer = new byte[1024];
    int read;
    while ((read = is.read(buffer)) != -1) {
        fos.write(buffer, 0, read);
    }
    fos.close();
    is.close();
    
    Log.d(TAG, "Arquivo criado: " + tempFile.getAbsolutePath() + " (" + tempFile.length() + " bytes)");
    return tempFile;
}

private String getFileNameFromUri(Uri uri) {
    String fileName = "temp_image";
    
    if (uri.getScheme().equals("content")) {
        try (android.database.Cursor cursor = getContentResolver().query(
                uri, 
                new String[]{android.provider.OpenableColumns.DISPLAY_NAME}, 
                null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao obter nome do arquivo", e);
        }
    } else {
        fileName = uri.getLastPathSegment();
    }
    
    return fileName != null ? fileName : "temp_image";
}

private String getFileExtension(String fileName) {
    if (fileName == null) return ".jpg";
    
    int lastDot = fileName.lastIndexOf('.');
    if (lastDot != -1 && lastDot < fileName.length() - 1) {
        return fileName.substring(lastDot);
    }
    
    // # Se não tiver extensão, tentar detectar pelo MIME type
    String mimeType = getContentResolver().getType(imageUri);
    if (mimeType != null) {
        switch (mimeType) {
            case "image/jpeg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/webp":
                return ".webp";
            case "image/gif":
                return ".gif";
            case "video/mp4":
                return ".mp4";
            case "video/3gp":
                return ".3gp";
            case "video/webm":
                return ".webm";
            default:
                return ".jpg"; // # Padrão para imagens
        }
    }
    
    return ".jpg"; // # Padrão final
}
```

### # **2. Melhorar uploadImage com validação:**
```java
private void uploadImage() {
    try {
        Log.d(TAG, "=== INICIANDO UPLOAD DE IMAGEM ===");
        
        // # Validações
        if (imageUri == null) {
            Log.e(TAG, "ERRO: imageUri é nulo");
            handleError("Selecione uma imagem primeiro");
            return;
        }
        
        String mimeType = getContentResolver().getType(imageUri);
        Log.d(TAG, "MIME Type detectado: " + mimeType);
        
        if (mimeType == null || !mimeType.startsWith("image/")) {
            Log.e(TAG, "ERRO: Arquivo não é imagem. MIME: " + mimeType);
            handleError("Selecione apenas arquivos de imagem");
            return;
        }
        
        // # Criar arquivo com extensão correta
        File file = getFileFromUri(imageUri, "img_upload");
        
        // # Validações do arquivo
        if (file.length() == 0) {
            Log.e(TAG, "ERRO: Arquivo vazio");
            handleError("Arquivo está vazio");
            return;
        }
        
        if (file.length() > 10 * 1024 * 1024) { // # 10MB
            Log.e(TAG, "ERRO: Arquivo muito grande: " + file.length() + " bytes");
            handleError("Arquivo muito grande. Máximo 10MB");
            return;
        }
        
        Log.d(TAG, "Arquivo válido: " + file.getName() + " (" + file.length() + " bytes)");
        
        // # Criar request
        RequestBody requestFile = RequestBody.create(file, MediaType.parse(mimeType));
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        
        ExerciseApi api = ApiClient.getExerciseClient().create(ExerciseApi.class);
        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;
        
        Log.d(TAG, "Enviando para: " + Constants.EXERCISE_BASE_URL + "exercises/upload/image");
        Log.d(TAG, "Auth header: " + authHeader.substring(0, 30) + "...");
        Log.d(TAG, "File name: " + file.getName());
        Log.d(TAG, "Content-Type: " + mimeType);
        
        api.uploadImage(authHeader, body).enqueue(new Callback<FileUploadResponse>() {
            @Override
            public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                Log.d(TAG, "Resposta do servidor - Code: " + response.code());
                
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
                    
                    Log.e(TAG, "ERRO NO UPLOAD:");
                    Log.e(TAG, "Code: " + response.code());
                    Log.e(TAG, "Message: " + response.message());
                    Log.e(TAG, "Body: " + errorBody);
                    
                    handleError("Erro no upload: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                Log.e(TAG, "FALHA DE REDE", t);
                handleError("Erro de conexão: " + t.getMessage());
            }
        });
        
    } catch (Exception e) {
        Log.e(TAG, "ERRO EXCEPTION", e);
        handleError("Erro ao processar imagem: " + e.getMessage());
    }
}
```

### # **3. Melhorar setupPickers com validação:**
```java
private void setupPickers() {
    ActivityResultLauncher<String> imagePicker = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    String mimeType = getContentResolver().getType(uri);
                    Log.d(TAG, "Imagem selecionada - MIME: " + mimeType);
                    
                    if (mimeType != null && mimeType.startsWith("image/")) {
                        imageUri = uri;
                        String fileName = getFileNameFromUri(uri);
                        btnPickImage.setText("Imagem: " + fileName + "  \u2705");
                        Log.d(TAG, "Imagem válida: " + fileName);
                    } else {
                        Log.e(TAG, "Arquivo inválido: " + mimeType);
                        Toast.makeText(this, "Selecione apenas arquivos de imagem", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    ActivityResultLauncher<String> videoPicker = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    String mimeType = getContentResolver().getType(uri);
                    Log.d(TAG, "Vídeo selecionado - MIME: " + mimeType);
                    
                    if (mimeType != null && mimeType.startsWith("video/")) {
                        videoUri = uri;
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
        Log.d(TAG, "Abrindo seletor de imagens");
        imagePicker.launch("image/*");
    });
    
    btnPickVideo.setOnClickListener(v -> {
        Log.d(TAG, "Abrindo seletor de vídeos");
        videoPicker.launch("video/*");
    });
}
```

### # **4. Adicionar import necessário:**
```java
// # No topo do AddExerciseActivity.java, adicionar:
import android.database.Cursor;
```

## # **TESTE E VALIDAÇÃO:**

### # **Passos:**
1. # **Substituir o método getFileFromUri** completo
2. # **Adicionar os métodos auxiliares** getFileNameFromUri e getFileExtension
3. # **Melhorar o uploadImage** com validações
4. # **Adicionar import do Cursor**
5. # **Testar upload de imagem**

### # **Logs esperados após correção:**
```
EXERCISE_UPLOAD_DEBUG: Convertendo URI: content://...
EXERCISE_UPLOAD_DEBUG: Arquivo original: foto.jpg
EXERCISE_UPLOAD_DEBUG: Extensão: .jpg
EXERCISE_UPLOAD_DEBUG: Arquivo temporário: img_upload_123.jpg
EXERCISE_UPLOAD_DEBUG: Arquivo criado: /path/img_upload_123.jpg (1024567 bytes)
EXERCISE_UPLOAD_DEBUG: SUCESSO - Imagem: /uploads/exercises/image_456.jpg
```

## # **RESULTADO ESPERADO:**

### # **Após correção:**
- # **Arquivo temporário** com extensão correta
- # **Backend aceita** arquivo de imagem
- # **Upload funciona** corretamente
- # **Logs detalhados** para debug

---

## # **PARA O GEMINI:**

**1. Substitua completamente o método getFileFromUri**
**2. Adicione os métodos auxiliares getFileNameFromUri e getFileExtension**
**3. Adicione o import android.database.Cursor**
**4. Melhore o uploadImage com as validações**
**5. Teste com uma imagem JPG ou PNG**

**O problema é que o arquivo temporário não tinha extensão - o backend rejeitava por isso!**
