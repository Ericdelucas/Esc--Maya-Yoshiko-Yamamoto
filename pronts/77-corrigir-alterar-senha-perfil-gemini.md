# # **CORRIGIR ALTERAÇÃO DE SENHA NO PERFIL - PARA O GEMINI**

## # **PROBLEMA IDENTIFICADO:**
- # **Erro 422:** "String should have at least 8 characters"
- # **Causa:** Frontend enviando senhas com menos de 8 caracteres
- # **Backend exige:** Mínimo 8 caracteres para novas senhas
- # **Endpoint:** PUT /auth/change-password (requer autenticação)

## # **ANÁLISE DO BACKEND:**

### # **1. Schema de Validação (change_password_request.py):**
```python
class ChangePasswordRequest(BaseModel):
    current_password: str = Field(min_length=1, max_length=128)
    new_password: str = Field(min_length=8, max_length=128)  # # MÍNIMO 8!
    confirm_password: str = Field(min_length=8, max_length=128)  # # MÍNIMO 8!
```

### # **2. Endpoint (auth_router.py):**
```python
@router.put("/change-password")
def change_password(
    payload: ChangePasswordRequest,
    authorization: str | None = Header(default=None),  # # REQUER TOKEN!
    svc: AuthService = Depends(get_auth_service)
):
    # # Validações adicionais no backend
    if payload.new_password != payload.confirm_password:
        raise HTTPException(status_code=400, detail="New passwords do not match")
    
    if payload.current_password == payload.new_password:
        raise HTTPException(status_code=400, detail="New password must be different from current")
```

### # **3. Teste Confirmado do Erro:**
```bash
curl -X PUT http://localhost:8080/auth/change-password \
  -H "Content-Type: application/json" \
  -d '{"current_password": "senha123", "new_password": "nova123", "confirm_password": "nova123"}'
# # Resposta: 422 - "String should have at least 8 characters"
# # "nova123" só tem 7 caracteres!
```

## # **SOLUÇÕES EXIGIDAS - PASSO A PASSO:**

### # **1. ENCONTRAR ACTIVITY/FRAGMENT DE PERFIL**
```java
// # PROCURAR por arquivos que contém "password" ou "senha":

// # Possíveis arquivos:
// # - ProfileActivity.java
// # - ProfileFragment.java  
// # - SettingsActivity.java
// # - ChangePasswordActivity.java
// # - UserProfileActivity.java
```

### # **2. VERIFICAR VALIDAÇÃO ATUAL**
```java
// # PROCURAR por validações de senha como:

// # Validação incorreta (provavelmente encontrada):
if (newPassword.length() < 6) {  // # ERRADO - deveria ser 8
    editNewPassword.setError("Senha deve ter pelo menos 6 caracteres");
    return;
}

// # Input filters incorretos:
editNewPassword.setFilters(new InputFilter[]{
    new InputFilter.LengthFilter(6)  // # ERRADO - deveria ser 8
});
```

### # **3. CORRIGIR VALIDAÇÃO PARA 8 CARACTERES**
```java
// # SUBSTITUIR validações para exigir 8 caracteres:

// # Validação correta:
if (newPassword.length() < 8) {
    editNewPassword.setError("Senha deve ter pelo menos 8 caracteres");
    return;
}

if (confirmPassword.length() < 8) {
    editConfirmPassword.setError("Senha deve ter pelo menos 8 caracteres");
    return;
}

// # Input filters corretos:
editNewPassword.setFilters(new InputFilter[]{
    new InputFilter.LengthFilter(128)  // # Máximo 128 como no backend
});

editConfirmPassword.setFilters(new InputFilter[]{
    new InputFilter.LengthFilter(128)
});
```

