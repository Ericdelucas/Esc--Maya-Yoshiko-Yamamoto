# # **CORRIGIR PREVIEW COM PICASSO - EXERCÍCIOS - GEMINI**

## # **STATUS ATUAL:**

### # **Funcionando:**
- # **Upload de imagem:** 200 OK
- # **Upload de vídeo:** 200 OK  
- # **Criação exercício:** 200 OK
- # **Picasso:** Instalado no build.gradle.kts

### # **Problema:**
- # **Preview da imagem** não aparece
- # **ImageView continua** branco/invisível
- # **Picasso.get()** pode não estar funcionando

## # **DIAGNÓSTICO:**

### # **Código atual:**
```java
private void showImagePreview(Uri uri) {
    ivImagePreview.setVisibility(View.VISIBLE);
    Picasso.get()
            .load(uri)
            .placeholder(R.drawable.bg_preview)
            .error(R.drawable.bg_preview)
            .into(ivImagePreview);
}
```

### # **Possíveis causas:**
- # **Picasso não inicializado** corretamente
- # **URI não acessível** pelo Picasso
- # **Permissões faltando** para acessar URI
- # **ImageView não encontrado** no layout

## # **SOLUÇÃO COMPLETA:**

### # **1. Adicionar debug completo:**
```java
// # No topo do AddExerciseActivity, adicionar:
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;

private void showImagePreview(Uri uri) {
    Log.d(TAG, "=== DEBUG PREVIEW IMAGEM ===");
    Log.d(TAG, "URI: " + uri.toString());
    Log.d(TAG, "Scheme: " + uri.getScheme());
    
    // # Verificar se ImageView existe
    if (ivImagePreview == null) {
        Log.e(TAG, "ERRO: ivImagePreview é NULL!");
        return;
    }
    
    // # Mostrar ImageView
    ivImagePreview.setVisibility(View.VISIBLE);
    Log.d(TAG, "ImageView visibility set to VISIBLE");
    
    // # Verificar se Picasso está disponível
    try {
        Picasso picasso = Picasso.get();
        Log.d(TAG, "Picasso.get() funcionou");
        
        // # Carregar imagem com callback
        picasso.load(uri)
                .placeholder(R.drawable.bg_preview)
                .error(R.drawable.bg_preview)
                .into(ivImagePreview, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "SUCESSO: Imagem carregada com Picasso!");
                    }
                    
                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "ERRO Picasso: " + e.getMessage());
                        // # Tentar método nativo como fallback
                        showImagePreviewNative(uri);
                    }
                });
                
    } catch (Exception e) {
        Log.e(TAG, "ERRO ao obter Picasso.get()", e);
        // # Fallback para método nativo
        showImagePreviewNative(uri);
    }
}

// # Método nativo como fallback
private void showImagePreviewNative(Uri uri) {
    Log.d(TAG, "Tentando método nativo para preview");
    
    try {
        ivImagePreview.setVisibility(View.VISIBLE);
        ivImagePreview.setImageURI(uri);
        
        // # Verificar se carregou
        ivImagePreview.postDelayed(() -> {
            if (ivImagePreview.getDrawable() != null) {
                Log.d(TAG, "SUCESSO: Imagem carregada com método nativo!");
            } else {
                Log.e(TAG, "FALHA: Método nativo também não funcionou");
                // # Mostrar placeholder
                ivImagePreview.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }, 500);
        
    } catch (Exception e) {
        Log.e(TAG, "ERRO no método nativo", e);
        ivImagePreview.setImageResource(android.R.drawable.ic_menu_gallery);
    }
}
```

### # **2. Inicializar Picasso no Application:**
```java
// # Criar classe Application se não existir:
// app/src/main/java/com/example/testbackend/AppApplication.java

package com.example.testbackend;

import android.app.Application;
import com.squareup.picasso.Picasso;

public class AppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // # Inicializar Picasso com configurações
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.loggingEnabled(true); // # Ativar logs do Picasso
        Picasso.setSingletonInstance(builder.build());
        
        Log.d("APP_INIT", "Picasso inicializado com logging");
    }
}
```

### # **3. Adicionar no AndroidManifest.xml:**
```xml
<!-- # Na tag <application> adicionar: -->
<application
    android:name=".AppApplication"
    android:allowBackup="true"
    ... resto dos atributos ...>
```

