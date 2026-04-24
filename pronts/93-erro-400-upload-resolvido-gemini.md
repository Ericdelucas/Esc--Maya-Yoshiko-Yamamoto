# # **ERRO 400 BAD REQUEST - UPLOAD RESOLVIDO! - GEMINI**

## # **PROBLEMA ATUAL:**

### # **Status:**
- # **403 Forbidden:** 100% RESOLVIDO! 
- # **400 Bad Request:** **Erro de validação de arquivo** (bom sinal!)

### # **Logs Atuais:**
```
smartsaude-exercise | POST /exercises/upload/image HTTP/1.1" 400 Bad Request
smartsaude-auth     | GET /auth/verify HTTP/1.1" 200 OK
```

### # **Causa do 400:**
- # **Arquivo inválido** sendo enviado
- # **Extensão não permitida** para upload de imagem
- # **Backend está funcionando** e validando corretamente

## # **DIAGNÓSTICO CONFIRMADO:**

### # **Teste Backend:**
```bash
# # Teste com arquivo inválido:
curl -X POST "http://localhost:8081/exercises/upload/image" \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@test.txt"
# # Resultado: {"detail":"Image extension .txt not allowed"}

# # Teste com arquivo válido (deve funcionar):
curl -X POST "http://localhost:8081/exercises/upload/image" \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@valid_image.jpg"
```

## # **SOLUÇÃO PARA O APP ANDROID:**

### # **Problema no AddExerciseActivity:**
```java
// # VERIFICAR ESTES PONTOS NO CÓDIGO:

private void uploadImage() {
    try {
        // # 1. Verificar se imageUri não é nulo
        if (imageUri == null) {
            Log.e(TAG, "ERRO: imageUri é nulo!");
            handleError("Selecione uma imagem primeiro");
            return;
        }
        
        // # 2. Verificar tipo MIME
        String mimeType = getContentResolver().getType(imageUri);
        Log.d(TAG, "MIME Type detectado: " + mimeType);
        
        if (mimeType == null || !mimeType.startsWith("image/")) {
            Log.e(TAG, "ERRO: Arquivo não é imagem! MIME: " + mimeType);
            handleError("Selecione apenas arquivos de imagem");
            return;
        }
        
        // # 3. Verificar se arquivo existe
        try (InputStream is = getContentResolver().openInputStream(imageUri)) {
            if (is == null) {
                Log.e(TAG, "ERRO: Não foi possível abrir o arquivo");
                handleError("Arquivo não encontrado ou corrompido");
                return;
            }
        }
        
        // # 4. Criar arquivo temporário corretamente
        File file = getFileFromUri(imageUri, "img_upload");
        Log.d(TAG, "Arquivo criado: " + file.getAbsolutePath() + " (" + file.length() + " bytes)");
        
        // # 5. Verificar tamanho do arquivo
        if (file.length() == 0) {
            Log.e(TAG, "ERRO: Arquivo vazio!");
            handleError("Arquivo está vazio");
            return;
        }
        
        // # 6. Criar request corretamente
        RequestBody requestFile = RequestBody.create(file, MediaType.parse(mimeType));
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        
        Log.d(TAG, "Enviando upload - MIME: " + mimeType + ", Size: " + file.length());
        
        // # 7. Fazer upload
        ExerciseApi api = ApiClient.getExerciseClient().create(ExerciseApi.class);
        api.uploadImage("Bearer " + token, body).enqueue(new Callback<FileUploadResponse>() {
            // ... callback existente ...
        });
        
    } catch (Exception e) {
        Log.e(TAG, "ERRO EXCEPTION NO UPLOAD", e);
        handleError("Erro ao processar imagem: " + e.getMessage());
    }
}
```

