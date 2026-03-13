package com.example.testbackend.models;

public class AssistantRequest {
    private String message;

    public AssistantRequest(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}