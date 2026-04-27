package com.example.testbackend;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestConnectionActivity extends AppCompatActivity {
    
    private static final String TAG = "CONNECTION_TEST";
    private TextView tvResult;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Layout simples via código
        tvResult = new TextView(this);
        tvResult.setText("Aguardando teste...");
        tvResult.setTextSize(16);
        tvResult.setPadding(20, 20, 20, 20);
        
        Button btnTest = new Button(this);
        btnTest.setText("Testar Conexão");
        btnTest.setOnClickListener(v -> testConnection());
        
        // Adicionar ao layout
        androidx.appcompat.widget.LinearLayoutCompat layout = new androidx.appcompat.widget.LinearLayoutCompat(this);
        layout.setOrientation(androidx.appcompat.widget.LinearLayoutCompat.VERTICAL);
        layout.addView(tvResult);
        layout.addView(btnTest);
        setContentView(layout);
    }
    
    private void testConnection() {
        new Thread(() -> {
            try {
                Log.d(TAG, "🔍 Testando conexão...");
                
                // Testar URL do Render.com
                String[] urls = {
                    "https://esc-maya-yoshiko-yamamoto.onrender.com/health"
                };
                
                final StringBuilder fullResults = new StringBuilder();
                
                for (String url : urls) {
                    try {
                        Log.d(TAG, "🌐 Testando: " + url);
                        String result = makeHttpRequest(url);
                        Log.d(TAG, "✅ Sucesso: " + url + " → " + result);
                        
                        fullResults.append("✅ SUCESSO: ").append(url).append("\n").append(result).append("\n\n");
                        
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Falha: " + url + " → " + e.getMessage());
                        fullResults.append("❌ FALHA: ").append(url).append("\n").append(e.getMessage()).append("\n\n");
                    }
                }
                
                runOnUiThread(() -> {
                    tvResult.setText(fullResults.toString());
                });
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Erro geral: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    tvResult.setText("❌ ERRO GERAL: " + e.getMessage());
                });
            }
        }).start();
    }
    
    private String makeHttpRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return "Response: " + response.toString();
        } else {
            throw new Exception("HTTP " + responseCode);
        }
    }
}
