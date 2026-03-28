package com.example.testbackend.utils;

public class Constants {
    // Usar a URL do servidor em produção ou o host correto
    public static final String BASE_URL = "https://smartsaude-auth.onrender.com/";
    
    // AUTH SERVICE (Usando BASE_URL por padrão para consistência)
    public static final String AUTH_BASE_URL = BASE_URL;

    // Outros serviços podem precisar de URLs específicas se não estiverem no mesmo domínio
    public static final String HOST = "localhost";
    public static final String EXERCISE_BASE_URL = "http://" + HOST + ":8081/";
    public static final String TRAINING_BASE_URL = "http://" + HOST + ":8030/";
    
    // AI Service Endpoints
    public static final String AI_HTTP_URL = "http://" + HOST + ":8080/ai/process-frame";
    public static final String AI_WS_URL = "ws://" + HOST + ":8080/ai/pose-stream";
}
