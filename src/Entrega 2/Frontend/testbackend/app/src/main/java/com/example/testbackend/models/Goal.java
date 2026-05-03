package com.example.testbackend.models;

public class Goal {
    private String title;
    private int targetValue;
    private int currentValue;
    private String status;

    public Goal(String title, int targetValue, int currentValue, String status) {
        this.title = title;
        this.targetValue = targetValue;
        this.currentValue = currentValue;
        this.status = status;
    }

    public String getTitle() { return title; }
    public int getTargetValue() { return targetValue; }
    public int getCurrentValue() { return currentValue; }
    public String getStatus() { return status; }
}