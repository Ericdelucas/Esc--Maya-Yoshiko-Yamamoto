package com.example.testbackend;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

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
        
        ScrollView scrollView = new ScrollView(this);
        LinearLayoutCompat layout = new LinearLayoutCompat(this);
        layout.setOrientation(LinearLayoutCompat.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        tvResult = new TextView(this);
        tvResult.setText("Aguardando teste...");
        tvResult.setTextSize(14);
        
        Button btnTest = new Button(this);
        btnTest.setText("Testar Conexão (Tudo)");
        btnTest.setOnClickListener(v -> testConnection());
        
        layout.addView(btnTest);
        layout.addView(tvResult);
        scrollView.addView(layout);
        setContentView(scrollView);
    }
    
    private void testConnection() {
        tvResult.setText("Iniciando testes...\n");
        new Thread(() -> {
            // Testar diferentes URLs
            String[] urls = {
                "http://10.1.9.88:8080/health",
                "http://10.0.2.2:8080/health",
                "http://localhost:8080/health",
                "http://127.0.0.1:8080/health"
            };
            
            for (String url : urls) {
                final String currentUrl = url;
                Log.d(TAG, "🌐 Testando: " + currentUrl);
                
                try {
                    String result = makeHttpRequest(currentUrl);
                    Log.d(TAG, "✅ Sucesso: " + currentUrl + " → " + result);
                    
                    runOnUiThread(() -> {
                        tvResult.append("\n✅ SUCESSO: " + currentUrl + "\n" + result + "\n");
                    });
                } catch (Exception e) {
                    Log.e(TAG, "❌ Falha: " + currentUrl + " → " + e.getMessage());
                    runOnUiThread(() -> {
                        tvResult.append("\n❌ FALHA: " + currentUrl + "\nErro: " + e.getMessage() + "\n");
                    });
                }
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
            return response.toString();
        } else {
            throw new Exception("HTTP " + responseCode);
        }
    }
}
