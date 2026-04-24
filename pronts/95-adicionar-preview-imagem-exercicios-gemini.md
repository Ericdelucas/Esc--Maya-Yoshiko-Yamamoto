# # **ADICIONAR PREVIEW DE IMAGEM - EXERCÍCIOS - GEMINI**

## # **PROBLEMA IDENTIFICADO:**

### # **O que está acontecendo:**
- # **Usuário seleciona imagem** mas não vê prévia
- # **Aparece área branca** onde deveria mostrar a imagem
- # **Layout atual** só tem botão, não tem ImageView

### # **Layout Atual (Problema):**
```xml
<!-- Upload de Imagem -->
<TextView android:text="Imagem (Thumbnail)" />
<MaterialButton android:id="@+id/btnPickImage" android:text="Selecionar Imagem" />
<!-- # FALTA ImageView PARA MOSTRAR A IMAGEM SELECIONADA! -->
```

## # **SOLUÇÃO COMPLETA:**

### # **1. Atualizar Layout activity_add_exercise.xml**

#### # **Substituir a seção de imagem:**
```xml
<!-- Upload de Imagem -->
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Imagem (Thumbnail)"
    android:textStyle="bold"
    android:layout_marginBottom="8dp" />

<!-- Container para preview e botão -->
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:layout_marginBottom="16dp">

    <!-- Preview da Imagem -->
    <ImageView
        android:id="@+id/ivImagePreview"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:scaleType="centerCrop"
        android:background="@drawable/bg_image_preview"
        android:contentDescription="Preview da imagem do exercício"
        android:layout_marginBottom="8dp"
        android:visibility="gone" />

    <!-- Botão de seleção (muda texto após seleção) -->
    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnPickImage"
        style="@style/Widget.Material3.Button.TonalButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Selecionar Imagem"
        app:icon="@android:drawable/ic_menu_gallery" />

</LinearLayout>
```

#### # **Adicionar seção de vídeo com preview:**
```xml
<!-- Upload de Vídeo -->
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Vídeo Explicativo"
    android:textStyle="bold"
    android:layout_marginBottom="8dp" />

<!-- Container para preview e botão -->
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:layout_marginBottom="32dp">

    <!-- Preview do Vídeo -->
    <ImageView
        android:id="@+id/ivVideoPreview"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:scaleType="centerCrop"
        android:background="@drawable/bg_video_preview"
        android:contentDescription="Preview do vídeo do exercício"
        android:layout_marginBottom="8dp"
        android:visibility="gone" />

    <!-- Botão de seleção -->
    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnPickVideo"
        style="@style/Widget.Material3.Button.TonalButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Selecionar Vídeo"
        app:icon="@android:drawable/presence_video_online" />

</LinearLayout>
```

### # **2. Criar backgrounds para preview**

#### # **Arquivo: res/drawable/bg_image_preview.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="#F5F5F5" />
    <stroke android:width="1dp" android:color="#E0E0E0" />
    <corners android:radius="8dp" />
</shape>
```

#### # **Arquivo: res/drawable/bg_video_preview.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="#F5F5F5" />
    <stroke android:width="1dp" android:color="#E0E0E0" />
    <corners android:radius="8dp" />
</shape>
```

#### # **Ou criar um drawable com ícone de vídeo:**
```xml
<!-- res/drawable/ic_video_placeholder.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="48dp"
    android:height="48dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#757575"
        android:pathData="M17,10.5V7c0,-0.55 -0.45,-1 -1,-1H4c-0.55,0 -1,0.45 -1,1v10c0,0.55 0.45,1 1,1h12c0.55,0 1,-0.45 1,-1v-3.5l4,4v-11l-4,4z" />
</vector>
```

### # **3. Atualizar AddExerciseActivity.java**

#### # **Adicionar imports:**
```java
import android.widget.ImageView;
import com.bumptech.glide.Glide;
```

#### # **Adicionar variáveis:**
```java
private ImageView ivImagePreview, ivVideoPreview;
```

#### # **Atualizar initViews():**
```java
private void initViews() {
    etTitle = findViewById(R.id.etTitle);
    etDescription = findViewById(R.id.etDescription);
    etInstructions = findViewById(R.id.etInstructions);
    btnPickImage = findViewById(R.id.btnPickImage);
    btnPickVideo = findViewById(R.id.btnPickVideo);
    btnSave = findViewById(R.id.btnSaveExercise);
    pbLoading = findViewById(R.id.pbLoading);
    
    // # Adicionar ImageViews de preview
    ivImagePreview = findViewById(R.id.ivImagePreview);
    ivVideoPreview = findViewById(R.id.ivVideoPreview);

    SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
    token = prefs.getString("jwt_token", "");
    String role = prefs.getString("user_role", "Unknown");
    
    Log.d(TAG, "Config: BaseURL=" + Constants.EXERCISE_BASE_URL + " | Role=" + role);
}
```

