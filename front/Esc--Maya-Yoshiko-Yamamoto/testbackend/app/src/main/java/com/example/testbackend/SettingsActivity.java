package com.example.testbackend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.example.testbackend.utils.LocaleHelper;
import com.google.android.material.button.MaterialButton;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("settings", MODE_PRIVATE);

        setupToolbar();
        setupThemeSelection();
        setupLanguageSelection();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupThemeSelection() {
        RadioGroup rgTheme = findViewById(R.id.rgTheme);
        int currentTheme = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        if (currentTheme == AppCompatDelegate.MODE_NIGHT_NO) {
            rgTheme.check(R.id.rbLight);
        } else if (currentTheme == AppCompatDelegate.MODE_NIGHT_YES) {
            rgTheme.check(R.id.rbDark);
        } else {
            rgTheme.check(R.id.rbSystem);
        }

        rgTheme.setOnCheckedChangeListener((group, checkedId) -> {
            int mode;
            if (checkedId == R.id.rbLight) {
                mode = AppCompatDelegate.MODE_NIGHT_NO;
            } else if (checkedId == R.id.rbDark) {
                mode = AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            }
            AppCompatDelegate.setDefaultNightMode(mode);
            prefs.edit().putInt("theme_mode", mode).apply();
        });
    }

    private void setupLanguageSelection() {
        MaterialButton btnPT = findViewById(R.id.btnPortuguese);
        MaterialButton btnEN = findViewById(R.id.btnEnglish);

        // Obter idioma atual salvo
        String currentLang = LocaleHelper.getCurrentLanguage(this);
        
        // Atualizar visualização dos botões
        updateLanguageButtons(btnPT, btnEN, currentLang);

        btnPT.setOnClickListener(v -> {
            if (!"pt".equals(currentLang)) {
                restartAppWithLocale("pt");
            }
        });
        
        btnEN.setOnClickListener(v -> {
            if (!"en".equals(currentLang)) {
                restartAppWithLocale("en");
            }
        });
    }
    
    private void updateLanguageButtons(MaterialButton btnPT, MaterialButton btnEN, String currentLang) {
        // Resetar estilos
        btnPT.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        btnEN.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        
        // Destacar idioma atual
        if ("pt".equals(currentLang)) {
            btnPT.setBackgroundColor(getResources().getColor(com.google.android.material.R.color.design_default_color_primary));
            btnPT.setTextColor(getResources().getColor(android.R.color.white));
        } else if ("en".equals(currentLang)) {
            btnEN.setBackgroundColor(getResources().getColor(com.google.android.material.R.color.design_default_color_primary));
            btnEN.setTextColor(getResources().getColor(android.R.color.white));
        }
    }

    private void restartAppWithLocale(String lang) {
        Log.d("SettingsActivity", "Restarting app with locale: " + lang);
        
        // Primeiro aplica o locale
        LocaleHelper.setLocale(this, lang);
        
        // Reinicia a stack do app para garantir que strings.xml seja recarregado globalmente
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}