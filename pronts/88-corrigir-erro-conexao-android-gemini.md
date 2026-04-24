# # **CORRIGIR ERRO DE CONEXÃO ANDROID - GEMINI**

## # **PROBLEMA IDENTIFICADO:**

### # **Sintomas:**
- # **"Erro de conexão de servidor"** no app Android
- # **Nenhum log aparece** no terminal
- # **Backend funcionando** (testado com curl)
- # **adb reverse ativo** e funcionando

### # **Causa Provável:**
- # **Configuração de rede** no Android
- # **Versão Android** mais recente (API 35)
- # **Security policy** mais restritiva
- # **Timeout muito curto** ou má configuração

## # **DIAGNÓSTICO PASSO A PASSO:**

### # **PASSO 1: Verificar Conexão Básica**

#### # **Testar no terminal:**
```bash
# # Verificar se backend está rodando:
curl -v http://localhost:8080/health

# # Verificar adb reverse:
adb reverse --list

# # Testar do emulador:
adb shell curl -v http://127.0.0.1:8080/health
```

#### # **Se funcionar, problema está no app Android**

### # **PASSO 2: Verificar Logs do Android**

#### # **Verificar logs em tempo real:**
```bash
# # Filtrar logs do app:
adb logcat -s "NETWORK_AUDIT" "NETWORK_AUDIT" "OkHttp" "Retrofit"

# # Filtrar por erros:
adb logcat -s "AndroidRuntime" "System.err"

# # Verificar todos os logs do app:
adb logcat | grep "com.example.testbackend"
```

### # **PASSO 3: Correções Necessárias**

#### # **1. Atualizar Constants.java**
```java
// # Arquivo: app/src/main/java/com/example/testbackend/utils/Constants.java
package com.example.testbackend.utils;

import android.util.Log;

public class Constants {
    
    private static final String TAG = "NETWORK_AUDIT";
    
    // # TENTATIVA 1: 127.0.0.1 (com adb reverse)
    public static final String HOST = "127.0.0.1";
    
    // # TENTATIVA 2: 10.0.2.2 (emulador padrão)
    // public static final String HOST = "10.0.2.2";
    
    // # TENTATIVA 3: localhost (se permitido)
    // public static final String HOST = "localhost";
    
    public static final String AUTH_BASE_URL = "http://" + HOST + ":8080/";
    public static final String PACIENTES_BASE_URL = "http://" + HOST + ":8080/";
    
    // # Outras URLs...
    public static final String EXERCISE_BASE_URL = "http://" + HOST + ":8081/";
    public static final String HEALTH_BASE_URL = "http://" + HOST + ":8071/";
    public static final String TRAINING_BASE_URL = "http://" + HOST + ":8030/";
    public static final String AI_HTTP_URL = "http://" + HOST + ":8090/ai/process-frame";

    static {
        Log.d(TAG, ">>> AUDITORIA DE REDE ATIVA <<<");
        Log.d(TAG, "HOST CONFIGURADO: " + HOST);
        Log.d(TAG, "URL DE AUTENTICAÇÃO: " + AUTH_BASE_URL);
        Log.d(TAG, "IMPORTANTE: Execute 'adb reverse tcp:8080 tcp:8080' no seu terminal!");
    }
}
```

