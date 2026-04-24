# # **IMAGEM APARECENDO - SOLUÇÃO SEM GLIDE - GEMINI**

## # **PROBLEMA ATUAL:**

### # **O que está acontecendo:**
- # **Imagem selecionada** mas não aparece no preview
- # **Glide não está** nas dependências do projeto
- # **ImageView continua** invisível ou branco

### # **Verificação:**
```bash
# # No build.gradle.kts não tem Glide:
dependencies {
    # ... outras dependências
    # FALTA: implementation 'com.github.bumptech.glide:glide:4.12.0'
}
```

## # **SOLUÇÃO 1: ADICIONAR GLIDE (RECOMENDADO)**

### # **Adicionar dependência no build.gradle.kts:**
```kotlin
dependencies {
    // ... dependências existentes ...
    
    // # Glide para carregar imagens
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    
    // # ... resto das dependências ...
}
```

### # **Sync do projeto:**
- # **Android Studio** vai pedir sync
- # **Clique em "Sync Now"**
- # **Espere o download** das dependências

## # **SOLUÇÃO 2: SEM GLIDE (IMEDIATA)**

### # **Usar método nativo do Android:**
```java
// # Substituir showImagePreview() por:
private void showImagePreview(Uri uri) {
    try {
        // # Mostrar ImageView
        ivImagePreview.setVisibility(View.VISIBLE);
        
        // # Carregar imagem nativamente
        ivImagePreview.setImageURI(uri);
        
        // # Verificar se carregou
        if (ivImagePreview.getDrawable() == null) {
            // # Se não carregou, mostrar placeholder
            ivImagePreview.setImageResource(R.drawable.ic_image_placeholder);
        }
        
        Log.d(TAG, "Preview da imagem mostrado (nativo): " + uri.toString());
        
    } catch (Exception e) {
        Log.e(TAG, "Erro ao carregar preview (nativo)", e);
        ivImagePreview.setImageResource(R.drawable.ic_image_placeholder);
    }
}

private void showVideoPreview(Uri uri) {
    try {
        // # Mostrar ImageView
        ivVideoPreview.setVisibility(View.VISIBLE);
        
        // # Carregar thumbnail do vídeo nativamente
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, uri);
        Bitmap bitmap = retriever.getFrameAtTime(1000000); // # 1 segundo
        retriever.release();
        
        if (bitmap != null) {
            ivVideoPreview.setImageBitmap(bitmap);
        } else {
            ivVideoPreview.setImageResource(R.drawable.ic_video_placeholder);
        }
        
        Log.d(TAG, "Preview do vídeo mostrado (nativo): " + uri.toString());
        
    } catch (Exception e) {
        Log.e(TAG, "Erro ao carregar preview do vídeo (nativo)", e);
        ivVideoPreview.setImageResource(R.drawable.ic_video_placeholder);
    }
}
```

### # **Adicionar imports necessários:**
```java
import android.media.MediaMetadataRetriever;
import android.graphics.Bitmap;
```

## # **SOLUÇÃO 3: IMPLEMENTAÇÃO COMPLETA IMEDIATA**

### # **Atualizar AddExerciseActivity.java:**
```java
// # No topo da classe, adicionar:
private ImageView ivImagePreview, ivVideoPreview;

// # No initViews():
private void initViews() {
    // ... código existente ...
    
    // # Adicionar ImageViews
    ivImagePreview = findViewById(R.id.ivImagePreview);
    ivVideoPreview = findViewById(R.id.ivVideoPreview);
    
    // # Configurar backgrounds se não existirem
    if (ivImagePreview.getBackground() == null) {
        ivImagePreview.setBackgroundColor(0xFFF5F5F5);
    }
    if (ivVideoPreview.getBackground() == null) {
        ivVideoPreview.setBackgroundColor(0xFFF5F5F5);
    }
}

// # No setupPickers(), atualizar imagePicker:
ActivityResultLauncher<String> imagePicker = registerForActivityResult(
        new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                String mimeType = getContentResolver().getType(uri);
                Log.d(TAG, "Imagem selecionada - MIME: " + mimeType);
                
                if (mimeType != null && mimeType.startsWith("image/")) {
                    imageUri = uri;
                    
                    // # MOSTRAR PREVIEW IMEDIATO
                    showImagePreviewNative(uri);
                    
                    // # Atualizar texto do botão
                    String fileName = getFileNameFromUri(uri);
                    btnPickImage.setText("Imagem: " + fileName + "  \u2705");
                    
                    Log.d(TAG, "Imagem válida e preview mostrado: " + fileName);
                } else {
                    Log.e(TAG, "Arquivo inválido: " + mimeType);
                    Toast.makeText(this, "Selecione apenas arquivos de imagem", Toast.LENGTH_SHORT).show();
                }
            }
        });

// # Método nativo para preview:
private void showImagePreviewNative(Uri uri) {
    try {
        Log.d(TAG, "Tentando mostrar preview: " + uri.toString());
        
        // # Mostrar ImageView
        ivImagePreview.setVisibility(View.VISIBLE);
        
        // # Carregar imagem
        ivImagePreview.setImageURI(uri);
        
        // # Forçar refresh
        ivImagePreview.post(() -> {
            if (ivImagePreview.getDrawable() != null) {
                Log.d(TAG, "Preview carregado com sucesso!");
            } else {
                Log.w(TAG, "Preview não carregou, usando placeholder");
                ivImagePreview.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        });
        
    } catch (Exception e) {
        Log.e(TAG, "Erro ao mostrar preview", e);
        ivImagePreview.setVisibility(View.VISIBLE);
        ivImagePreview.setImageResource(android.R.drawable.ic_menu_gallery);
    }
}
```

