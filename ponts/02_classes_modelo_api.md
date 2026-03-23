# Guia 02: Criar Classes de Modelo API

## 🎯 Objetivo
Criar classes Java para representar as requisições e respostas da API do auth-service.

## 📁 Arquivos a Criar

### 1. RegisterRequest.java
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

### 2. RegisterResponse.java
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

### 3. LoginRequest.java
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

### 4. LoginResponse.java
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

### 5. ApiError.java
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

## 📋 Estrutura de Pastas

```
app/src/main/java/com/example/esclogin/
├── models/
│   ├── RegisterRequest.java
│   ├── RegisterResponse.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   └── ApiError.java
└── RegisterActivity.java
```

## 🔄 Próximos Passos

1. ✅ Dependências HTTP (guia anterior)
2. ✅ Classes de modelo (este guia)
3. 🔄 Cliente API (próximo guia)
4. 🔄 Integração no RegisterActivity

## 🧪 Teste

Compile o projeto para garantir que todas as classes foram criadas sem erros.
