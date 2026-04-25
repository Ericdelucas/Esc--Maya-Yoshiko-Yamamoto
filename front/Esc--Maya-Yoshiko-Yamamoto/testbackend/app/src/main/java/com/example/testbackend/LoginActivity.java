package com.example.testbackend;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import org.json.JSONObject;

import java.util.Objects;

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
                    String fullName = responseBody.getFullName();
                    
                    Log.d(TAG, "✅ Sucesso! Token recebido, Role: '" + role + "', Nome: " + fullName);

                    if (token != null && !token.isEmpty()) {
                        tokenManager.saveSession(token, role, email, -1, fullName != null ? fullName : "");
                        loginResponse = responseBody;
                        navigateToCorrectActivity();
                    } else {
                        Log.e(TAG, "❌ Erro: Token veio vazio do backend");
                        Toast.makeText(LoginActivity.this, "Erro: Token vazio", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "❌ Erro Body: " + errorBody);
                            
                            JSONObject errorJson = new JSONObject(errorBody);
                            
                            if (response.code() == 429) {
                                JSONObject detailJson = errorJson.optJSONObject("detail");
                                String message = detailJson != null ? detailJson.optString("message", "Muitas tentativas") : "Muitas tentativas";
                                int retryAfter = detailJson != null ? detailJson.optInt("retry_after", 300) : 300;
                                
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                                startCountdownTimer(retryAfter);
                            } else if (response.code() == 401) {
                                JSONObject detailJson = errorJson.optJSONObject("detail");
                                String message = detailJson != null ? detailJson.optString("message", "E-mail ou senha incorretos") : "E-mail ou senha incorretos";
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Erro " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao processar erro do servidor", e);
                        Toast.makeText(LoginActivity.this, "Erro ao processar resposta (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                setLoading(false);
                Log.e(TAG, "❌ FALHA DE REDE: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Erro de conexão com o servidor", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startCountdownTimer(int seconds) {
        if (btnLogin != null) btnLogin.setEnabled(false);
        new CountDownTimer(seconds * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 60000);
                int secs = (int) (millisUntilFinished % 60000) / 1000;
                if (btnLogin != null) {
                    btnLogin.setText(String.format("Bloqueado (%d:%02d)", minutes, secs));
                }
            }
            
            public void onFinish() {
                if (btnLogin != null) {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Entrar");
                }
            }
        }.start();
    }

    private void navigateToCorrectActivity() {
        if (loginResponse == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        String targetActivity = loginResponse.getTargetActivity();
        Class<?> activityClass = Objects.equals(targetActivity, "ProfessionalMainActivity") ? 
                                ProfessionalMainActivity.class : MainActivity.class;
        
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