### # **4. CORRIGIR LAYOUT XML**
```xml
<!-- # VERIFICAR EM layout do perfil (ex: activity_profile.xml): -->

<!-- # Input hints atualizados: -->
<com.google.android.material.textfield.TextInputLayout
    android:hint="Senha atual"
    app:passwordToggleEnabled="true">

    <com.google.android.material.textfield.TextInputEditText
        android:id="@+id/editCurrentPassword"
        android:inputType="textPassword"
        android:maxLength="128"/>
</com.google.android.material.textfield.TextInputLayout>

<com.google.android.material.textfield.TextInputLayout
    android:hint="Nova senha (mínimo 8 caracteres)"
    app:passwordToggleEnabled="true">

    <com.google.android.material.textfield.TextInputEditText
        android:id="@+id/editNewPassword"
        android:inputType="textPassword"
        android:maxLength="128"/>
</com.google.android.material.textfield.TextInputLayout>

<com.google.android.material.textfield.TextInputLayout
    android:hint="Confirmar nova senha"
    app:passwordToggleEnabled="true">

    <com.google.android.material.textfield.TextInputEditText
        android:id="@+id/editConfirmPassword"
        android:inputType="textPassword"
        android:maxLength="128"/>
</com.google.android.material.textfield.TextInputLayout>
```

### # **5. VERIFICAR CHAMADA DA API**
```java
// # PROCURAR por chamada de API para change-password:

// # Chamada incorreta (sem autenticação):
api.changePassword(request).enqueue(...);  // # ERRADO - não envia token

// # Chamada correta (com autenticação):
// # A API client deve incluir automaticamente o token no header
// # Verificar se ApiClient está configurado corretamente
```

### # **6. VERIFICAR API CLIENT CONFIGURAÇÃO**
```java
// # EM ApiClient.java - verificar se inclui token automaticamente:

public static Retrofit getAuthClient() {
    OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(chain -> {
            Request original = chain.request();
            // # Token deve ser adicionado automaticamente aqui
            Request request = original.newBuilder()
                .header("Authorization", "Bearer " + getToken())
                .method(original.method(), original.body())
                .build();
            return chain.proceed(request);
        })
        .build();
    
    return new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
}
```

### # **7. MODELO DE REQUEST PARA SENHA**
```java
// # VERIFICAR SE existe modelo para change password:

// # CRIAR se não existir - ChangePasswordRequest.java:
package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequest {
    @SerializedName("current_password")
    private String currentPassword;
    
    @SerializedName("new_password")
    private String newPassword;
    
    @SerializedName("confirm_password")
    private String confirmPassword;
    
    // # Getters e Setters
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
```

### # **8. INTERFACE DA API**
```java
// # VERIFICAR EM PatientReportApi ou AuthApi.java:
// # Deve ter o endpoint:

@PUT("auth/change-password")
Call<ChangePasswordResponse> changePassword(@Body ChangePasswordRequest request);

// # E se necessário, criar a resposta:
public class ChangePasswordResponse {
    private String message;
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
```

### # **9. IMPLEMENTAÇÃO COMPLETA DA ACTIVITY**
```java
// # EXEMPLO completo para ChangePasswordActivity.java:

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText editCurrentPassword;
    private EditText editNewPassword;
    private EditText editConfirmPassword;
    private Button btnSave;
    private ProgressBar progressBar;
    private AuthApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        
        api = ApiClient.getAuthClient().create(AuthApi.class);
        setupViews();
    }

    private void setupViews() {
        editCurrentPassword = findViewById(R.id.editCurrentPassword);
        editNewPassword = findViewById(R.id.editNewPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        btnSave.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String currentPassword = editCurrentPassword.getText().toString().trim();
        String newPassword = editNewPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        // # Validações CORRETAS (8 caracteres)
        if (currentPassword.isEmpty()) {
            editCurrentPassword.setError("Senha atual é obrigatória");
            return;
        }

        if (newPassword.length() < 8) {
            editNewPassword.setError("Nova senha deve ter pelo menos 8 caracteres");
            return;
        }

        if (confirmPassword.length() < 8) {
            editConfirmPassword.setError("Confirmação deve ter pelo menos 8 caracteres");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            editConfirmPassword.setError("As senhas não coincidem");
            return;
        }

        if (currentPassword.equals(newPassword)) {
            editNewPassword.setError("Nova senha deve ser diferente da atual");
            return;
        }

        // # Enviar para API
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword(currentPassword);
        request.setNewPassword(newPassword);
        request.setConfirmPassword(confirmPassword);

        progressBar.setVisibility(View.VISIBLE);

        api.changePassword(request).enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, 
                        "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMessage = "Erro ao alterar senha";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            if (errorBody.contains("8 characters")) {
                                errorMessage = "Nova senha deve ter pelo menos 8 caracteres";
                            } else if (errorBody.contains("do not match")) {
                                errorMessage = "As senhas não coincidem";
                            } else if (errorBody.contains("different from current")) {
                                errorMessage = "Nova senha deve ser diferente da atual";
                            } else {
                                errorMessage = "Erro: " + errorBody;
                            }
                        }
                    } catch (Exception e) {
                        // # Manter mensagem padrão
                    }
                    Toast.makeText(ChangePasswordActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ChangePasswordActivity.this, 
                    "Erro de conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
```

