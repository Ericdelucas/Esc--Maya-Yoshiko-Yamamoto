# 🚀 Solução Completa: Botão Criar Novo Usuário Android + Backend SmartSaúde

## 📋 Sumário
Este documento combina todas as correções necessárias para fazer o botão "Criar Novo Usuário" funcionar no app Android com o backend SmartSaúde.

---

## 🎯 Problemas Identificados

### ✅ Backend - Schema do Banco (RESOLVIDO)
- **Erro**: `(1054, "Unknown column 'users.full_name' in 'field list'")`
- **Causa**: Modelo SQLAlchemy tinha colunas que não existiam no banco
- **Solução**: Atualizar schema do banco

### ✅ Frontend Android (A RESOLVER)
- **Problema**: Botão só mostrava Toast placeholder
- **Solução**: Implementar comunicação HTTP completa

---

## 🔧 PARTE 1: CORREÇÃO DO BACKEND

### 1.1 Atualizar Schema do Banco
**Arquivo**: `/Backend/database/init.sql`

```sql
-- init.sql - VERSÃO CORRIGIDA
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL DEFAULT 'Patient',
    full_name VARCHAR(255) NULL,                    -- ✅ NOVO
    profile_photo_url TEXT NULL,                     -- ✅ NOVO  
    updated_at TIMESTAMP NULL,                       -- ✅ NOVO
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- ✅ MANTIDO
    INDEX idx_email (email)
);
```

### 1.2 Recriar Banco
```bash
docker-compose down -v
docker-compose up --build
```

---

## 🔧 PARTE 2: CORREÇÃO DO ANDROID

### 2.1 Adicionar Dependências HTTP
**Arquivo**: `/front/Esc--Maya-Yoshiko-Yamamoto.git/Neon/Login/app/build.gradle.kts`

```kotlin
dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    // ✅ NOVAS DEPENDÊNCIAS HTTP
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
```

### 2.2 Adicionar Permissões
**Arquivo**: `AndroidManifest.xml`

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

---

## 🔧 PARTE 3: CLASSES DE MODELO API

### 3.1 RegisterRequest.java
**Localização**: `/Neon/Login/app/src/main/java/com/example/esclogin/models/RegisterRequest.java`

```java
package com.example.esclogin.models;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("email")
    private String email;
    
    @SerializedName("password") 
    private String password;
    
    @SerializedName("role")
    private String role = "Patient";  // Valor padrão
    
    public RegisterRequest(String email, String password) {
        this.email = email;
        this.password = password;
        this.role = "Patient";
    }
    
    // Getters
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    
    // Setters
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
}
```

### 3.2 RegisterResponse.java
**Localização**: `/Neon/Login/app/src/main/java/com/example/esclogin/models/RegisterResponse.java`

```java
package com.example.esclogin.models;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("user_id")
    private int userId;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("status")
    private String status;
    
    // Getters
    public int getUserId() { return userId; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
    
    // Setters
    public void setUserId(int userId) { this.userId = userId; }
    public void setMessage(String message) { this.message = message; }
    public void setStatus(String status) { this.status = status; }
}
```

### 3.3 LoginRequest.java
**Localização**: `/Neon/Login/app/src/main/java/com/example/esclogin/models/LoginRequest.java`

```java
package com.example.esclogin.models;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("email")
    private String email;
    
    @SerializedName("password")
    private String password;
    
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    // Getters
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    
    // Setters
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}
```

### 3.4 LoginResponse.java
**Localização**: `/Neon/Login/app/src/main/java/com/example/esclogin/models/LoginResponse.java`

```java
package com.example.esclogin.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("access_token")
    private String accessToken;
    
    @SerializedName("token_type")
    private String tokenType;
    
    @SerializedName("expires_in")
    private int expiresIn;
    
    @SerializedName("user")
    private UserInfo user;
    
    // Getters
    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
    public int getExpiresIn() { return expiresIn; }
    public UserInfo getUser() { return user; }
    
    // Setters
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public void setExpiresIn(int expiresIn) { this.expiresIn = expiresIn; }
    public void setUser(UserInfo user) { this.user = user; }
    
    // Classe interna para info do usuário
    public static class UserInfo {
        @SerializedName("id")
        private int id;
        
        @SerializedName("email")
        private String email;
        
        @SerializedName("role")
        private String role;
        
        // Getters
        public int getId() { return id; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        
        // Setters
        public void setId(int id) { this.id = id; }
        public void setEmail(String email) { this.email = email; }
        public void setRole(String role) { this.role = role; }
    }
}
```

### 3.5 ApiError.java
**Localização**: `/Neon/Login/app/src/main/java/com/example/esclogin/models/ApiError.java`

