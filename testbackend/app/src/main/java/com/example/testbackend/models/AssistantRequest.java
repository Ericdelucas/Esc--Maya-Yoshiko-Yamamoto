package com.example.testbackend.models;

public class AssistantRequest {
    private final String session_id;
    private final Integer user_id;
    private final String message;
    private final String screen_context;

    public AssistantRequest(String session_id, Integer user_id, String message, String screen_context) {
        this.session_id = session_id;
        this.user_id = user_id;
        this.message = message;
        this.screen_context = screen_context;
    }

    public String getSession_id() { return session_id; }
    public Integer getUser_id() { return user_id; }
    public String getMessage() { return message; }
    public String getScreen_context() { return screen_context; }
}