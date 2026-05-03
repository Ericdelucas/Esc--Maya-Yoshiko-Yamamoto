package com.example.testbackend.models;

public class AssistantResponse {
    private String reply;
    private String intent;
    private AssistantAction action;
    private boolean memory_updated;

    public String getReply() { return reply; }
    public String getIntent() { return intent; }
    public AssistantAction getAction() { return action; }
    public boolean isMemory_updated() { return memory_updated; }
}