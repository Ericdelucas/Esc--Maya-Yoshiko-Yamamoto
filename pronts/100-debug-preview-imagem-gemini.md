# # **DEBUG COMPLETO - PREVIEW IMAGEM NÃO APARECE - GEMINI**

## # **DIAGNÓSTICO:**

### # **Código:** 100% CORRETO
- # **Picasso:** Removido
- # **Método nativo:** setImageURI() implementado
- # **Logs:** Todos presentes
- # **Layout:** ImageView existe com visibility="gone"

### # **Problema:** Preview não aparece mesmo com código correto

## # **SOLUÇÃO DE DEBUG COMPLETA:**

### # **1. Adicionar debug AGRESSIVO no showImagePreview:**
```java
private void showImagePreview(Uri uri) {
    Log.d(TAG, "=== DEBUG AGRESSIVO PREVIEW ===");
    Log.d(TAG, "URI: " + uri.toString());
    
    // # VERIFICAÇÃO 1: ImageView existe?
    if (ivImagePreview == null) {
        Log.e(TAG, "ERRO: ivImagePreview é NULL!");
        return;
    }
    Log.d(TAG, "ivImagePreview não é NULL");
    
    // # VERIFICAÇÃO 2: ImageView está no layout?
    Log.d(TAG, "ImageView ID: " + ivImagePreview.getId());
    Log.d(TAG, "Visibility atual: " + ivImagePreview.getVisibility());
    Log.d(TAG, "Width: " + ivImagePreview.getWidth() + " | Height: " + ivImagePreview.getHeight());
    
    try {
        // # FORÇAR VISIBILIDADE
        ivImagePreview.setVisibility(View.VISIBLE);
        Log.d(TAG, "Visibility setada para VISIBLE");
        
        // # VERIFICAÇÃO 3: Realmente ficou visível?
        ivImagePreview.post(() -> {
            Log.d(TAG, "Visibility após post: " + ivImagePreview.getVisibility());
            Log.d(TAG, "Width após post: " + ivImagePreview.getWidth() + " | Height: " + ivImagePreview.getHeight());
        });
        
        // # VERIFICAÇÃO 4: URI é válida?
        Log.d(TAG, "URI Scheme: " + uri.getScheme());
        Log.d(TAG, "URI Path: " + uri.getPath());
        
        // # TESTE: Tentar abrir InputStream
        try (InputStream is = getContentResolver().openInputStream(uri)) {
            if (is != null) {
                Log.d(TAG, "InputStream aberto com sucesso - URI é válida");
            } else {
                Log.e(TAG, "InputStream é NULL - URI inválida");
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao abrir InputStream", e);
            return;
        }
        
        // # MÉTODO NATIVO
        Log.d(TAG, "Chamando setImageURI...");
        ivImagePreview.setImageURI(uri);
        Log.d(TAG, "setImageURI chamado");
        
        // # VERIFICAÇÃO 5: Drawable foi carregado?
        ivImagePreview.postDelayed(() -> {
            Log.d(TAG, "=== VERIFICAÇÃO FINAL ===");
            Log.d(TAG, "Visibility: " + ivImagePreview.getVisibility());
            Log.d(TAG, "Width: " + ivImagePreview.getWidth() + " | Height: " + ivImagePreview.getHeight());
            
            if (ivImagePreview.getDrawable() != null) {
                Log.d(TAG, "SUCESSO: Drawable não é NULL!");
                Log.d(TAG, "Drawable class: " + ivImagePreview.getDrawable().getClass().getSimpleName());
                Log.d(TAG, "Drawable intrinsic size: " + ivImagePreview.getDrawable().getIntrinsicWidth() + "x" + ivImagePreview.getDrawable().getIntrinsicHeight());
            } else {
                Log.e(TAG, "FALHA: Drawable é NULL!");
                Log.e(TAG, "Isso significa que setImageURI falhou");
                
                // # TENTATIVA 2: Usar Bitmap diretamente
                try {
                    InputStream is = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(is);
                    is.close();
                    
                    if (bitmap != null) {
                        Log.d(TAG, "Bitmap carregado diretamente - usando setImageBitmap");
                        ivImagePreview.setImageBitmap(bitmap);
                        Log.d(TAG, "setImageBitmap chamado");
                    } else {
                        Log.e(TAG, "Bitmap também é NULL");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Erro ao carregar Bitmap", e);
                }
            }
        }, 500);
        
    } catch (Exception e) {
        Log.e(TAG, "ERRO EXCEÇÃO", e);
        ivImagePreview.setVisibility(View.VISIBLE);
        ivImagePreview.setImageResource(android.R.drawable.ic_menu_gallery);
        Log.d(TAG, "Placeholder mostrado");
    }
}
```

