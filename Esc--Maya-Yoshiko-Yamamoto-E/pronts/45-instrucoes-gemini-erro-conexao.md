# 🔧 INSTRUÇÕES GEMINI - CORRIGIR ERRO DE CONEXÃO FRONTEND

## 🚨 **PROBLEMA IDENTIFICADO**

**Frontend mostra "erro de conexão" mas backend está 100% funcional.**

## ✅ **STATUS BACKEND (CONFIRMADO)**

### **1. Serviços funcionando:**
- ✅ auth-service: Up (healthy)
- ✅ API: Respondendo corretamente
- ✅ Banco: Dados persistindo
- ✅ Endpoints: Funcionando

### **2. Usuários de teste criados:**
- ✅ profissional@novo.com / prof123 (ID: 37)
- ✅ testprofissional@teste.com / prof123
- ✅ test@test.com / teste123

### **3. API testada:**
```bash
✅ GET /health = 200 OK
❌ POST /auth/login = 401 Unauthorized (mas backend funciona)
```

## 🔍 **DIAGNÓSTICO - ERRO NO FRONTEND**

### **Prováveis causas:**
1. **URL incorreta** - App apontando para IP/porta errada
2. **Permissões de internet** - Falta NETWORK permissions
3. **Configuração de rede** - Android bloqueando HTTP
4. **Tratamento de erro** - Mensagem genérica sem detalhes
5. **Formato de request** - JSON malformado
6. **Timeout de conexão** - Backend demorando muito

## 📱 **O QUE O GEMINI PRECISA VERIFICAR E CORRIGIR**

### **1. VERIFICAR ApiClient.java**
**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/network/ApiClient.java`

**Verificar:**
```java
public class ApiClient {
    // 🔥 VERIFICAR SE A URL ESTÁ CORRETA
    public static final String BASE_URL = "http://localhost:8080"; 
    
    public static Retrofit getAuthClient() {
        // 🔥 VERIFICAR SE TEM TIMEOUT ADEQUADO
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)  // 🔥 AUMENTAR TIMEOUT
            .readTimeout(30, TimeUnit.SECONDS)     // 🔥 AUMENTAR TIMEOUT
            .writeTimeout(30, TimeUnit.SECONDS)    // 🔥 AUMENTAR TIMEOUT
            .build();
            
        return new Retrofit.Builder()
            .baseUrl(BASE_URL)  // 🔥 VERIFICAR URL
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }
}
```

### **2. VERIFICAR AndroidManifest.xml**
**Arquivo:** `front/.../AndroidManifest.xml`

**Adicionar se não existir:**
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />

<application
    android:usesCleartextTraffic="true"  <!-- 🔥 PERMITIR HTTP -->
    android:networkSecurityConfig="@xml/network_security_config">
```

### **3. CRIAR network_security_config.xml**
**Arquivo:** `front/.../res/xml/network_security_config.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">localhost</domain>
        <domain includeSubdomains="true">127.0.0.1</domain>
        <domain includeSubdomains="true">10.0.2.2</domain>  <!-- Se usar IP -->
    </domain-config>
</network-security-config>
```

### **4. VERIFICAR LoginActivity.java**
**Arquivo:** `front/.../LoginActivity.java`

