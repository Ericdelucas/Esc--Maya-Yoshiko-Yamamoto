package com.example.testbackend.models;

public class PatientProgress {
    private int totalSessions;
    private int weeklySessions;
    private int streakDays;
    private int progressPercentage;
    private int totalPoints;
    private int level;

    public PatientProgress(int totalSessions, int weeklySessions, int streakDays, int progressPercentage, int totalPoints, int level) {
        this.totalSessions = totalSessions;
        this.weeklySessions = weeklySessions;
        this.streakDays = streakDays;
        this.progressPercentage = progressPercentage;
        this.totalPoints = totalPoints;
        this.level = level;
    }

    public int getTotalSessions() { return totalSessions; }
    public int getWeeklySessions() { return weeklySessions; }
    public int getStreakDays() { return streakDays; }
    public int getProgressPercentage() { return progressPercentage; }
    public int getTotalPoints() { return totalPoints; }
    public int getLevel() { return level; }
}