### # **2. Adicionar debug no onCreate:**
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_exercise);
    
    Log.d(TAG, "=== onCreate DEBUG ===");
    
    // # DEBUG: Verificar se ImageView existe antes de tudo
    ivImagePreview = findViewById(R.id.ivImagePreview);
    if (ivImagePreview == null) {
        Log.e(TAG, "ERRO CRÍTICO: ivImagePreview não encontrado no layout!");
    } else {
        Log.d(TAG, "ivImagePreview encontrado no layout");
        Log.d(TAG, "Visibility inicial: " + ivImagePreview.getVisibility());
    }
    
    // # Continuar com o resto do onCreate...
    initViews();
    setupToolbar();
    setupPickers();
}
```

### # **3. Adicionar botão de debug no layout:**
```xml
<!-- # Adicionar após o ImageView da imagem -->
<Button
    android:id="@+id/btnDebugImage"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="DEBUG: Forçar Preview"
    android:layout_marginBottom="8dp" />
```

### # **4. Adicionar ação do botão de debug:**
```java
// # No initViews(), adicionar:
Button btnDebugImage = findViewById(R.id.btnDebugImage);
btnDebugImage.setOnClickListener(v -> {
    Log.d(TAG, "=== BOTÃO DEBUG CLICADO ===");
    
    if (ivImagePreview == null) {
        Log.e(TAG, "ivImagePreview é NULL");
        Toast.makeText(this, "ImageView é NULL", Toast.LENGTH_SHORT).show();
        return;
    }
    
    // # Forçar visibilidade
    ivImagePreview.setVisibility(View.VISIBLE);
    Log.d(TAG, "Visibility forçada para VISIBLE");
    
    // # Mostrar informações
    String info = "Width: " + ivImagePreview.getWidth() + 
                  " Height: " + ivImagePreview.getHeight() +
                  " Visibility: " + ivImagePreview.getVisibility() +
                  " Drawable: " + (ivImagePreview.getDrawable() != null ? "OK" : "NULL");
    
    Log.d(TAG, "Info ImageView: " + info);
    Toast.makeText(this, info, Toast.LENGTH_LONG).show();
    
    // # Se tiver URI, tentar carregar
    if (imageUri != null) {
        Log.d(TAG, "Tentando carregar URI existente: " + imageUri.toString());
        showImagePreview(imageUri);
    } else {
        Log.w(TAG, "Nenhuma URI de imagem para testar");
        Toast.makeText(this, "Selecione uma imagem primeiro", Toast.LENGTH_SHORT).show();
    }
});
```

### # **5. Teste passo a passo:**
```bash
# # 1. Limpar logs
adb logcat -c

# # 2. Abrir activity e verificar logs iniciais
adb logcat -s "EXERCISE_UPLOAD_DEBUG" | head -20

# # 3. Clicar no botão "DEBUG: Forçar Preview"
adb logcat -s "EXERCISE_UPLOAD_DEBUG" | tail -10

# # 4. Selecionar imagem
adb logcat -s "EXERCISE_UPLOAD_DEBUG" | tail -20
```

### # **6. Logs esperados se tudo funcionar:**
```
EXERCISE_UPLOAD_DEBUG: === onCreate DEBUG ===
EXERCISE_UPLOAD_DEBUG: ivImagePreview encontrado no layout
EXERCISE_UPLOAD_DEBUG: Visibility inicial: 8 (GONE)
EXERCISE_UPLOAD_DEBUG: === SETUP PICKERS ===
EXERCISE_UPLOAD_DEBUG: ivImagePreview: OK
EXERCISE_UPLOAD_DEBUG: === IMAGEM SELECIONADA ===
EXERCISE_UPLOAD_DEBUG: URI: content://...
EXERCISE_UPLOAD_DEBUG: === DEBUG AGRESSIVO PREVIEW ===
EXERCISE_UPLOAD_DEBUG: InputStream aberto com sucesso - URI é válida
EXERCISE_UPLOAD_DEBUG: setImageURI chamado
EXERCISE_UPLOAD_DEBUG: SUCESSO: Drawable não é NULL!
```

### # **7. Se ainda não funcionar, tentar alternativa:**
```java
// # Alternativa com Glide (se disponível)
private void showImagePreviewWithGlide(Uri uri) {
    Log.d(TAG, "Tentando com Glide...");
    
    try {
        Glide.with(this)
            .load(uri)
            .placeholder(R.drawable.bg_preview)
            .error(R.drawable.bg_preview)
            .into(ivImagePreview);
            
        Log.d(TAG, "Glide chamado");
    } catch (Exception e) {
        Log.e(TAG, "Glide não disponível", e);
        showImagePreviewNative(uri);
    }
}
```

## # **POSSÍVEIS CAUSAS SE NÃO FUNCIONAR:**

### # **1. Permissões faltando:**
```xml
<!-- # No AndroidManifest.xml -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" 
                     android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

### # **2. URI inválida ou corrompida:**
- # Verificar se a URI realmente aponta para uma imagem
- # Testar com diferentes imagens da galeria

### # **3. Problema no layout:**
- # ImageView pode estar atrás de outro elemento
- # Layout pode estar corrompido

---

## # **PARA O GEMINI:**

**1. Adicionar o debug AGRESSIVO no showImagePreview**
**2. Adicionar botão de debug no layout**
**3. Verificar os logs passo a passo**
**4. Testar com diferentes imagens**

**Com este debug vamos encontrar EXATAMENTE onde está o problema!**
