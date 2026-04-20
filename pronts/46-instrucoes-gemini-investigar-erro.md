# 🔍 INSTRUÇÕES GEMINI - INVESTIGAR CAUSA RAIZ DO ERRO

## 🎯 **OBJETIVO**

**Investigar a causa exata do erro de conexão no login para resolver definitivamente.**

## ✅ **O QUE JÁ FOI CORRIGIDO**

### **ApiClient.java (Gemini aplicou):**
- ✅ Timeouts aumentados para 30s
- ✅ Retry automático habilitado
- ✅ Logs detalhados (HttpLoggingInterceptor.Level.BODY)

## 🔍 **PRÓXIMOS PASSOS PARA INVESTIGAÇÃO**

### **1. VERIFICAR LoginActivity.java**
**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/LoginActivity.java`

**O Gemini precisa:**

#### **A. Adicionar logs detalhados no método de login:**
```java
private void attemptLogin() {
    String email = etEmail.getText().toString().trim();
    String password = etPassword.getText().toString().trim();
    
    Log.d(TAG, "🔐 Tentando login com email: " + email);
    Log.d(TAG, "🌐 BASE_URL: " + Constants.HOST); // 🔥 VERIFICAR URL
    Log.d(TAG, "🌐 URL completa: " + Constants.HOST + "/auth/login");
    
    // Verificar conectividade
    if (!isNetworkAvailable()) {
        Log.e(TAG, "❌ Sem conexão de internet");
        Toast.makeText(this, "Sem conexão de internet", Toast.LENGTH_SHORT).show();
        return;
    }
    
    LoginRequest loginRequest = new LoginRequest(email, password);
    
    authApi.login(loginRequest).enqueue(new Callback<LoginResponse>() {
        @Override
        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
            Log.d(TAG, "📡 Response code: " + response.code());
            Log.d(TAG, "📡 Response message: " + response.message());
            Log.d(TAG, "📡 Headers: " + response.headers());
            
            if (response.isSuccessful() && response.body() != null) {
                LoginResponse loginResponse = response.body();
                Log.d(TAG, "✅ Login sucesso! Token recebido");
                tokenManager.saveToken(loginResponse.getAccessToken());
                navigateToMain();
            } else {
                // 🔥 ERRO DETALHADO
                String errorBody = "";
                try {
                    if (response.errorBody() != null) {
                        errorBody = response.errorBody().string();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Erro ao ler errorBody: " + e.getMessage());
                }
                
                Log.e(TAG, "❌ Login falhou - Code: " + response.code());
                Log.e(TAG, "❌ Login falhou - Message: " + response.message());
                Log.e(TAG, "❌ Login falhou - Body: " + errorBody);
                
                // Mensagem específica para o usuário
                String userMessage = "Falha no login (" + response.code() + ")";
                if (response.code() == 401) {
                    userMessage = "Email ou senha incorretos";
                } else if (response.code() == 500) {
                    userMessage = "Erro interno do servidor";
                } else if (response.code() == 404) {
                    userMessage = "Endpoint não encontrado";
                }
                
                Toast.makeText(LoginActivity.this, userMessage, Toast.LENGTH_LONG).show();
            }
        }
        
        @Override
        public void onFailure(Call<LoginResponse> call, Throwable t) {
            // 🔥 ERRO DE CONEXÃO DETALHADO
            String errorType = t.getClass().getSimpleName();
            String errorMessage = t.getMessage();
            
            Log.e(TAG, "❌ Erro de conexão - Type: " + errorType);
            Log.e(TAG, "❌ Erro de conexão - Message: " + errorMessage);
            Log.e(TAG, "❌ Erro de conexão - Call: " + call.request().url());
            
            // Mensagem específica
            String userMessage = "Erro de conexão";
            if (t instanceof SocketTimeoutException) {
                userMessage = "Timeout - Servidor demorou muito";
            } else if (t instanceof UnknownHostException) {
                userMessage = "Host desconhecido - URL incorreta";
            } else if (t instanceof ConnectException) {
                userMessage = "Conexão recusada - Servidor offline?";
            } else if (t instanceof IOException) {
                userMessage = "Erro de rede: " + errorMessage;
            }
            
            Toast.makeText(LoginActivity.this, userMessage + "\n(" + errorType + ")", Toast.LENGTH_LONG).show();
        }
    });
}