```java
package com.example.esclogin.models;

import com.google.gson.annotations.SerializedName;

public class ApiError {
    @SerializedName("detail")
    private String detail;
    
    @SerializedName("error")
    private String error;
    
    // Getters
    public String getDetail() { return detail; }
    public String getError() { return error; }
    
    // Setters
    public void setDetail(String detail) { this.detail = detail; }
    public void setError(String error) { this.error = error; }
    
    @Override
    public String toString() {
        return detail != null ? detail : (error != null ? error : "Erro desconhecido");
    }
}
```

---

## 🔧 PARTE 4: CLIENTE API

### 4.1 ApiClient.java
**Localização**: `/Neon/Login/app/src/main/java/com/example/esclogin/api/ApiClient.java`

```java
package com.example.esclogin.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    // ⚠️ Substituir pelo IP do seu computador na rede local
    private static String BASE_URL = "http://192.168.1.100:8080";
    
    private static Retrofit retrofit = null;
    
    public static Retrofit getClient() {
        if (retrofit == null) {
            // Logging para debug
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // Cliente HTTP com timeout
            OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
            
            retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        return retrofit;
    }
    
    // Método para atualizar BASE_URL (se necessário)
    public static void updateBaseUrl(String newBaseUrl) {
        BASE_URL = newBaseUrl;
        retrofit = null; // Força recriação
    }
}
```

### 4.2 AuthService.java
**Localização**: `/Neon/Login/app/src/main/java/com/example/esclogin/api/AuthService.java`

```java
package com.example.esclogin.api;

import com.example.esclogin.models.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface AuthService {
    
    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);
    
    @POST("auth/login") 
    Call<LoginResponse> login(@Body LoginRequest request);
    
    @GET("health")
    Call<Void> healthCheck();
}
```

### 4.3 ApiUtils.java
**Localização**: `/Neon/Login/app/src/main/java/com/example/esclogin/api/ApiUtils.java`

```java
package com.example.esclogin.api;

import com.example.esclogin.models.ApiError;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;

public class ApiUtils {
    
    public interface ApiCallback<T> {
        void onSuccess(T response);
        void onError(String errorMessage);
    }
    
    public static <T> void enqueueCall(Call<T> call, ApiCallback<T> callback) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = getErrorMessage(response);
                    callback.onError(errorMessage);
                }
            }
            
            @Override
            public void onFailure(Call<T> call, Throwable t) {
                String errorMessage = "Erro de conexão: " + t.getMessage();
                callback.onError(errorMessage);
            }
        });
    }
    
    private static String getErrorMessage(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                // Tenta parsear como ApiError
                try {
                    ApiError apiError = new com.google.gson.Gson()
                        .fromJson(errorBody, ApiError.class);
                    return apiError.toString();
                } catch (Exception e) {
                    return "Erro " + response.code() + ": " + errorBody;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Mensagens padrão baseadas no código HTTP
        switch (response.code()) {
            case 400: return "Requisição inválida";
            case 401: return "Não autorizado";
            case 403: return "Acesso negado";
            case 404: return "Recurso não encontrado";
            case 500: return "Erro interno do servidor";
            default: return "Erro " + response.code();
        }
    }
    
    // Método para testar conexão
    public static void testConnection(ApiCallback<Void> callback) {
        AuthService authService = ApiClient.getClient().create(AuthService.class);
        enqueueCall(authService.healthCheck(), callback);
    }
}
```

---

## 🔧 PARTE 5: INTEGRAÇÃO FINAL

### 5.1 RegisterActivity.java (VERSÃO COMPLETA)
**Localização**: `/Neon/Login/app/src/main/java/com/example/esclogin/RegisterActivity.java`

