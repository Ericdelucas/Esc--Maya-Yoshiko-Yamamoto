package com.example.testbackend.utils;

public class Constants {
    /**
     * SOLUÇÃO: Usar localhost com adb reverse
     * O adb reverse redireciona localhost:8080 do emulador para a porta 8080 do host.
     * Execute no terminal: adb reverse tcp:8080 tcp:8080 && adb reverse tcp:8071 tcp:8071
     */
    public static final String HOST = "localhost";

    // AUTH SERVICE (Porta 8080)
    public static final String BASE_URL = "http://" + HOST + ":8080/";
    public static final String AUTH_BASE_URL = BASE_URL;

    // EXERCISE SERVICE (Porta 8081)
    public static final String EXERCISE_BASE_URL = "http://" + HOST + ":8081/";

    // TRAINING SERVICE (Porta 8030)
    public static final String TRAINING_BASE_URL = "http://" + HOST + ":8030/";
    
    // AI SERVICE (Porta 8090)
    public static final String AI_HTTP_URL = "http://" + HOST + ":8090/ai/process-frame";
    public static final String AI_WS_URL = "ws://" + HOST + ":8090/ai/pose-stream";

    // HEALTH SERVICE (Porta 8071 - Exposta via Docker Compose)
    public static final String HEALTH_BASE_URL = "http://" + HOST + ":8071/";
}
