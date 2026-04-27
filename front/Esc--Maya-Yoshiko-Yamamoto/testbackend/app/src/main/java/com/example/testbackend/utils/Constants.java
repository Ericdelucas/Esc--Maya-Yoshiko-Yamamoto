package com.example.testbackend.utils;

import android.util.Log;

public class Constants {
    
    private static final String TAG = "NETWORK_AUDIT";
    
    /**
     *  CONFIGURAÇÃO DE HOST:
     *  127.0.0.1 -> Use se estiver com 'adb reverse tcp:8080 tcp:8080' (Recomendado)
     *  10.0.2.2  -> Use para acessar o localhost do PC diretamente do Emulador Android
     */
    public static final String HOST = "127.0.0.1"; 
    // public static final String HOST = "10.0.2.2"; 
    
    public static final String AUTH_BASE_URL = "http://" + HOST + ":8080/";
    public static final String PACIENTES_BASE_URL = "http://" + HOST + ":8080/";
    
    public static final String EXERCISE_BASE_URL = "http://" + HOST + ":8081/";
    public static final String HEALTH_BASE_URL = "http://" + HOST + ":8080/";
    public static final String TRAINING_BASE_URL = "http://" + HOST + ":8030/";
    public static final String AI_HTTP_URL = "http://" + HOST + ":8090/ai/process-frame";

    static {
        Log.d(TAG, "🌐 >>> AUDITORIA DE REDE ATIVA <<<");
        Log.d(TAG, "🌐 HOST ATUAL: " + HOST);
        Log.d(TAG, "🌐 URL AUTH: " + AUTH_BASE_URL);
        Log.d(TAG, "🌐 DICA: Se o erro persistir, verifique se o comando 'adb reverse tcp:8080 tcp:8080' foi executado.");
    }
}
