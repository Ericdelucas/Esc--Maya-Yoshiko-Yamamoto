# 🚨 DEBUG COMPLETO - APP TESTE SIMPLES

## ❌ **PROBLEMA GRAVE IDENTIFICADO**

### **Nem a tela de cadastro funciona!**
- **Login:** "erro Docker"
- **Cadastro:** Também não funciona
- **Problema:** Fundamental - conexão com backend

## 🔍 **DIAGNÓSTICO NECESSÁRIO**

### **O problema pode ser:**
1. **Network Security Config** - Bloqueando HTTP
2. **AndroidManifest** - Faltando permissões
3. **Constants.java** - IP ainda errado
4. **Firewall** - Device não consegue acessar o PC
5. **Backend** - Não aceitando conexões externas

## 🛠️ **SOLUÇÃO 1: APP TESTE SIMPLES**

### **Criar activity de teste:**
```java
// TestConnectionActivity.java
package com.example.testbackend;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestConnectionActivity extends AppCompatActivity {
    
    private static final String TAG = "CONNECTION_TEST";
    private TextView tvResult;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Layout simples via código
        tvResult = new TextView(this);
        tvResult.setText("Aguardando teste...");
        tvResult.setTextSize(16);
        tvResult.setPadding(20, 20, 20, 20);
        
        Button btnTest = new Button(this);
        btnTest.setText("Testar Conexão");
        btnTest.setOnClickListener(v -> testConnection());
        
        // Adicionar ao layout
        androidx.appcompat.widget.LinearLayoutCompat layout = new androidx.appcompat.widget.LinearLayoutCompat(this);
        layout.setOrientation(androidx.appcompat.widget.LinearLayoutCompat.VERTICAL);
        layout.addView(tvResult);
        layout.addView(btnTest);
        setContentView(layout);
    }
    
    private void testConnection() {
        new Thread(() -> {
            try {
                Log.d(TAG, "🔍 Testando conexão...");
                
                // Testar diferentes URLs
                String[] urls = {
                    "http://10.1.9.88:8080/health",
                    "http://localhost:8080/health",
                    "http://127.0.0.1:8080/health",
                    "http://10.0.2.2:8080/health"
                };
                
                for (String url : urls) {
                    try {
                        Log.d(TAG, "🌐 Testando: " + url);
                        String result = makeHttpRequest(url);
                        Log.d(TAG, "✅ Sucesso: " + url + " → " + result);
                        
                        runOnUiThread(() -> {
                            tvResult.setText("✅ SUCESSO!\n" + url + "\n" + result);
                        });
                        return;
                        
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Falha: " + url + " → " + e.getMessage());
                        runOnUiThread(() -> {
                            tvResult.setText(tvResult.getText() + "\n❌ Falha: " + url + "\n" + e.getMessage() + "\n");
                        });
                    }
                }
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Erro geral: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    tvResult.setText("❌ ERRO GERAL: " + e.getMessage());
                });
            }
        }).start();
    }
    
    private String makeHttpRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return "Response: " + response.toString();
        } else {
            throw new Exception("HTTP " + responseCode);
        }
    }
}
```

### **Adicionar ao AndroidManifest.xml:**
```xml
<activity android:name=".TestConnectionActivity" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

## 🔧 **SOLUÇÃO 2: VERIFICAÇÃO COMPLETA**

### **1. Verificar AndroidManifest.xml:**
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- 🔥 PERMISSÕES ESSENCIAIS -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <application
        android:usesCleartextTraffic="true"
        android:networkSecurityConfig="@xml/network_security_config">
        
        <!-- Activities... -->
    </application>
</manifest>
```

### **2. Verificar network_security_config.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
    
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.1.9.88</domain>
        <domain includeSubdomains="true">localhost</domain>
        <domain includeSubdomains="true">127.0.0.1</domain>
        <domain includeSubdomains="true">10.0.2.2</domain>
    </domain-config>
</network-security-config>
```

### **3. Verificar Constants.java (forçado):**
```java
public class Constants {
    // 🔥 FORÇAR TODAS AS OPÇÕES PARA TESTE
    public static final String[] TEST_HOSTS = {
        "10.1.9.88",
        "localhost", 
        "127.0.0.1",
        "10.0.2.2"
    };
    
    public static final String HOST = "10.1.9.88"; // Mudar aqui para testar
    public static final String AUTH_BASE_URL = "http://" + HOST + ":8080/";
}
```

## 🧪 **SOLUÇÃO 3: TESTE PASSO A PASSO**

### **Passo 1: Testar conectividade básica:**
```bash
# No PC onde o backend está rodando:
python3 -m http.server 8080

# No device (via adb shell):
curl http://10.1.9.88:8080
```

### **Passo 2: Testar com app simples:**
1. **Criar TestConnectionActivity**
2. **Compilar e instalar**
3. **Abrir e testar**
4. **Ver logs**: `adb logcat | grep "CONNECTION_TEST"`

### **Passo 3: Verificar cada componente:**
```bash
# 1. Backend está rodando?
docker-compose ps

# 2. Porta está aberta?
netstat -tlnp | grep 8080

# 3. IP está correto?
hostname -I

# 4. Device consegue pingar?
adb shell ping 10.1.9.88
```

## 📱 **INSTRUÇÕES PARA GEMINI**

### **1. Criar TestConnectionActivity:**
- Copiar o código acima
- Adicionar ao AndroidManifest.xml
- Compilar e testar

### **2. Verificar logs:**
```bash
adb logcat | grep "CONNECTION_TEST"
```

### **3. Testar cada IP:**
- Mudar Constants.HOST para cada opção
- Recompilar e testar
- Anotar qual funciona

### **4. Se nada funcionar:**
- Verificar firewall do PC
- Verificar se device está na mesma rede
- Tentar usar WiFi em vez de dados móveis

## 🎯 **RESULTADO ESPERADO**

### **Se TestConnectionActivity funcionar:**
```
✅ SUCESSO!
http://10.1.9.88:8080/health
Response: {"status":"ok"}
```

### **Se não funcionar:**
```
❌ Falha: http://10.1.9.88:8080/health
Connection refused / Network unreachable
```

---

**Status:** 🚨 **PROBLEMA GRAVE - PRECISA DEBUG COMPLETO COM APP TESTE**
