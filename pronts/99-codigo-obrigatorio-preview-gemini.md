# # **CÓDIGO OBRIGATÓRIO - PREVIEW IMAGEM - GEMINI**

## # **INSTRUÇÕES ESPECÍFICAS - NÃO PODE IGNORAR!**

### # **SUBSTITUA showImagePreview POR ESTE CÓDIGO EXATAMENTE:**
```java
private void showImagePreview(Uri uri) {
    Log.d(TAG, "=== PREVIEW IMAGEM (MÉTODO NATIVO) ===");
    Log.d(TAG, "URI: " + uri.toString());
    
    // # VERIFICAÇÃO OBRIGATÓRIA
    if (ivImagePreview == null) {
        Log.e(TAG, "ERRO FATAL: ivImagePreview é NULL!");
        return;
    }
    
    try {
        // # OBRIGATÓRIO: Mostrar ImageView
        ivImagePreview.setVisibility(View.VISIBLE);
        Log.d(TAG, "ImageView visibilidade: VISIBLE");
        
        // # OBRIGATÓRIO: Método nativo (SEM PICASSO!)
        ivImagePreview.setImageURI(uri);
        Log.d(TAG, "setImageURI chamado com: " + uri.toString());
        
        // # OBRIGATÓRIO: Verificação após delay
        ivImagePreview.postDelayed(() -> {
            if (ivImagePreview.getDrawable() != null) {
                Log.d(TAG, "SUCESSO: Imagem carregada no ImageView!");
                Log.d(TAG, "Drawable: " + ivImagePreview.getDrawable().getClass().getSimpleName());
            } else {
                Log.e(TAG, "FALHA: Drawable é NULL após carregamento!");
                // # OBRIGATÓRIO: Mostrar placeholder
                ivImagePreview.setImageResource(android.R.drawable.ic_menu_gallery);
                Log.d(TAG, "Placeholder mostrado");
            }
        }, 300);
        
    } catch (Exception e) {
        Log.e(TAG, "ERRO EXCEÇÃO ao carregar preview", e);
        ivImagePreview.setVisibility(View.VISIBLE);
        ivImagePreview.setImageResource(android.R.drawable.ic_menu_gallery);
    }
}
```

### # **SUBSTITUA showVideoPreview POR ESTE CÓDIGO EXATAMENTE:**
```java
private void showVideoPreview(Uri uri) {
    Log.d(TAG, "=== PREVIEW VÍDEO ===");
    Log.d(TAG, "URI: " + uri.toString());
    
    // # VERIFICAÇÃO OBRIGATÓRIA
    if (ivVideoPreview == null) {
        Log.e(TAG, "ERRO: ivVideoPreview é NULL!");
        return;
    }
    
    try {
        // # OBRIGATÓRIO: Mostrar ImageView
        ivVideoPreview.setVisibility(View.VISIBLE);
        
        // # OBRIGATÓRIO: Tentar obter thumbnail
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, uri);
        Bitmap bitmap = retriever.getFrameAtTime(1000000); // # 1 segundo
        retriever.release();
        
        if (bitmap != null) {
            ivVideoPreview.setImageBitmap(bitmap);
            Log.d(TAG, "Thumbnail do vídeo carregado");
        } else {
            // # OBRIGATÓRIO: Fallback para ícone de vídeo
            ivVideoPreview.setImageResource(android.R.drawable.presence_video_online);
            Log.d(TAG, "Usando placeholder de vídeo");
        }
        
    } catch (Exception e) {
        Log.e(TAG, "ERRO ao carregar thumbnail do vídeo", e);
        ivVideoPreview.setVisibility(View.VISIBLE);
        ivVideoPreview.setImageResource(android.R.drawable.presence_video_online);
    }
}
```

### # **SUBSTITUA setupPickers POR ESTE CÓDIGO EXATAMENTE:**
```java
private void setupPickers() {
    Log.d(TAG, "=== SETUP PICKERS ===");
    
    // # OBRIGATÓRIO: Verificar ImageViews
    ivImagePreview = findViewById(R.id.ivImagePreview);
    ivVideoPreview = findViewById(R.id.ivVideoPreview);
    
    Log.d(TAG, "ivImagePreview: " + (ivImagePreview != null ? "OK" : "NULL"));
    Log.d(TAG, "ivVideoPreview: " + (ivVideoPreview != null ? "OK" : "NULL"));
    
    ActivityResultLauncher<String> imagePicker = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                Log.d(TAG, "=== IMAGEM SELECIONADA ===");
                Log.d(TAG, "URI: " + (uri != null ? uri.toString() : "NULL"));
                
                if (uri != null) {
                    String mimeType = getContentResolver().getType(uri);
                    Log.d(TAG, "MIME Type: " + mimeType);
                    
                    if (mimeType != null && mimeType.startsWith("image/")) {
                        imageUri = uri;
                        
                        // # OBRIGATÓRIO: Chamar showImagePreview
                        showImagePreview(uri);
                        
                        String fileName = getFileNameFromUri(uri);
                        btnPickImage.setText("Imagem: " + fileName + "  \u2705");
                        
                        Log.d(TAG, "Imagem válida e preview chamado: " + fileName);
                    } else {
                        Log.e(TAG, "Arquivo inválido: " + mimeType);
                        Toast.makeText(this, "Selecione apenas arquivos de imagem", Toast.LENGTH_SHORT).show();
                    }
                }
            });

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

## # **REGRAS OBRIGATÓRIAS:**

### # **1. NÃO PODE USAR PICASSO EM showImagePreview**
- # **PROIBIDO:** Picasso.get()
- # **OBRIGATÓRIO:** ivImagePreview.setImageURI(uri)

### # **2. TEM QUE TER OS LOGS ESPECÍFICOS**
- # **OBRIGATÓRIO:** "=== PREVIEW IMAGEM (MÉTODO NATIVO) ==="
- # **OBRIGATÓRIO:** "SUCESSO: Imagem carregada!" ou "FALHA: Drawable é NULL!"

### # **3. TEM QUE TER VERIFICAÇÃO**
- # **OBRIGATÓRIO:** ivImagePreview.postDelayed(() -> {...}, 300)
- # **OBRIGATÓRIO:** if (ivImagePreview.getDrawable() != null)

### # **4. TEM QUE TER TRATAMENTO DE ERRO**
- # **OBRIGATÓRIO:** try-catch em showImagePreview
- # **OBRIGATÓRIO:** Log.e(TAG, "ERRO EXCEÇÃO", e)

## # **SE O GEMINI NÃO CUMPRIR:**

### # **É IMPOSSÍVEL NÃO CUMPRIR ESTAS INSTRUÇÕES:**
1. # **Copiar e colar** o código exatamente como está acima
2. # **Não adicionar** Picasso.get() em showImagePreview
3. # **Não remover** os logs obrigatórios
4. # **Não remover** a verificação postDelayed

### # **TESTE OBRIGATÓRIO:**
```bash
# # Limpar logs
adb logcat -c

# # Selecionar imagem e verificar:
adb logcat -s "EXERCISE_UPLOAD_DEBUG"

# # TEM QUE APARECER:
EXERCISE_UPLOAD_DEBUG: === PREVIEW IMAGEM (MÉTODO NATIVO) ===
EXERCISE_UPLOAD_DEBUG: SUCESSO: Imagem carregada no ImageView!
```

## # **CONCLUSÃO:**

**O GEMINI DEVE USAR ESTE CÓDIGO EXATAMENTE COMO ESTÁ. NÃO PODE MODIFICAR. NÃO PODE ADICIONAR PICASSO. NÃO PODE REMOVER LOGS. É OBRIGATÓRIO!**