## # **TESTES PARA REALIZAR:**

### # **1. TESTE DE VALIDAÇÃO:**
```java
// # Testar senhas com menos de 8 caracteres:
// # - Deve mostrar erro específico
// # - Não deve enviar para API

// # Testar senhas com 8+ caracteres:
// # - Deve permitir envio
// # - Deve processar corretamente
```

### # **2. TESTE DE API:**
```bash
# # Testar com token válido:
curl -X PUT http://localhost:8080/auth/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -d '{"current_password": "senha123", "new_password": "nova12345", "confirm_password": "nova12345"}'
# # Deve retornar 200 OK
```

### # **3. TESTE DE INTEGRAÇÃO:**
```java
// # 1. Fazer login no app
// # 2. Navegar para perfil/alterar senha
// # 3. Digitar senhas válidas (8+ caracteres)
// # 4. Clicar em salvar
// # 5. Verificar mensagem de sucesso
// # 6. Tentar fazer login com nova senha
```

## # **VERIFICAÇÕES FINAIS:**

### # **1. VERIFICAR SE O TOKEN ESTÁ SENDO ENVIADO:**
```java
// # Adicionar logs para debug:
Log.d("ChangePassword", "Enviando requisição para API");
Log.d("ChangePassword", "Request: " + new Gson().toJson(request));

// # Verificar nos logs se o Authorization header está presente
```

### # **2. VERIFICAR SE API CLIENT ESTÁ CONFIGURADO:**
```java
// # Confirmar que getAuthClient() inclui o token
// # Testar outros endpoints que requerem autenticação
// # Se outros funcionam, o problema é só na validação
```

### # **3. VERIFICAR SE HÁ INTERCEPTOR DE TOKEN:**
```java
// # Confirmar que há interceptor adicionando o token
// # Se não houver, adicionar conforme exemplo acima
```

## # **PROVÁVEIS ARQUIVOS A MODIFICAR:**

### # **Arquivos que provavelmente existem:**
1. # **ProfileActivity.java** - Activity principal do perfil
2. # **activity_profile.xml** - Layout do perfil
3. # **ApiClient.java** - Configuração do cliente HTTP
4. # **AuthApi.java** - Interface da API de autenticação

### # **Arquivos que podem precisar ser criados:**
1. # **ChangePasswordRequest.java** - Modelo para request
2. # **ChangePasswordResponse.java** - Modelo para response
3. # **activity_change_password.xml** - Layout específico para senha

## # **RESUMO DA CORREÇÃO:**

### # **Problema Principal:**
- # **Validação frontend:** 6 caracteres (errado)
- # **Backend exige:** 8 caracteres (certo)
- # **Resultado:** Erro 422 sempre

### # **Solução:**
1. # **Atualizar validações** para 8 caracteres
2. # **Atualizar maxLength** para 128
3. # **Verificar autenticação** no API client
4. # **Implementar tratamento** de erros específicos
5. # **Testar integração** completa

---

## # **IMPORTANTE PARA O GEMINI:**

**O problema é simples: o frontend está validando senhas com menos caracteres que o backend exige. Implemente as correções acima, principalmente:**

1. # **Mudar validação de 6 para 8 caracteres**
2. # **Verificar se o token está sendo enviado na requisição**
3. # **Implementar tratamento de erros específicos do backend**
4. # **Testar com senhas de 8+ caracteres**

**Depois dessas correções, a alteração de senha deve funcionar perfeitamente!**