```java
package com.example.esclogin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.esclogin.api.ApiClient;
import com.example.esclogin.api.ApiUtils;
import com.example.esclogin.api.AuthService;
import com.example.esclogin.models.RegisterRequest;
import com.example.esclogin.models.RegisterResponse;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etBirthDate, etCPF, etPhone, etEmail, etPassword, etConfirmPassword, etWeight, etHeight, etComplaints;
    private CheckBox cbTerms, cbData;
    private Button btnRegisterSubmit;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializando os campos do formulário
        etFullName = findViewById(R.id.etFullName);
        etBirthDate = findViewById(R.id.etBirthDate);
        etCPF = findViewById(R.id.etCPF);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etComplaints = findViewById(R.id.etComplaints);
        
        cbTerms = findViewById(R.id.cbTerms);
        cbData = findViewById(R.id.cbData);
        
        btnRegisterSubmit = findViewById(R.id.btnRegisterSubmit);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // Botão para voltar ao Login
        tvBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Fecha a tela de cadastro e volta para o Login
            }
        });

        // ✅ Botão de cadastro com integração real
        btnRegisterSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    registerUser();
                }
            }
        });
    }

    private boolean validateForm() {
        if (etFullName.getText().toString().isEmpty() || etEmail.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "Preencha os campos obrigatórios (Nome, Email e Senha)", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Você precisa aceitar os termos de uso", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        String pass = etPassword.getText().toString();
        String confirmPass = etConfirmPassword.getText().toString();
        if (!pass.equals(confirmPass)) {
            Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }

    // ✅ MÉTODO DE CADASTRO REAL
    private void registerUser() {
        // Desabilitar botão para evitar cliques múltiplos
        btnRegisterSubmit.setEnabled(false);
        btnRegisterSubmit.setText("Cadastrando...");

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Criar requisição
        RegisterRequest request = new RegisterRequest(email, password);

        // Obter serviço da API
        AuthService authService = ApiClient.getClient().create(AuthService.class);

        // Fazer chamada assíncrona
        ApiUtils.enqueueCall(authService.register(request), new ApiUtils.ApiCallback<RegisterResponse>() {
            @Override
            public void onSuccess(RegisterResponse response) {
                runOnUiThread(() -> {
                    // Reabilitar botão
                    btnRegisterSubmit.setEnabled(true);
                    btnRegisterSubmit.setText("Cadastrar");

                    // Sucesso!
                    Toast.makeText(RegisterActivity.this, 
                        "Cadastro realizado! ID: " + response.getUserId(), 
                        Toast.LENGTH_LONG).show();
                    
                    // ✅ Navegar para tela principal ou login
                    finish();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    // Reabilitar botão
                    btnRegisterSubmit.setEnabled(true);
                    btnRegisterSubmit.setText("Cadastrar");

                    // Mostrar erro
                    Toast.makeText(RegisterActivity.this, 
                        "Erro no cadastro: " + errorMessage, 
                        Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    // 🧪 MÉTODO PARA TESTAR CONEXÃO (opcional)
    private void testConnection() {
        ApiUtils.testConnection(new ApiUtils.ApiCallback<Void>() {
            @Override
            public void onSuccess(Void response) {
                runOnUiThread(() -> 
                    Toast.makeText(RegisterActivity.this, "Conexão com backend OK!", Toast.LENGTH_SHORT).show());
            }
            
            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> 
                    Toast.makeText(RegisterActivity.this, "Erro de conexão: " + errorMessage, Toast.LENGTH_LONG).show());
            }
        });
    }
}
```

---

## 🚀 PASSO A PASSO PARA IMPLEMENTAR

### Passo 1: Preparar Backend
```bash
# Parar serviços
docker-compose down -v

# Iniciar com schema corrigido
docker-compose up --build

# Verificar se está funcionando
curl http://localhost:8080/health
```

### Passo 2: Descobrir IP da Rede
```bash
# Windows
ipconfig

# Linux/Mac  
ifconfig ou ip a
```

### Passo 3: Configurar Android
1. **Adicionar dependências** (seção 2.1)
2. **Adicionar permissões** (seção 2.2)
3. **Criar classes modelo** (seção 3)
4. **Criar cliente API** (seção 4)
5. **Atualizar BASE_URL** no ApiClient.java com seu IP
6. **Substituir RegisterActivity** (seção 5.1)

### Passo 4: Compilar e Testar
1. Build > Make Project no Android Studio
2. Preencher formulário de cadastro
3. Aceitar termos
4. Clicar em "Cadastrar"
5. Observar Toast de sucesso/erro

---

## 🧪 TROUBLESHOOTING

### Erro: "Connection refused"
- Verifique se `docker-compose up` está rodando
- Confirme o IP no `ApiClient.java`
- Teste: `http://SEU_IP:8080/health`

### Erro: 400 Bad Request
- Verifique se todos campos obrigatórios estão preenchidos
- Confirme formato do email

### Erro: 500 Internal Server Error
- Verifique logs do Docker
- Confirme schema do banco está correto

### Erro: Timeout
- Verifique conexão de rede
- Confirme IP está correto

---

## 🎉 RESULTADO ESPERADO

Ao clicar em "Cadastrar":
1. ✅ App envia requisição para `http://SEU_IP:8080/auth/register`
2. ✅ Backend cria usuário no banco MySQL
3. ✅ Backend retorna ID do usuário
4. ✅ App mostra mensagem de sucesso
5. ✅ App navega para tela principal

**O botão "Criar Novo Usuário" estará 100% funcional!** 🚀
