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
import com.example.testbackend.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LOGIN_DEBUG";
    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoToRegister;
    private ProgressBar loadingIndicator;
    private TokenManager tokenManager;
    private LoginResponse loginResponse; // 🔥 GUARDAR RESPOSTA DO BACKEND

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

        AuthApi authApi = ApiClient.getAuthClient().create(AuthApi.class);
        LoginRequest loginRequest = new LoginRequest(email, password);

        Log.d(TAG, "Iniciando login: " + email);

        authApi.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse responseBody = response.body();
                    String token = responseBody.getToken();
                    String role = responseBody.getUserRole();
                    
                    Log.d(TAG, "Sucesso! Token recebido, Role: '" + role + "'");

                    if (token != null && !token.isEmpty()) {
                        // ✅ Salva a sessão
                        tokenManager.saveSession(token, role, email);
                        
                        // 🔥 GUARDAR RESPOSTA DO BACKEND PARA NAVEGAÇÃO
                        loginResponse = responseBody;
                        
                        // 🔥 Navegar baseado no que o BACKEND mandou
                        navigateToCorrectActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, "Erro: Token vazio", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Erro HTTP: " + response.code());
                    Toast.makeText(LoginActivity.this, "Credenciais inválidas", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                setLoading(false);
                Log.e(TAG, "Falha de rede", t);
                Toast.makeText(LoginActivity.this, "Erro de conexão", Toast.LENGTH_LONG).show();
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

        // 🔥 SIMPLIFICADO: Usa o que o backend mandou
        String targetActivity = loginResponse.getTargetActivity();
        boolean isProfessional = loginResponse.isProfessional();
        
        Log.d(TAG, "Backend mandou ir para: " + targetActivity);
        Log.d(TAG, "É profissional? " + isProfessional);
        
        Class<?> activityClass;
        
        // Backend decide, frontend só executa
        if ("ProfessionalMainActivity".equals(targetActivity)) {
            activityClass = ProfessionalMainActivity.class;
            Log.d(TAG, "🏥 PROFISSIONAL -> ProfessionalMainActivity");
        } else {
            activityClass = MainActivity.class;
            Log.d(TAG, "👤 PACIENTE -> MainActivity");
        }
        
        Log.d(TAG, "ABRINDO ACTIVITY: " + activityClass.getSimpleName());
        
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
