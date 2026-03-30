package com.example.testbackend.utils;

public class Constants {
    /**
     * OPÇÃO A: DESENVOLVIMENTO LOCAL
     * Substitua pelo IP da sua máquina (comando: ip addr show)
     * se estiver usando um dispositivo físico na mesma rede Wi-Fi.
     * Se estiver usando o emulador, pode usar "10.0.2.2" ou o IP da rede.
     */
    public static final String HOST = "192.168.15.8";

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
