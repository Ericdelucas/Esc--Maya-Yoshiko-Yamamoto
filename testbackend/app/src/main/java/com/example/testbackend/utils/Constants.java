package com.example.testbackend.utils;

public class Constants {
    // 🔥 MANTER LOCALHOST (FUNCIONA COM ADB REVERSE)
    public static final String HOST = "localhost";
    
    // 🔥 URLS ORIGINAIS
    public static final String AUTH_BASE_URL = "http://" + HOST + ":8080/";
    public static final String EXERCISE_BASE_URL = "http://" + HOST + ":8081/";
    public static final String HEALTH_BASE_URL = "http://" + HOST + ":8071/";
    public static final String TRAINING_BASE_URL = "http://" + HOST + ":8030/";
    public static final String AI_HTTP_URL = "http://" + HOST + ":8090/ai/process-frame";

    // PACIENTES SERVICE (Porta 8085) - Necessária para o ApiClient compilar
    public static final String PACIENTES_BASE_URL = "http://" + HOST + ":8085/";
}