### # **Criar placeholders simples:**
```xml
<!-- res/drawable/ic_image_placeholder.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="48dp"
    android:height="48dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="#757575">
    <path
        android:fillColor="@android:color/white"
        android:pathData="M21,19V5c0,-1.1 -0.9,-2 -2,-2H5c-1.1,0 -2,0.9 -2,2v14c0,1.1 0.9,2 2,2h14c1.1,0 2,-0.9 2,-2zM8.5,13.5l2.5,3.01L14.5,12l4.5,6H5l3.5,-4.5z" />
</vector>
```

```xml
<!-- res/drawable/ic_video_placeholder.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="48dp"
    android:height="48dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="#757575">
    <path
        android:fillColor="@android:color/white"
        android:pathData="M17,10.5V7c0,-0.55 -0.45,-1 -1,-1H4c-0.55,0 -1,0.45 -1,1v10c0,0.55 0.45,1 1,1h12c0.55,0 1,-0.45 1,-1v-3.5l4,4v-11l-4,4z" />
</vector>
```

## # **SOLUÇÃO 4: DEBUG PASSO A PASSO**

### # **Adicionar logs para debug:**
```java
private void debugImagePreview(Uri uri) {
    Log.d(TAG, "=== DEBUG IMAGE PREVIEW ===");
    Log.d(TAG, "URI: " + uri.toString());
    Log.d(TAG, "Scheme: " + uri.getScheme());
    Log.d(TAG, "Path: " + uri.getPath());
    Log.d(TAG, "LastPathSegment: " + uri.getLastPathSegment());
    
    try {
        String mimeType = getContentResolver().getType(uri);
        Log.d(TAG, "MIME Type: " + mimeType);
        
        // # Testar se ImageView existe
        if (ivImagePreview == null) {
            Log.e(TAG, "ImageView ivImagePreview é NULL!");
            return;
        }
        
        // # Testar se URI é válido
        try (InputStream is = getContentResolver().openInputStream(uri)) {
            if (is == null) {
                Log.e(TAG, "Não foi possível abrir InputStream do URI!");
                return;
            }
            Log.d(TAG, "InputStream aberto com sucesso!");
        }
        
        // # Mostrar ImageView
        ivImagePreview.setVisibility(View.VISIBLE);
        Log.d(TAG, "ImageView visibility set to VISIBLE");
        
        // # Carregar imagem
        ivImagePreview.setImageURI(uri);
        Log.d(TAG, "setImageURI chamado");
        
        // # Verificar resultado após um pequeno delay
        ivImagePreview.postDelayed(() -> {
            if (ivImagePreview.getDrawable() != null) {
                Log.d(TAG, "SUCESSO: Imagem carregada no ImageView!");
            } else {
                Log.e(TAG, "FALHA: Drawable é NULL após carregamento!");
            }
        }, 500);
        
    } catch (Exception e) {
        Log.e(TAG, "Exception no debug", e);
    }
}
```

### # **Chamar debug no setupPickers:**
```java
// # No imagePicker callback:
if (mimeType != null && mimeType.startsWith("image/")) {
    imageUri = uri;
    debugImagePreview(uri); // # DEBUG
    String fileName = getFileNameFromUri(uri);
    btnPickImage.setText("Imagem: " + fileName + "  \u2705");
}
```

## # **RESULTADO ESPERADO:**

### # **Após correção:**
- # **Preview aparece** imediatamente ao selecionar
- # **Logs detalhados** para identificar problemas
- # **Fallback com placeholder** se falhar
- # **Funciona sem Glide** (nativo)

### # **Logs esperados:**
```
EXERCISE_UPLOAD_DEBUG: === DEBUG IMAGE PREVIEW ===
EXERCISE_UPLOAD_DEBUG: URI: content://media/external/images/1
EXERCISE_UPLOAD_DEBUG: MIME Type: image/jpeg
EXERCISE_UPLOAD_DEBUG: InputStream aberto com sucesso!
EXERCISE_UPLOAD_DEBUG: ImageView visibility set to VISIBLE
EXERCISE_UPLOAD_DEBUG: SUCESSO: Imagem carregada no ImageView!
```

---

## # **PARA O GEMINI:**

**OPÇÃO 1 (RECOMENDADA):**
1. # Adicionar dependência Glide no build.gradle.kts
2. # Fazer sync do projeto
3. # Usar implementação com Glide

**OPÇÃO 2 (IMEDIATA):**
1. # Usar showImagePreviewNative() sem Glide
2. # Adicionar debugImagePreview() para logs
3. # Verificar logs para identificar problema

**Comece com a opção 2 (sem Glide) para funcionar imediatamente!**
