# Guia 03: Implementar Cliente API

## 🎯 Objetivo
Criar cliente HTTP para comunicação com o backend auth-service.

## 📁 Arquivos a Criar

### 1. ApiClient.java
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
    private static final String BASE_URL = "http://192.168.1.100:8080";
    
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

### 2. AuthService.java
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

### 3: ApiUtils.java
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
                    ApiError apiError = ApiClient.getClient()
                        .create(com.google.gson.Gson.class)
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

## 📋 Estrutura de Pastas

```
app/src/main/java/com/example/esclogin/
├── api/
│   ├── ApiClient.java
│   ├── AuthService.java
│   └── ApiUtils.java
├── models/
│   └── [classes do guia anterior]
└── RegisterActivity.java
```

## ⚠️ Configuração Importante

### Descobrir IP do Computador

1. **Windows**: Abrir CMD e digitar `ipconfig`
2. **Linux/Mac**: Abrir terminal e digitar `ifconfig` ou `ip a`
3. Procurar pelo endereço IPv4 (ex: 192.168.1.100)

### Atualizar BASE_URL

No `ApiClient.java`, substituir:
```java
private static final String BASE_URL = "http://SEU_IP_AQUI:8080";
```

## 🧪 Teste de Conexão

Para testar se a conexão está funcionando, adicione este método no RegisterActivity:

```java
private void testApiConnection() {
    ApiUtils.testConnection(new ApiUtils.ApiCallback<Void>() {
        @Override
        public void onSuccess(Void response) {
            runOnUiThread(() -> 
                Toast.makeText(RegisterActivity.this, "Conexão OK!", Toast.LENGTH_SHORT).show());
        }
        
        @Override
        public void onError(String errorMessage) {
            runOnUiThread(() -> 
                Toast.makeText(RegisterActivity.this, "Erro: " + errorMessage, Toast.LENGTH_LONG).show());
        }
    });
}
```

## 🔄 Próximos Passos

1. ✅ Dependências HTTP (guia 01)
2. ✅ Classes de modelo (guia 02)  
3. ✅ Cliente API (este guia)
4. 🔄 Integrar no RegisterActivity (próximo guia)

## 🚨 Troubleshooting

- **Connection refused**: Verifique se o backend está rodando
- **Timeout**: Verifique o IP e se estão na mesma rede
- **SSL errors**: Use HTTP (não HTTPS) para desenvolvimento local