### # **4. Melhorar setupPickers com debug:**
```java
private void setupPickers() {
    Log.d(TAG, "Configurando pickers de imagem/vídeo");
    
    // # Verificar se ImageViews existem
    ivImagePreview = findViewById(R.id.ivImagePreview);
    ivVideoPreview = findViewById(R.id.ivVideoPreview);
    
    if (ivImagePreview == null) {
        Log.e(TAG, "ERRO: ivImagePreview não encontrado no layout!");
    } else {
        Log.d(TAG, "ivImagePreview encontrado no layout");
    }
    
    if (ivVideoPreview == null) {
        Log.e(TAG, "ERRO: ivVideoPreview não encontrado no layout!");
    } else {
        Log.d(TAG, "ivVideoPreview encontrado no layout");
    }
    
    ActivityResultLauncher<String> imagePicker = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    Log.d(TAG, "Imagem selecionada: " + uri.toString());
                    
                    String mimeType = getContentResolver().getType(uri);
                    Log.d(TAG, "MIME Type: " + mimeType);
                    
                    if (mimeType != null && mimeType.startsWith("image/")) {
                        imageUri = uri;
                        
                        // # MOSTRAR PREVIEW COM DEBUG
                        showImagePreview(uri);
                        
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
                    Log.d(TAG, "Vídeo selecionado: " + uri.toString());
                    
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
        Log.d(TAG, "Botão de imagem clicado");
        imagePicker.launch("image/*");
    });
    
    btnPickVideo.setOnClickListener(v -> {
        Log.d(TAG, "Botão de vídeo clicado");
        videoPicker.launch("video/*");
    });
}
```

### # **5. Adicionar permissões se necessário:**
```xml
<!-- # No AndroidManifest.xml, verificar se tem: -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" 
                     android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

### # **6. Verificar getFileNameFromUri:**
```java
private String getFileNameFromUri(Uri uri) {
    String fileName = "temp_image";
    
    if (uri == null) {
        Log.e(TAG, "getFileNameFromUri: URI é NULL");
        return fileName;
    }
    
    if (uri.getScheme().equals("content")) {
        try (android.database.Cursor cursor = getContentResolver().query(
                uri, 
                new String[]{android.provider.OpenableColumns.DISPLAY_NAME}, 
                null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex);
                    Log.d(TAG, "Nome do arquivo obtido: " + fileName);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao obter nome do arquivo", e);
        }
    } else {
        fileName = uri.getLastPathSegment();
        Log.d(TAG, "Nome do arquivo (path): " + fileName);
    }
    
    return fileName != null ? fileName : "temp_image";
}
```

## # **TESTE E VALIDAÇÃO:**

### # **Passos para testar:**
1. # **Adicionar AppApplication.java** e configurar no manifest
2. # **Adicionar debug completo** no showImagePreview
3. # **Verificar logs** ao selecionar imagem
4. # **Testar fallback nativo** se Picasso falhar

### # **Logs esperados:**
```
EXERCISE_UPLOAD_DEBUG: === DEBUG PREVIEW IMAGEM ===
EXERCISE_UPLOAD_DEBUG: URI: content://media/external/images/1
EXERCISE_UPLOAD_DEBUG: ivImagePreview encontrado no layout
EXERCISE_UPLOAD_DEBUG: Picasso.get() funcionou
EXERCISE_UPLOAD_DEBUG: SUCESSO: Imagem carregada com Picasso!
```

### # **Se Picasso falhar:**
```
EXERCISE_UPLOAD_DEBUG: ERRO Picasso: Failed to create image decoder
EXERCISE_UPLOAD_DEBUG: Tentando método nativo para preview
EXERCISE_UPLOAD_DEBUG: SUCESSO: Imagem carregada com método nativo!
```

## # **RESULTADO ESPERADO:**

### # **Após correção:**
- # **Preview aparece** imediatamente ao selecionar imagem
- # **Logs detalhados** para identificar problemas
- # **Fallback nativo** se Picasso não funcionar
- # **Funciona com ou sem** Picasso

---

## # **PARA O GEMINI:**

**1. Criar AppApplication.java para inicializar Picasso**
**2. Adicionar android:name=".AppApplication" no manifest**
**3. Substituir showImagePreview() com debug e fallback**
**4. Adicionar debug no setupPickers()**
**5. Verificar logs ao testar**

**O upload está funcionando perfeitamente - só precisa corrigir o preview com estas melhorias!**
