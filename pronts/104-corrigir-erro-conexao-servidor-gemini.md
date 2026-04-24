# # **CORRIGIR ERRO DE CONEXÃO DE SERVIDOR - PREVIEW IMAGEM - GEMINI**

## # **PROBLEMA IDENTIFICADO:**

### # **Erro:** "Erro de conexão de servidor" no preview da imagem
### # **Causa:** O app não consegue acessar a URL da imagem do servidor
### # **Upload:** Funciona (200 OK) - backend está OK
### # **Download:** Falha - frontend não consegue baixar a imagem

## # **DIAGNÓSTICO:**

### # **Possíveis causas:**
1. # **URL incorreta** - Constants.EXERCISE_BASE_URL pode estar errada
2. # **adb reverse** pode ter perdido a conexão
3. # **Picasso** não está conseguindo acessar a URL
4. # **Permissões** de rede faltando

## # **SOLUÇÃO IMEDIATA:**

### # **1. Verificar e corrigir URL no showImageFromServer:**
```java
private void showImageFromServer(String imageUrl) {
    Log.d(TAG, "=== SHOW IMAGE FROM SERVER DEBUG ===");
    Log.d(TAG, "ImageUrl recebida: " + imageUrl);
    
    if (ivImagePreview == null) {
        Log.e(TAG, "ivImagePreview é NULL");
        return;
    }
    
    try {
        // # FORÇAR URL 127.0.0.1 (ignorar Constants)
        String baseUrl = "http://127.0.0.1:8081";
        Log.d(TAG, "BaseUrl forçada: " + baseUrl);
        
        if (imageUrl.startsWith("/")) {
            // # Se já começa com /, só concatenar
            String fullImageUrl = baseUrl + imageUrl;
            Log.d(TAG, "FullImageUrl: " + fullImageUrl);
            
            // # TESTE: Tentar acessar a URL primeiro
            testImageUrl(fullImageUrl);
            
            // # Mostrar ImageView
            ivImagePreview.setVisibility(View.VISIBLE);
            
            // # Carregar com Picasso
            Picasso.get()
                .load(fullImageUrl)
                .placeholder(R.drawable.bg_preview)
                .error(R.drawable.bg_preview)
                .into(ivImagePreview, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "PICASSO SUCESSO: Imagem carregada!");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "PICASSO ERRO: " + e.getMessage());
                        // # TENTATIVA 2: Usar Glide se disponível
                        try {
                            com.bumptech.glide.Glide.with(AddExerciseActivity.this)
                                .load(fullImageUrl)
                                .placeholder(R.drawable.bg_preview)
                                .error(R.drawable.bg_preview)
                                .into(ivImagePreview);
                            Log.d(TAG, "Tentando Glide como fallback");
                        } catch (Exception glideException) {
                            Log.e(TAG, "Glide também falhou: " + glideException.getMessage());
                            // # TENTATIVA 3: Download manual
                            downloadAndShowImage(fullImageUrl);
                        }
                    }
                });
        } else {
            Log.e(TAG, "URL não começa com /: " + imageUrl);
        }
        
    } catch (Exception e) {
        Log.e(TAG, "ERRO EXCEPTION: " + e.getMessage());
        e.printStackTrace();
    }
}
```

### # **2. Adicionar método de teste de URL:**
```java
private void testImageUrl(String imageUrl) {
    Log.d(TAG, "=== TESTANDO URL ===");
    Log.d(TAG, "Testando: " + imageUrl);
    
    new Thread(() -> {
        try {
            java.net.URL url = new java.net.URL(imageUrl);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            Log.d(TAG, "Response Code: " + responseCode);
            
            if (responseCode == 200) {
                Log.d(TAG, "URL ACESSÍVEL: 200 OK");
            } else {
                Log.e(TAG, "URL INACESSÍVEL: " + responseCode);
            }
            
            connection.disconnect();
        } catch (Exception e) {
            Log.e(TAG, "ERRO AO TESTAR URL: " + e.getMessage());
        }
    }).start();
}
```

