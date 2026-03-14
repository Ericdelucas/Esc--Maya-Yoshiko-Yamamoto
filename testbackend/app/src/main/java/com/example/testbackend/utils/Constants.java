package com.example.testbackend.utils;

public class Constants {
    // Altere para "localhost" se estiver testando localmente via adb reverse ou hardware real no mesmo host
    // Caso use o emulador padrão, o endereço costuma ser "10.0.2.2"
    public static final String HOST = "localhost";

    // Portas alinhadas com o backend
    public static final String AUTH_BASE_URL = "http://" + HOST + ":8080/";
    public static final String EXERCISE_BASE_URL = "http://" + HOST + ":8081/";
    public static final String TRAINING_BASE_URL = "http://" + HOST + ":8030/";
    public static final String ANALYTICS_BASE_URL = "http://" + HOST + ":8050/";
    public static final String EHR_BASE_URL = "http://" + HOST + ":8060/";
    public static final String NOTIFICATION_BASE_URL = "http://" + HOST + ":8070/";
    
    // AI Service Endpoints
    public static final String AI_HTTP_URL = "http://" + HOST + ":8090/ai/process-frame";
    public static final String AI_WS_URL = "ws://" + HOST + ":8090/ai/pose-stream";
}
