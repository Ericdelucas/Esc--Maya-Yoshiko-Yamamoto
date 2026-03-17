package com.example.testbackend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.testbackend.utils.LocaleHelper;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "PROFILE_DEBUG";
    private TextView tvProfileInitial, tvProfileName, tvProfileEmail, tvProfileRole;
    private MaterialButton btnChangePhoto, btnChangePassword, btnProfileLogout;

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    onImageSelected(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "### PROFILE_ACTIVITY: onCreate executado com sucesso! ###");
        setContentView(R.layout.activity_profile);

        setupToolbar();
        initViews();
        loadUserData();
        setupListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        tvProfileInitial = findViewById(R.id.tvProfileInitial);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileRole = findViewById(R.id.tvProfileRole);
        
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnProfileLogout = findViewById(R.id.btnProfileLogout);
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
        String email = prefs.getString("user_email", "usuario@email.com");
        String role = prefs.getString("user_role", "Patient");

        Log.d(TAG, "Dados carregados: Email=[" + email + "], Role=[" + role + "]");

        tvProfileEmail.setText(email);
        tvProfileRole.setText(role.toUpperCase());
        
        String name = email.split("@")[0];
        tvProfileName.setText(name.substring(0, 1).toUpperCase() + name.substring(1));

        if (!email.isEmpty()) {
            tvProfileInitial.setText(email.substring(0, 1).toUpperCase());
        }
    }

    private void setupListeners() {
        btnChangePhoto.setOnClickListener(v -> {
            Log.d(TAG, "Clique detectado: ALTERAR FOTO");
            Toast.makeText(this, "ABRINDO GALERIA REAL...", Toast.LENGTH_SHORT).show();
            galleryLauncher.launch("image/*");
        });

        btnChangePassword.setOnClickListener(v -> {
            Log.d(TAG, "Clique detectado: ALTERAR SENHA");
            Toast.makeText(this, "ABRINDO TELA DE SENHA REAL...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        btnProfileLogout.setOnClickListener(v -> logout());
    }

    private void onImageSelected(Uri uri) {
        Log.d(TAG, "Imagem selecionada: " + uri.toString());
        Toast.makeText(this, "FOTO SELECIONADA COM SUCESSO!", Toast.LENGTH_LONG).show();
    }

    private void logout() {
        Log.d(TAG, "Executando LOGOUT...");
        SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}