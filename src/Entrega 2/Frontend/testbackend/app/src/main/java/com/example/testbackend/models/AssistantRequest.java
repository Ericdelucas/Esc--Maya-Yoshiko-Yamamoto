package com.example.testbackend.models;

import java.util.Locale;

public class AssistantRequest {
    private final String session_id;
    private final Integer user_id;
    private final String message;
    private final String screen_context;
    private final String language;

    public AssistantRequest(String session_id, Integer user_id, String message, String screen_context) {
        this.session_id = session_id;
        this.user_id = user_id;
        this.message = message;
        this.screen_context = screen_context;
        // Pega o idioma atual do dispositivo (ex: "pt" ou "en")
        this.language = Locale.getDefault().getLanguage();
    }

    public String getSession_id() { return session_id; }
    public Integer getUser_id() { return user_id; }
    public String getMessage() { return message; }
    public String getScreen_context() { return screen_context; }
    public String getLanguage() { return language; }
}