// 🔥 MÉTODO PARA VERIFICAR CONEXÃO
private boolean isNetworkAvailable() {
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
}
```

#### **B. Verificar Constants.java:**
```java
public class Constants {
    // 🔥 VERIFICAR SE ESTÁ CORRETO
    public static final String HOST = "http://10.0.2.2:8080"; // Para emulador
    // OU
    public static final String HOST = "http://localhost:8080"; // Para device real
}
```

### **2. VERIFICAR AuthApi.java**
**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/network/AuthApi.java`

**Verificar se o endpoint está correto:**
```java
@POST("auth/login")
Call<LoginResponse> login(@Body LoginRequest request);
```

### **3. VERIFICAR LoginRequest.java**
**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/models/LoginRequest.java`

**Verificar se tem os campos corretos:**
```java
public class LoginRequest {
    private String email;
    private String password;
    
    // Construtores, getters, setters
}
```

## 🧪 **FLUXO DE TESTE E INVESTIGAÇÃO**

### **Para o Gemini executar:**

1. **Adicionar logs detalhados** na LoginActivity
2. **Verificar Constants.HOST** - está correto?
3. **Testar com usuário válido**: profissional@novo.com / prof123
4. **Verificar os logs no Logcat** - quais erros aparecem?

### **Logs esperados no Logcat:**
```
D/LoginActivity: 🔐 Tentando login com email: profissional@novo.com
D/LoginActivity: 🌐 BASE_URL: http://10.0.2.2:8080
D/LoginActivity: 🌐 URL completa: http://10.0.2.2:8080/auth/login
D/OkHttp: --> POST http://10.0.2.2:8080/auth/login
D/OkHttp: Content-Type: application/json
D/OkHttp: {"email":"profissional@novo.com","password":"prof123"}
D/OkHttp: <-- 401 Unauthorized
D/OkHttp: {"error":{"code":"UNAUTHORIZED","message":"invalid credentials"}}
E/LoginActivity: ❌ Login falhou - Code: 401
E/LoginActivity: ❌ Login falhou - Message: Unauthorized
E/LoginActivity: ❌ Login falhou - Body: {"error":{"code":"UNAUTHORIZED","message":"invalid credentials"}}
```

## 🎯 **POSSÍVEIS CAUSAS E SOLUÇÕES**

### **Se mostrar 401 Unauthorized:**
- **Causa:** Senha incorreta ou hash incompatível
- **Solução:** Verificar se o hash foi criado com pepper correto

### **Se mostrar UnknownHostException:**
- **Causa:** URL incorreta (10.0.2.2 vs localhost)
- **Solução:** Ajustar Constants.HOST

### **Se mostrar ConnectException:**
- **Causa:** Backend offline ou porta errada
- **Solução:** Verificar se backend está rodando na porta 8080

### **Se mostrar SocketTimeoutException:**
- **Causa:** Backend muito lento ou bloqueado
- **Solução:** Aumentar timeout ou verificar backend

### **Se mostrar erro de parse JSON:**
- **Causa:** LoginRequest malformado
- **Solução:** Verificar estrutura JSON

## 📋 **CHECKLIST DE INVESTIGAÇÃO**

- [ ] Adicionar logs detalhados na LoginActivity
- [ ] Verificar Constants.HOST (10.0.2.2 vs localhost)
- [ ] Testar com profissional@novo.com / prof123
- [ ] Verificar logs no Logcat
- [ ] Identificar código de erro exato
- [ ] Corrigir causa raiz baseada no erro

## 🎯 **RESULTADO ESPERADO**

**Após investigação:**
```
❌ Antes: "Erro de conexão" (genérico)
✅ Depois: "Erro 401: Email ou senha incorretos" (específico)
```

---

**Status:** 🔍 **INSTRUÇÕES PRONTAS PARA INVESTIGAÇÃO DETALHADA**
