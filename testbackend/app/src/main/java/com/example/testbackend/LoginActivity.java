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
import com.example.testbackend.utils.Constants;
import com.example.testbackend.utils.TokenManager;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "API_DEBUG";
    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoToRegister;
    private ProgressBar loadingIndicator;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tokenManager = new TokenManager(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        loadingIndicator = findViewById(R.id.loadingIndicator);

        btnLogin.setOnClickListener(v -> {
            if (validateLoginForm()) {
                performLogin();
            }
        });

        btnGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private boolean validateLoginForm() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("E-mail é obrigatório");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("E-mail inválido. Use o formato: usuario@exemplo.com");
            Toast.makeText(this, "Por favor, insira um e-mail válido.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Senha é obrigatória");
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("A senha deve ter pelo menos 6 caracteres");
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
                        Toast.makeText(LoginActivity.this, "Login realizado!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Erro: Token não recebido", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(LoginActivity.this, "E-mail ou senha incorretos", Toast.LENGTH_LONG).show();
                    } else if (response.code() == 422) {
                        Toast.makeText(LoginActivity.this, "Formato de e-mail inválido pelo servidor", Toast.LENGTH_LONG).show();
                    } else {
                        String errorMsg = ApiErrorHandler.getHttpErrorMessage(response.code());
                        Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, ApiErrorHandler.getErrorMessage(t), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        btnLogin.setEnabled(!isLoading);
    }
}