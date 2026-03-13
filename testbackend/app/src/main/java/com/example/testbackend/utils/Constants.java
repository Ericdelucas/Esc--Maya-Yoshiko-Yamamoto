package com.example.testbackend.utils;

public class Constants {
    public static final String HOST = "10.0.2.2";

    // Portas alinhadas com o backend
    public static final String AUTH_BASE_URL = "http://10.0.2.2:8080/";
    public static final String EXERCISE_BASE_URL = "http://10.0.2.2:8081/";
    public static final String TRAINING_BASE_URL = "http://10.0.2.2:8030/";
    public static final String ANALYTICS_BASE_URL = "http://10.0.2.2:8050/";
    public static final String EHR_BASE_URL = "http://10.0.2.2:8060/";
    public static final String NOTIFICATION_BASE_URL = "http://10.0.2.2:8070/";
    
    // AI Service Endpoints
    public static final String AI_HTTP_URL = "http://10.0.2.2:8090/ai/process-frame";
    public static final String AI_WS_URL = "ws://10.0.2.2:8090/ai/pose-stream";
}