### # **Correção no getFileFromUri:**
```java
private File getFileFromUri(Uri uri, String prefix) throws Exception {
    Log.d(TAG, "Convertendo URI para arquivo: " + uri.toString());
    
    InputStream is = getContentResolver().openInputStream(uri);
    if (is == null) {
        throw new Exception("Não foi possível abrir o URI: " + uri);
    }
    
    // # Criar arquivo com extensão correta
    String fileName = getFileNameFromUri(uri);
    File tempFile = File.createTempFile(prefix, fileName, getCacheDir());
    
    FileOutputStream fos = new FileOutputStream(tempFile);
    byte[] buffer = new byte[1024];
    int read;
    while ((read = is.read(buffer)) != -1) {
        fos.write(buffer, 0, read);
    }
    fos.close();
    is.close();
    
    Log.d(TAG, "Arquivo temporário criado: " + tempFile.getAbsolutePath() + " (" + tempFile.length() + " bytes)");
    return tempFile;
}

private String getFileNameFromUri(Uri uri) {
    String fileName = "temp_image";
    
    // # Tentar obter nome do arquivo do URI
    if (uri.getScheme().equals("content")) {
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex);
                }
            }
        }
    } else {
        fileName = uri.getLastPathSegment();
    }
    
    // # Garantir que tenha extensão
    if (!fileName.contains(".")) {
        String mimeType = getContentResolver().getType(uri);
        if (mimeType != null) {
            switch (mimeType) {
                case "image/jpeg":
                    fileName += ".jpg";
                    break;
                case "image/png":
                    fileName += ".png";
                    break;
                case "image/webp":
                    fileName += ".webp";
                    break;
                default:
                    fileName += ".jpg";
            }
        }
    }
    
    return fileName;
}
```

### # **Melhoria no setupPickers:**
```java
private void setupPickers() {
    ActivityResultLauncher<String> imagePicker = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    // # Validar URI antes de aceitar
                    String mimeType = getContentResolver().getType(uri);
                    if (mimeType != null && mimeType.startsWith("image/")) {
                        imageUri = uri;
                        btnPickImage.setText("Imagem: " + uri.getLastPathSegment() + "  \u2705");
                        Log.d(TAG, "Imagem válida selecionada: " + mimeType);
                    } else {
                        Log.e(TAG, "Arquivo inválido selecionado: " + mimeType);
                        Toast.makeText(this, "Selecione apenas arquivos de imagem", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    ActivityResultLauncher<String> videoPicker = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    String mimeType = getContentResolver().getType(uri);
                    if (mimeType != null && mimeType.startsWith("video/")) {
                        videoUri = uri;
                        btnPickVideo.setText("Vídeo: " + uri.getLastPathSegment() + "  \u2705");
                        Log.d(TAG, "Vídeo válido selecionado: " + mimeType);
                    } else {
                        Log.e(TAG, "Arquivo inválido selecionado: " + mimeType);
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

## # **TESTE E VALIDAÇÃO:**

### # **Passos para testar:**
1. # **Adicionar logs detalhados** no AddExerciseActivity
2. # **Selecionar imagem válida** (jpg, png, webp)
3. # **Verificar logs** para ver MIME type detectado
4. # **Verificar tamanho** do arquivo
5. # **Testar upload** e observar resposta

### # **Logs esperados:**
```
EXERCISE_UPLOAD_DEBUG: MIME Type detectado: image/jpeg
EXERCISE_UPLOAD_DEBUG: Arquivo criado: /path/temp_img_upload.jpg (1024567 bytes)
EXERCISE_UPLOAD_DEBUG: Enviando upload - MIME: image/jpeg, Size: 1024567
EXERCISE_UPLOAD_DEBUG: SUCESSO - Imagem: /uploads/exercises/image_123.jpg
```

### # **Se ainda falhar:**
```java
// # Teste sem upload (apenas texto):
private void createExerciseWithoutFiles() {
    // # Preencher apenas os campos de texto
    // # Não selecionar imagem nem vídeo
    // # Deve criar exercício com sucesso
}
```

## # **RESULTADO ESPERADO:**

### # **Após correção:**
- # **Upload de imagem** funcionando
- # **Upload de vídeo** funcionando
- # **Validação de MIME type** correta
- # **Mensagens de erro** claras para usuário
- # **Logs detalhados** para debug

---

## # **PARA O GEMINI:**

**1. Adicionar validação de MIME type no setupPickers**
**2. Melhorar o método getFileFromUri para preservar extensão**
**3. Adicionar logs detalhados no uploadImage**
**4. Testar primeiro sem arquivos (apenas texto)**
**5. Depois testar com imagem válida**

**O erro 400 é bom - significa que autenticação funciona e só precisa corrigir a validação de arquivos no app!**
