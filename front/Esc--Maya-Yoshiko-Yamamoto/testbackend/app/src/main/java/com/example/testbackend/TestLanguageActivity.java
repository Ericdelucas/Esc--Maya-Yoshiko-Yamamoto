package com.example.testbackend;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testbackend.utils.LocaleHelper;

public class TestLanguageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Criar layout simples programaticamente
        TextView tvTest = new TextView(this);
        tvTest.setText(R.string.app_name);
        tvTest.setTextSize(24);
        tvTest.setPadding(50, 50, 50, 50);
        
        Button btnPT = new Button(this);
        btnPT.setText("Português");
        btnPT.setOnClickListener(v -> {
            Log.d("TestLanguage", "Setting to Portuguese");
            LocaleHelper.setLocale(this, "pt");
            recreate();
        });
        
        Button btnEN = new Button(this);
        btnEN.setText("English");
        btnEN.setOnClickListener(v -> {
            Log.d("TestLanguage", "Setting to English");
            LocaleHelper.setLocale(this, "en");
            recreate();
        });
        
        // Layout
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.addView(tvTest);
        layout.addView(btnPT);
        layout.addView(btnEN);
        
        setContentView(layout);
        
        // Log informações atuais
        Log.d("TestLanguage", "Current locale: " + getResources().getConfiguration().locale.getLanguage());
        Log.d("TestLanguage", "App name string: " + getString(R.string.app_name));
        Log.d("TestLanguage", "Settings string: " + getString(R.string.settings));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
