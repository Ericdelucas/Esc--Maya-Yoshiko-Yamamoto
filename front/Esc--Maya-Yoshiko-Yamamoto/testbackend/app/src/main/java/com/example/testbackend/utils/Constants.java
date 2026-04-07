package com.example.testbackend.utils;

import android.util.Log;

public class Constants {
    
    private static final String TAG = "NETWORK_AUDIT";
    
    // 🔥 FORÇANDO 127.0.0.1 (Requer adb reverse tcp:8080 tcp:8080)
    public static final String HOST = "127.0.0.1";
    
    public static final String AUTH_BASE_URL = "http://" + HOST + ":8080/";
    public static final String PACIENTES_BASE_URL = "http://" + HOST + ":8080/";
    
    public static final String EXERCISE_BASE_URL = "http://" + HOST + ":8081/";
    public static final String HEALTH_BASE_URL = "http://" + HOST + ":8071/";
    public static final String TRAINING_BASE_URL = "http://" + HOST + ":8030/";
    public static final String AI_HTTP_URL = "http://" + HOST + ":8090/ai/process-frame";

    static {
        Log.d(TAG, "🌐 >>> AUDITORIA DE REDE ATIVA <<<");
        Log.d(TAG, "🌐 HOST CONFIGURADO: " + HOST);
        Log.d(TAG, "🌐 URL DE AUTENTICAÇÃO: " + AUTH_BASE_URL);
        Log.d(TAG, "🌐 IMPORTANTE: Execute 'adb reverse tcp:8080 tcp:8080' no seu terminal!");
    }
}
