package com.example.testbackend;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testbackend.models.RegisterRequest;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.AuthApi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private Spinner spinnerRole;
    private CheckBox cbLgpd;
    private Button btnRegister, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupRoleSpinner();
        setupListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.etRegName);
        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        etConfirmPassword = findViewById(R.id.etRegConfirmPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        cbLgpd = findViewById(R.id.cbLgpd);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBackToLogin);
    }

    private void setupRoleSpinner() {
        String[] roles = {"patient", "professional", "admin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> {
            if (validateForm()) {
                performRegister();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private boolean validateForm() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Nome é obrigatório");
            return false;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("E-mail inválido");
            return false;
        }

        // ✅ Validação crucial: Mínimo 6 caracteres (Exigência do Backend)
        if (password.length() < 6) {
            etPassword.setError("A senha deve ter pelo menos 6 caracteres");
            Toast.makeText(this, "Senha muito curta! Mínimo 6 caracteres.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("As senhas não coincidem");
            return false;
        }

        if (!cbLgpd.isChecked()) {
            Toast.makeText(this, "Você deve aceitar os termos da LGPD", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void performRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();

        btnRegister.setEnabled(false);
        btnRegister.setText("Cadastrando...");

        AuthApi authApi = ApiClient.getAuthClient().create(AuthApi.class);
        RegisterRequest registerRequest = new RegisterRequest(name, email, password, role);

        authApi.register(registerRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Criar conta");
                
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Cadastro realizado! Faça login.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Log.e("RegisterActivity", "Erro: " + response.code());
                    if (response.code() == 422) {
                        Toast.makeText(RegisterActivity.this, "Dados inválidos. Verifique a senha (mín. 6 caracteres).", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Erro no cadastro: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Criar conta");
                Log.e("RegisterActivity", "Erro de rede", t);
                Toast.makeText(RegisterActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}