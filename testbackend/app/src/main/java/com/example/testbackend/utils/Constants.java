package com.example.testbackend.utils;

import android.util.Log;

public class Constants {
    
    private static final String TAG = "Constants_DEBUG";
    
    // 🔥 SOLUÇÃO DEFINITIVA: App e Backend na mesma máquina
    public static final String HOST = "localhost";
    
    // 🔥 TODOS OS SERVIÇOS DE AUTH E APPOINTMENTS NA PORTA 8080
    public static final String AUTH_BASE_URL = "http://" + HOST + ":8080/";
    public static final String PACIENTES_BASE_URL = "http://" + HOST + ":8080/";
    
    public static final String EXERCISE_BASE_URL = "http://" + HOST + ":8081/";
    public static final String HEALTH_BASE_URL = "http://" + HOST + ":8071/";
    public static final String TRAINING_BASE_URL = "http://" + HOST + ":8030/";
    public static final String AI_HTTP_URL = "http://" + HOST + ":8090/ai/process-frame";
}
