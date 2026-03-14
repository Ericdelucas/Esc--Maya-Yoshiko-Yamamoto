package com.example.testbackend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "API_DEBUG";
    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoToRegister;
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        loadingIndicator = findViewById(R.id.loadingIndicator);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Logs de rastreio inicial
            Log.d(TAG, "CLICK_LOGIN email=" + email);
            Log.d(TAG, "CLICK_LOGIN password_len=" + password.length());
            Log.d(TAG, "VOU_DISPARAR_LOGIN");

            // Teste visual da URL
            Toast.makeText(this, "URL: " + Constants.AUTH_BASE_URL, Toast.LENGTH_LONG).show();
            
            performLogin(email, password);
        });

        btnGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void performLogin(String email, String password) {
        setLoading(true);

        Log.d(TAG, "CRIANDO_REQUEST_LOGIN");
        Log.d(TAG, "HOST=" + Constants.HOST);
        Log.d(TAG, "AUTH_BASE_URL=" + Constants.AUTH_BASE_URL);
        Log.d(TAG, "URL_TARGET=" + Constants.AUTH_BASE_URL + "auth/login");

        AuthApi authApi = ApiClient.getAuthClient().create(AuthApi.class);
        LoginRequest loginRequest = new LoginRequest(email, password);

        authApi.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                setLoading(false);
                Log.d(TAG, "ENTROU_onResponse code=" + response.code());
                Log.d(TAG, "RAW_CODE=" + response.code());
                Log.d(TAG, "RAW_SUCCESS=" + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Log.d(TAG, "Response Body: " + new Gson().toJson(loginResponse));
                    
                    String token = loginResponse.getToken();
                    String role = loginResponse.getUserRole();
                    
                    if (token != null && !token.isEmpty()) {
                        saveSession(token, role);
                        Toast.makeText(LoginActivity.this, "Login realizado!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Log.e(TAG, "Token nulo ou vazio na resposta");
                        Toast.makeText(LoginActivity.this, "Erro: Token não recebido", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = ApiErrorHandler.getHttpErrorMessage(response.code());
                    Log.w(TAG, "Login falhou: " + errorMsg);
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                setLoading(false);
                Log.e(TAG, "ENTROU_onFailure", t);
                Log.e(TAG, "FAIL_CLASS=" + t.getClass().getName());
                Log.e(TAG, "FAIL_MESSAGE=" + t.getMessage());
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

    private void saveSession(String token, String role) {
        SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
        prefs.edit()
            .putString("jwt_token", token)
            .putString("user_role", role)
            .apply();
    }
}