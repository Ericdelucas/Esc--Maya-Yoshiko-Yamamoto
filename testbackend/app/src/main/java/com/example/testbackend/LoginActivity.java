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
import com.example.testbackend.network.ApiErrorHandler;
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
                Log.e(TAG, "Erro: Componentes do layout não encontrados. Verifique activity_login.xml");
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
            Log.e(TAG, "Erro fatal no onCreate da LoginActivity", e);
            Toast.makeText(this, "Erro ao iniciar aplicativo", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateLoginForm() {
        if (etEmail == null || etPassword == null) return false;
        
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

        authApi.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String token = loginResponse.getToken();
                    String role = loginResponse.getUserRole();
                    
                    if (token != null && !token.isEmpty()) {
                        tokenManager.saveSession(token, role, email);
                        navigateToMain();
                    } else {
                        Toast.makeText(LoginActivity.this, "Erro: Token não recebido", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciais inválidas", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                setLoading(false);
                Log.e(TAG, "Falha na conexão", t);
                Toast.makeText(LoginActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToMain() {
        String userRole = tokenManager.getUserRole();
        Class<?> targetActivity;
        
        // Lógica de navegação segura
        if (userRole != null && (userRole.equalsIgnoreCase("professional") || userRole.equalsIgnoreCase("doctor") || userRole.equalsIgnoreCase("admin"))) {
            targetActivity = ProfessionalMainActivity.class;
        } else {
            targetActivity = MainActivity.class;
        }
        
        Intent intent = new Intent(this, targetActivity);
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
