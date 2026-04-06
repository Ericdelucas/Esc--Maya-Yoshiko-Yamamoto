package com.example.testbackend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

        btnPT.setOnClickListener(v -> restartAppWithLocale("pt"));
        btnEN.setOnClickListener(v -> restartAppWithLocale("en"));
    }

    private void restartAppWithLocale(String lang) {
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