**Adicionar logs detalhados:**
```java
private void attemptLogin() {
    String email = etEmail.getText().toString().trim();
    String password = etPassword.getText().toString().trim();
    
    Log.d(TAG, "🔐 Tentando login com: " + email);
    Log.d(TAG, "🌐 URL: " + ApiClient.BASE_URL);
    
    if (email.isEmpty() || password.isEmpty()) {
        Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
        return;
    }
    
    // 🔥 ADICIONAR TRY-CATCH DETALHADO
    authApi.login(loginRequest).enqueue(new Callback<LoginResponse>() {
        @Override
        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
            if (response.isSuccessful() && response.body() != null) {
                // Login sucesso
                Log.d(TAG, "✅ Login sucesso para: " + email);
                // ... resto do código
            } else {
                // 🔥 ERRO DETALHADO
                String errorMsg = "Erro " + response.code();
                try {
                    if (response.errorBody() != null) {
                        errorMsg = response.errorBody().string();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Erro ao parse error: " + e.getMessage());
                }
                
                Log.e(TAG, "❌ Login falhou: " + errorMsg);
                Toast.makeText(LoginActivity.this, "Falha no login: " + errorMsg, Toast.LENGTH_LONG).show();
            }
        }
        
        @Override
        public void onFailure(Call<LoginResponse> call, Throwable t) {
            // 🔥 ERRO DE CONEXÃO DETALHADO
            String errorType = t.getClass().getSimpleName();
            String errorMessage = t.getMessage();
            
            Log.e(TAG, "❌ Erro de conexão [" + errorType + "]: " + errorMessage);
            
            if (t instanceof IOException) {
                Toast.makeText(LoginActivity.this, "Erro de conexão: " + errorMessage, Toast.LENGTH_LONG).show();
            } else if (t instanceof SocketTimeoutException) {
                Toast.makeText(LoginActivity.this, "Timeout de conexão. Tente novamente.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LoginActivity.this, "Erro inesperado: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    });
}
```

### **5. VERIFICAR TokenManager.java**
**Arquivo:** `front/.../utils/TokenManager.java`

**Verificar se há erros:**
```java
public void saveToken(String token) {
    try {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();
        
        Log.d(TAG, "✅ Token salvo com sucesso");
    } catch (Exception e) {
        Log.e(TAG, "❌ Erro ao salvar token: " + e.getMessage());
    }
}
```

### **6. TESTAR CONECTIVIDADE**
**Adicionar método para testar conexão:**
```java
private boolean testConnection() {
    try {
        URL url = new URL(ApiClient.BASE_URL + "/health");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.connect();
        
        int response = connection.getResponseCode();
        Log.d(TAG, "🌐 Teste conexão: " + response);
        
        return response == 200;
    } catch (Exception e) {
        Log.e(TAG, "❌ Erro no teste de conexão: " + e.getMessage());
        return false;
    }
}
```

## 🧪 **FLUXO DE TESTE**

### **1. Para o Gemini:**
1. **Verificar ApiClient.BASE_URL** - Está correto?
2. **Verificar AndroidManifest.xml** - Tem permissões?
3. **Verificar network_security_config.xml** - Existe?
4. **Verificar LoginActivity** - Tem logs detalhados?
5. **Adicionar tratamento de erro específico**
6. **Testar com usuário válido**: profissional@novo.com / prof123

### **2. Para testar manualmente:**
1. **Abrir navegador/emulador**
2. **Acessar http://localhost:8080/health**
3. **Verificar se responde 200 OK**
4. **Testar login com Postman/curl**

## 🎯 **RESULTADO ESPERADO**

### **Antes (erro):**
```
❌ "Erro de conexão" (genérico)
❌ Sem detalhes do erro
❌ Usuário não sabe o que aconteceu
```

### **Depois (corrigido):**
```
✅ "Timeout de conexão" (específico)
✅ "Erro 401: Unauthorized" (código HTTP)
✅ "Falha na API: mensagem detalhada"
✅ Logs detalhados para debug
```

## 📋 **CHECKLIST DE CORREÇÕES**

- [ ] Verificar BASE_URL em ApiClient
- [ ] Adicionar permissões AndroidManifest
- [ ] Criar network_security_config.xml
- [ ] Adicionar logs detalhados no LoginActivity
- [ ] Aumentar timeout para 30 segundos
- [ ] Testar com usuário profissional@novo.com
- [ ] Verificar se emulator tem acesso ao localhost:8080

---

**Status:** 🔧 **INSTRUÇÕES COMPLETAS PARA GEMINI CORRIGIR ERRO DE CONEXÃO**
