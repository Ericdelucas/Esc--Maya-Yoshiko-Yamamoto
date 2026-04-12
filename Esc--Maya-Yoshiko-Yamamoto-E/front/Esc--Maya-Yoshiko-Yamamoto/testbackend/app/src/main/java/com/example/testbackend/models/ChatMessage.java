package com.example.testbackend.models;

public class ChatMessage {
    public static final int TYPE_USER = 0;
    public static final int TYPE_ASSISTANT = 1;

    private String text;
    private int type;
    private long timestamp;

    public ChatMessage(String text, int type) {
        this.text = text;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public String getText() { return text; }
    public int getType() { return type; }
    public long getTimestamp() { return timestamp; }
}