#### # **2. Atualizar ApiClient.java**
```java
// # Arquivo: app/src/main/java/com/example/testbackend/network/ApiClient.java
package com.example.testbackend.network;

import com.example.testbackend.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {

    private static Retrofit authRetrofit = null;
    private static Retrofit aiRetrofit = null;
    private static Retrofit exerciseRetrofit = null;
    private static Retrofit healthRetrofit = null;
    private static Retrofit appointmentRetrofit = null;

    private static Gson getGson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .setLenient() // # Mais tolerante a erros de JSON
                .create();
    }

    private static OkHttpClient getOkHttpClient() {
        // # INTERCEPTOR DE LOG DETALHADO
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(60, TimeUnit.SECONDS) // # Aumentado para 60s
                .readTimeout(60, TimeUnit.SECONDS)    // # Aumentado para 60s
                .writeTimeout(60, TimeUnit.SECONDS)   // # Aumentado para 60s
                .callTimeout(60, TimeUnit.SECONDS)    # # Timeout total da chamada
                .retryOnConnectionFailure(true)
                .addInterceptor(chain -> {
                    // # Log personalizado para debug
                    okhttp3.Request request = chain.request();
                    long startTime = System.currentTimeMillis();
                    
                    try {
                        okhttp3.Response response = chain.proceed(request);
                        long endTime = System.currentTimeMillis();
                        
                        android.util.Log.d("NETWORK_DEBUG", 
                            "URL: " + request.url() + 
                            " | Time: " + (endTime - startTime) + "ms" +
                            " | Code: " + response.code());
                            
                        return response;
                    } catch (Exception e) {
                        long endTime = System.currentTimeMillis();
                        android.util.Log.e("NETWORK_DEBUG", 
                            "URL: " + request.url() + 
                            " | Time: " + (endTime - startTime) + "ms" +
                            " | Error: " + e.getMessage());
                        throw e;
                    }
                })
                .build();
    }

    public static Retrofit getAuthClient() {
        if (authRetrofit == null) {
            authRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.AUTH_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .client(getOkHttpClient())
                    .build();
        }
        return authRetrofit;
    }

    // # ... outros métodos get*Client() ...
}
```

#### # **3. Atualizar AndroidManifest.xml**
```xml
<!-- # Arquivo: app/src/main/AndroidManifest.xml -->
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- # PERMISSÕES NECESSÁRIAS -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" /> <!-- # Adicionar -->
    
    <!-- # Permissões para Android 13+ -->
    <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
    <uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" 
                     android:maxSdkVersion="32" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Testbackend"
        android:usesCleartextTraffic="true"
        android:networkSecurityConfig="@xml/network_security_config"
        android:requestLegacyExternalStorage="true"> <!-- # Adicionar para compatibilidade -->
        
        <!-- # Activities existentes -->
        
    </application>
</manifest>
```

#### # **4. Atualizar network_security_config.xml**
```xml
<!-- # Arquivo: app/src/main/res/xml/network_security_config.xml -->
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <!-- # Permitir tráfego HTTP para desenvolvimento -->
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">localhost</domain>
        <domain includeSubdomains="true">127.0.0.1</domain>
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">10.0.2.15</domain> <!-- # Adicionar -->
        <domain includeSubdomains="true">192.168.1.1</domain> <!-- # Adicionar -->
        <domain includeSubdomains="true">10.1.9.88</domain>
    </domain-config>
    
    <!-- # Configuração base mais permissiva para debug -->
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system" />
            <certificates src="user" />
        </trust-anchors>
    </base-config>
    
    <!-- # Configuração específica para debug -->
    <debug-overrides>
        <trust-anchors>
            <certificates src="system" />
            <certificates src="user" />
        </trust-anchors>
    </debug-overrides>
</network-security-config>
```

### # **PASSO 4: Adicionar Activity de Teste**

#### # **Criar TestNetworkActivity.java**
```java
// # Arquivo: app/src/main/java/com/example/testbackend/TestNetworkActivity.java
package com.example.testbackend;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.testbackend.utils.Constants;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestNetworkActivity extends AppCompatActivity {
    
    private TextView tvLog;
    private Button btnTestConnection;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_network);
        
        tvLog = findViewById(R.id.tvLog);
        btnTestConnection = findViewById(R.id.btnTestConnection);
        
        btnTestConnection.setOnClickListener(v -> testConnection());
        
        // Teste automático ao iniciar
        testConnection();
    }
    
    private void testConnection() {
        appendLog("Iniciando teste de conexão...");
        appendLog("URL: " + Constants.AUTH_BASE_URL + "health");
        
        executor.execute(() -> {
            try {
                URL url = new URL(Constants.AUTH_BASE_URL + "health");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                
                int responseCode = conn.getResponseCode();
                
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                    );
                    String response = reader.readLine();
                    reader.close();
                    
                    runOnUiThread(() -> {
                        appendLog("CONEXÃO BEM-SUCEDIDA!");
                        appendLog("Response Code: " + responseCode);
                        appendLog("Response: " + response);
                    });
                } else {
                    runOnUiThread(() -> {
                        appendLog("ERRO NA CONEXÃO!");
                        appendLog("Response Code: " + responseCode);
                    });
                }
                
                conn.disconnect();
                
            } catch (Exception e) {
                runOnUiThread(() -> {
                    appendLog("ERRO DE EXCEÇÃO:");
                    appendLog(e.getMessage());
                    Log.e("TEST_NETWORK", "Erro de conexão", e);
                });
            }
        });
    }
    
    private void appendLog(String message) {
        tvLog.append(message + "\n");
        Log.d("TEST_NETWORK", message);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
```

