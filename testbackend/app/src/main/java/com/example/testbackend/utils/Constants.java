package com.example.testbackend.utils;

public class Constants {
    // Usar localhost para celular físico com adb reverse
    public static final String HOST = "localhost";

    // AUTH SERVICE (Porta 8080)
    public static final String AUTH_BASE_URL = "http://" + HOST + ":8080/";

    // EXERCISE SERVICE (Porta 8081)
    public static final String EXERCISE_BASE_URL = "http://" + HOST + ":8081/";

    // DEMAIS SERVIÇOS (Se houver gateway na 8080, podem ser mantidos)
    public static final String TRAINING_BASE_URL = "http://" + HOST + ":8030/";
    
    // AI Service Endpoints
    public static final String AI_HTTP_URL = "http://" + HOST + ":8080/ai/process-frame";
    public static final String AI_WS_URL = "ws://" + HOST + ":8080/ai/pose-stream";
}
