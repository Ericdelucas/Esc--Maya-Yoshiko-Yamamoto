package com.example.testbackend.models;

import java.util.List;

public class ProgressResponse {
    private float weekly_completion;
    private String motivational_message;
    private List<Float> weekly_history; // Percentuais dos últimos 7 dias

    public float getWeeklyCompletion() { return weekly_completion; }
    public String getMotivationalMessage() { return motivational_message; }
    public List<Float> getWeeklyHistory() { return weekly_history; }
}