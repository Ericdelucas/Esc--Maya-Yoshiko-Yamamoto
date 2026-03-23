# Guia 04: Integrar API no RegisterActivity

## 🎯 Objetivo
Substituir o placeholder do botão de cadastro pela chamada real à API do backend.

## 📁 Arquivo a Modificar
`/Neon/Login/app/src/main/java/com/example/esclogin/RegisterActivity.java`

## 🔧 Integração Completa

### Versão Final do RegisterActivity.java

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
                    // Por enquanto, volta para o login
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

    @Override
    protected void onResume() {
        super.onResume();
        // Opcional: testar conexão ao abrir a tela
        // testConnection();
    }
}
```

## 📋 Mudanças Principais

### 1. ✅ Importações Novas
```java
import com.example.esclogin.api.ApiClient;
import com.example.esclogin.api.ApiUtils; 
import com.example.esclogin.api.AuthService;
import com.example.esclogin.models.RegisterRequest;
import com.example.esclogin.models.RegisterResponse;
```

### 2. ✅ Botão de Cadastro Real
**Antes** (placeholder):
```java
Toast.makeText(RegisterActivity.this, "Enviando dados para o SmartSaúde...", Toast.LENGTH_SHORT).show();
```

**Depois** (chamada real):
```java
registerUser();
```

### 3. ✅ Método registerUser()
- Cria `RegisterRequest` com email e senha
- Usa `ApiUtils.enqueueCall()` para chamada assíncrona
- Trata sucesso e erro
- Desabilita botão durante requisição
- Mostra feedback ao usuário

## 🧪 Teste Passo a Passo

### 1. Compilar o Projeto
```bash
# No Android Studio: Build > Make Project
```

### 2. Verificar Conexão
- Garanta que o backend está rodando
- Verifique o IP no `ApiClient.java`
- Teste com o método `testConnection()` se necessário

### 3. Testar Cadastro
1. Preencha o formulário
2. Aceite os termos
3. Clique em "Cadastrar"
4. Observe o Toast de sucesso/erro

## 🚨 Troubleshooting

### Erro "Connection refused"
- Verifique se `docker-compose up` está rodando
- Confirme o IP no `ApiClient.java`
- Teste acesso: `http://SEU_IP:8080/health`

### Erro 400/500
- Verifique os logs do backend
- Confirme que o schema do banco está correto
- Valide se os campos obrigatórios estão preenchidos

## 🔄 Próximos Passos

1. ✅ Backend corrigido (schema do banco)
2. ✅ Dependências HTTP
3. ✅ Classes de modelo  
4. ✅ Cliente API
5. ✅ Integração no RegisterActivity (este guia)

## 🎉 Resultado Esperado

Ao clicar em "Cadastrar", o app deve:
1. Enviar requisição para `http://SEU_IP:8080/auth/register`
2. Criar usuário no banco MySQL
3. Retornar ID do usuário
4. Mostrar mensagem de sucesso
5. Navegar para próxima tela