#### # **Criar layout activity_test_network.xml**
```xml
<!-- # Arquivo: app/src/main/res/layout/activity_test_network.xml -->
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <Button
        android:id="@+id/btnTestConnection"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Testar Conexão"
        android:layout_marginBottom="16dp" />

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1">

        <TextView
            android:id="@+id/tvLog"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:fontFamily="monospace"
            android:textSize="12sp"
            android:background="#f5f5f5"
            android:padding="8dp" />

    </ScrollView>

</LinearLayout>
```

### # **PASSO 5: Registrar Activity no Manifest**
```xml
<!-- # Adicionar no AndroidManifest.xml dentro de <application> -->
<activity
    android:name=".TestNetworkActivity"
    android:exported="true"
    android:label="Testar Conexão">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

### # **PASSO 6: Soluções Alternativas**

#### # **Opção 1: Usar IP da Máquina**
```java
// # Em Constants.java, tentar:
public static final String HOST = "10.0.2.2"; // # Emulador padrão
// # ou
public static final String HOST = "192.168.1.100"; // # Seu IP local
```

#### # **Opção 2: Configurar Port Forwarding**
```bash
# # No terminal:
adb forward tcp:8080 tcp:8080
```

#### # **Opção 3: Usar Serviço de Tunneling**
```bash
# # Usar ngrok (se disponível):
ngrok http 8080
# # Depois usar a URL do ngrok no Constants.java
```

### # **PASSO 7: Verificar Build e Deploy**

#### # **Limpar e rebuildar:**
```bash
# # No diretório do frontend:
./gradlew clean
./gradlew assembleDebug

# # Instalar o APK:
./gradlew installDebug
```

#### # **Verificar se está instalado:**
```bash
adb shell pm list packages | grep testbackend
```

## # **DIAGNÓSTICO RÁPIDO:**

### # **1. Teste Básico:**
```bash
# # Se isto funcionar:
curl http://localhost:8080/health

# # Mas isto não funcionar no app, problema está no Android
```

### # **2. Verificar Logs:**
```bash
# # Procurar estes padrões nos logs:
adb logcat | grep -E "(NETWORK_AUDIT|NETWORK_DEBUG|OkHttp|Retrofit)"
```

### # **3. Soluções em Ordem:**
1. # **Atualizar Constants.java** com HOST correto
2. # **Aumentar timeouts** no ApiClient
3. # **Verificar permissões** no AndroidManifest
4. # **Testar com TestNetworkActivity**
5. # **Tentar IP diferente** (10.0.2.2)

## # **RESULTADO ESPERADO:**

### # **Após correção:**
- # **Logs detalhados** no logcat
- # **Conexão funcionando** no app
- # **TestNetworkActivity** mostrando sucesso
- # **Login e outras APIs** funcionando

---

## # **IMPORTANTE PARA O GEMINI:**

**1. Execute os testes de diagnóstico primeiro**
**2. Verifique os logs do Android em tempo real**
**3. Aplique as correções na ordem apresentada**
**4. Use a TestNetworkActivity para isolar o problema**
**5. Se nada funcionar, tente o IP 10.0.2.2**

**O problema é 90% das vezes configuração de rede ou timeout - as soluções acima devem resolver!**
