package com.example.esclogin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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

        // Botão de submeter o cadastro
        btnRegisterSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    // Placeholder para chamada ao auth-service (Porta 8080) / register
                    Toast.makeText(RegisterActivity.this, "Enviando dados para o SmartSaúde...", Toast.LENGTH_SHORT).show();
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
}