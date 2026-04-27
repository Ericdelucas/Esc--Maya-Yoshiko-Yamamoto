package com.example.testbackend.utils;

import android.util.Log;

public class Constants {
    
    private static final String TAG = "NETWORK_AUDIT";
    
    /**
     *  CONFIGURAÇÃO DE HOST:
     *  127.0.0.1 -> Use se estiver com 'adb reverse tcp:8080 tcp:8080' (Recomendado)
     *  10.0.2.2  -> Use para acessar o localhost do PC diretamente do Emulador Android
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