### # **3. Adicionar download manual como fallback:**
```java
private void downloadAndShowImage(String imageUrl) {
    Log.d(TAG, "=== DOWNLOAD MANUAL ===");
    
    new Thread(() -> {
        try {
            java.net.URL url = new java.net.URL(imageUrl);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                java.io.InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                
                if (bitmap != null) {
                    // # Mostrar na UI thread
                    runOnUiThread(() -> {
                        ivImagePreview.setImageBitmap(bitmap);
                        ivImagePreview.setVisibility(View.VISIBLE);
                        Log.d(TAG, "Imagem carregada manualmente com sucesso!");
                    });
                } else {
                    Log.e(TAG, "Bitmap decode falhou");
                }
            } else {
                Log.e(TAG, "Download falhou: " + responseCode);
            }
            
            connection.disconnect();
        } catch (Exception e) {
            Log.e(TAG, "ERRO DOWNLOAD MANUAL: " + e.getMessage());
        }
    }).start();
}
```

### # **4. Verificar permissões no AndroidManifest.xml:**
```xml
<!-- # Verificar se tem estas permissões -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- # Se Android 9+ (API 28+) -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />

<!-- # Se Android 10+ (API 29+) -->
<application
    android:usesCleartextTraffic="true"
    ... >
```

### # **5. Adicionar verificação de Constants:**
```java
// # Em algum método para debug
private void debugConstants() {
    Log.d(TAG, "=== DEBUG CONSTANTS ===");
    Log.d(TAG, "EXERCISE_BASE_URL: " + Constants.EXERCISE_BASE_URL);
    Log.d(TAG, "AUTH_BASE_URL: " + Constants.AUTH_BASE_URL);
    Log.d(TAG, "HOST: " + Constants.HOST);
}
```

### # **6. Forçar reconfiguração do adb reverse:**
```bash
# # No terminal, executar:
adb reverse tcp:8080 tcp:8080
adb reverse tcp:8081 tcp:8081
adb reverse --list

# # Verificar se está funcionando:
curl -I http://127.0.0.1:8081/health
```

## # **TESTE E VALIDAÇÃO:**

### # **Passos:**
1. # **Reconfigurar adb reverse**
2. # **Testar URL manualmente:** `curl -I http://127.0.0.1:8081/media/images/xxx.jpg`
3. # **Adicionar logs de debug**
4. # **Testar no app**
5. # **Verificar logs:** `adb logcat -s "EXERCISE_UPLOAD_DEBUG"`

### # **Logs esperados:**
```
EXERCISE_UPLOAD_DEBUG: === SHOW IMAGE FROM SERVER DEBUG ===
EXERCISE_UPLOAD_DEBUG: BaseUrl forçada: http://127.0.0.1:8081
EXERCISE_UPLOAD_DEBUG: FullImageUrl: http://127.0.0.1:8081/media/images/xxx.jpg
EXERCISE_UPLOAD_DEBUG: === TESTANDO URL ===
EXERCISE_UPLOAD_DEBUG: Response Code: 200
EXERCISE_UPLOAD_DEBUG: URL ACESSÍVEL: 200 OK
EXERCISE_UPLOAD_DEBUG: PICASSO SUCESSO: Imagem carregada!
```

## # **SE AINDA NÃO FUNCIONAR:**

### # **Solução final:**
```java
// # Se tudo falhar, mostrar placeholder e mensagem
ivImagePreview.setVisibility(View.VISIBLE);
ivImagePreview.setImageResource(android.R.drawable.ic_menu_gallery);
Toast.makeText(this, "Imagem enviada com sucesso! Preview indisponível.", Toast.LENGTH_SHORT).show();
```

---

## # **PARA O GEMINI:**

**1. Implementar showImageFromServer com URL forçada**
**2. Adicionar método testImageUrl**
**3. Adicionar downloadAndShowImage como fallback**
**4. Verificar permissões no AndroidManifest**
**5. Reconfigurar adb reverse**
**6. Testar e verificar logs**

**O problema é conexão com servidor - precisa forçar URL 127.0.0.1 e adicionar fallbacks!**