#### # **Atualizar setupPickers():**
```java
private void setupPickers() {
    ActivityResultLauncher<String> imagePicker = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    String mimeType = getContentResolver().getType(uri);
                    Log.d(TAG, "Imagem selecionada - MIME: " + mimeType);
                    
                    if (mimeType != null && mimeType.startsWith("image/")) {
                        imageUri = uri;
                        
                        // # Mostrar preview da imagem
                        showImagePreview(uri);
                        
                        // # Atualizar texto do botão
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
                        
                        // # Mostrar preview do vídeo (thumbnail)
                        showVideoPreview(uri);
                        
                        // # Atualizar texto do botão
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

#### # **Adicionar métodos de preview:**
```java
private void showImagePreview(Uri uri) {
    // # Mostrar ImageView
    ivImagePreview.setVisibility(View.VISIBLE);
    
    // # Carregar imagem com Glide
    Glide.with(this)
        .load(uri)
        .placeholder(R.drawable.bg_image_preview)
        .error(R.drawable.bg_image_preview)
        .into(ivImagePreview);
    
    Log.d(TAG, "Preview da imagem mostrado: " + uri.toString());
}

private void showVideoPreview(Uri uri) {
    // # Mostrar ImageView
    ivVideoPreview.setVisibility(View.VISIBLE);
    
    // # Carregar thumbnail do vídeo com Glide
    Glide.with(this)
        .load(uri)
        .placeholder(R.drawable.bg_video_preview)
        .error(R.drawable.bg_video_preview)
        .into(ivVideoPreview);
    
    Log.d(TAG, "Preview do vídeo mostrado: " + uri.toString());
}

private void clearPreviews() {
    // # Esconder previews
    ivImagePreview.setVisibility(View.GONE);
    ivVideoPreview.setVisibility(View.GONE);
    
    // # Limpar imagens
    Glide.with(this).clear(ivImagePreview);
    Glide.with(this).clear(ivVideoPreview);
    
    // # Resetar URIs
    imageUri = null;
    videoUri = null;
    
    // # Resetar textos dos botões
    btnPickImage.setText("Selecionar Imagem");
    btnPickVideo.setText("Selecionar Vídeo");
}
```

#### # **Adicionar botão para limpar (opcional):**
```xml
<!-- No layout, após cada ImageView -->
<ImageButton
    android:id="@+id/btnClearImage"
    android:layout_width="32dp"
    android:layout_height="32dp"
    android:layout_gravity="top|end"
    android:layout_marginTop="8dp"
    android:layout_marginEnd="8dp"
    android:background="@android:drawable/ic_menu_close_clear_cancel"
    android:contentDescription="Remover imagem"
    android:visibility="gone" />
```

```java
// # No initViews():
ImageButton btnClearImage = findViewById(R.id.btnClearImage);
btnClearImage.setOnClickListener(v -> {
    imageUri = null;
    ivImagePreview.setVisibility(View.GONE);
    btnPickImage.setText("Selecionar Imagem");
    btnClearImage.setVisibility(View.GONE);
});

// # No showImagePreview():
btnClearImage.setVisibility(View.VISIBLE);
```

### # **4. Verificar dependência Glide**

#### # **Adicionar no build.gradle se não tiver:**
```gradle
dependencies {
    // ... outras dependências
    implementation('com.github.bumptech.glide:glide:4.12.0') {
        exclude group: "com.android.support"
    }
    annotationProcessor 'com.github.bumptech.glide:compiler:4.12.0'
}
```

## # **RESULTADO ESPERADO:**

### # **Após implementação:**
- # **Usuário seleciona imagem** e vê preview imediato
- # **Preview aparece** em 200dp de altura
- # **Botão muda texto** para mostrar nome do arquivo
- # **Interface mais intuitiva** e profissional
- # **Vídeo também mostra** thumbnail

### # **Experiência do usuário:**
1. # **Clica em "Selecionar Imagem"**
2. # **Escolhe imagem da galeria**
3. # **Imagem aparece** no preview imediatamente
4. # **Botão mostra** "Imagem: foto.jpg  \u2705"
5. # **Pode remover** e selecionar outra

---

## # **PARA O GEMINI:**

**1. Atualizar o layout activity_add_exercise.xml com ImageViews**
**2. Criar os drawables bg_image_preview.xml e bg_video_preview.xml**
**3. Adicionar imports e variáveis no AddExerciseActivity**
**4. Implementar showImagePreview() e showVideoPreview() com Glide**
**5. Atualizar setupPickers() para mostrar preview**

**Agora o usuário verá a imagem selecionada imediatamente, não mais área branca!**
