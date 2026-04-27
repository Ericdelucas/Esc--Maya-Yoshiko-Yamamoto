package com.example.testbackend.utils;

import android.util.Log;

public class Constants {
    
    private static final String TAG = "NETWORK_AUDIT";
    
    /**
     *  CONFIGURAÇÃO DE HOST:
     *  Render.com - Produção (HTTPS padrão, sem porta específica)
     */
    // 🔥 URLs do Render.com - Produção
    public static final String AUTH_BASE_URL = "https://esc-maya-yoshiko-yamamoto.onrender.com/";
    public static final String PACIENTES_BASE_URL = "https://esc-maya-yoshiko-yamamoto.onrender.com/";
    public static final String EXERCISE_BASE_URL = "https://esc-maya-yoshiko-yamamoto.onrender.com/";
    public static final String HEALTH_BASE_URL = "https://esc-maya-yoshiko-yamamoto.onrender.com/";
    public static final String TRAINING_BASE_URL = "https://esc-maya-yoshiko-yamamoto.onrender.com/";
    public static final String AI_HTTP_URL = "https://esc-maya-yoshiko-yamamoto.onrender.com/ai/process-frame";

    static {
        Log.d(TAG, "🌐 >>> AUDITORIA DE REDE ATIVA <<<");
        Log.d(TAG, "🌐 URL AUTH: " + AUTH_BASE_URL);
        Log.d(TAG, "🌐 Render.com - Produção");
    }
}
