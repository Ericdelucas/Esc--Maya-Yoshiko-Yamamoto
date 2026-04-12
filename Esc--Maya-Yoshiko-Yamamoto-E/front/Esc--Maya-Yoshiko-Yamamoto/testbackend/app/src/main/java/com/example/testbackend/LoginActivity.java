package com.example.testbackend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testbackend.models.LoginRequest;
import com.example.testbackend.models.LoginResponse;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.AuthApi;
import com.example.testbackend.utils.Constants;
import com.example.testbackend.utils.TokenManager;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LOGIN_DEBUG";
    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoToRegister;
    private ProgressBar loadingIndicator;
    private TokenManager tokenManager;
    private LoginResponse loginResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_login);
            tokenManager = new TokenManager(this);

            etEmail = findViewById(R.id.etEmail);
            etPassword = findViewById(R.id.etPassword);
            btnLogin = findViewById(R.id.btnLogin);
            btnGoToRegister = findViewById(R.id.btnGoToRegister);
            loadingIndicator = findViewById(R.id.loadingIndicator);

            if (btnLogin == null) {
                Log.e(TAG, "Erro: Componentes do layout não encontrados.");
                return;
            }

            btnLogin.setOnClickListener(v -> {
                if (validateLoginForm()) {
                    performLogin();
                }
            });

            btnGoToRegister.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Erro fatal no onCreate", e);
        }
    }

    private boolean validateLoginForm() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("E-mail é obrigatório");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("E-mail inválido");
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Senha é obrigatória");
            return false;
        }

        return true;
    }

    private void performLogin() {
        setLoading(true);
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        Log.d(TAG, "🔐 Tentando login com: " + email);
        Log.d(TAG, "🌐 URL Base: " + Constants.AUTH_BASE_URL);

        AuthApi authApi = ApiClient.getAuthClient().create(AuthApi.class);
        LoginRequest loginRequest = new LoginRequest(email, password);

        authApi.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                setLoading(false);
                Log.d(TAG, "📡 Resposta Recebida - Code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse responseBody = response.body();
                    String token = responseBody.getToken();
                    String role = responseBody.getUserRole();
                    
                    Log.d(TAG, "✅ Sucesso! Token recebido, Role: '" + role + "'");

                    if (token != null && !token.isEmpty()) {
                        tokenManager.saveSession(token, role, email);
                        loginResponse = responseBody;
                        navigateToCorrectActivity();
                    } else {
                        Log.e(TAG, "❌ Erro: Token veio vazio do backend");
                        Toast.makeText(LoginActivity.this, "Erro: Token vazio", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 🔥 ERRO HTTP DETALHADO
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Erro ao ler corpo do erro", e);
                    }
                    
                    Log.e(TAG, "❌ Login falhou - Code: " + response.code());
                    Log.e(TAG, "❌ Login falhou - Body: " + errorBody);
                    
                    String message = "Credenciais inválidas (" + response.code() + ")";
                    if (response.code() == 401) message = "E-mail ou senha incorretos";
                    else if (response.code() == 404) message = "Serviço de autenticação não encontrado";
                    else if (response.code() == 500) message = "Erro interno no servidor";
                    
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                setLoading(false);
                
                // 🔥 ERRO DE CONEXÃO DETALHADO
                String errorType = t.getClass().getSimpleName();
                String errorMessage = t.getMessage();
                String requestUrl = call.request().url().toString();
                
                Log.e(TAG, "❌ FALHA DE REDE DETALHADA:");
                Log.e(TAG, "   Tipo: " + errorType);
                Log.e(TAG, "   Mensagem: " + errorMessage);
                Log.e(TAG, "   URL tentada: " + requestUrl);
                
                String userMessage = "Erro de conexão";
                if (t instanceof SocketTimeoutException) {
                    userMessage = "O servidor demorou muito para responder (Timeout)";
                } else if (t instanceof ConnectException) {
                    userMessage = "Não foi possível conectar ao servidor. Verifique se o Docker está rodando.";
                } else if (t instanceof UnknownHostException) {
                    userMessage = "Endereço do servidor não encontrado. Verifique o Constants.HOST.";
                }
                
                Toast.makeText(LoginActivity.this, userMessage + "\n(" + errorType + ")", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToCorrectActivity() {
        if (loginResponse == null) {
            Log.e(TAG, "loginResponse nulo. Usando fallback.");
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        String targetActivity = loginResponse.getTargetActivity();
        Class<?> activityClass;
        
        if ("ProfessionalMainActivity".equals(targetActivity)) {
            activityClass = ProfessionalMainActivity.class;
        } else {
            activityClass = MainActivity.class;
        }
        
        Log.d(TAG, "➡️ Navegando para: " + activityClass.getSimpleName());
        
        Intent intent = new Intent(this, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean isLoading) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        if (btnLogin != null) btnLogin.setEnabled(!isLoading);
    }
}
