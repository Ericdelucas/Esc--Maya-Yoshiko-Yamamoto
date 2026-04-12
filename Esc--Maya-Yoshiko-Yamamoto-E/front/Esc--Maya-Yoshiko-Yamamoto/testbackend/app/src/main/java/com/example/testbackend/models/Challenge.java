package com.example.testbackend.models;

public class Challenge {
    private String title;
    private String description;
    private int currentProgress;
    private int targetProgress;
    private int rewardPoints;

    public Challenge(String title, String description, int currentProgress, int targetProgress, int rewardPoints) {
        this.title = title;
        this.description = description;
        this.currentProgress = currentProgress;
        this.targetProgress = targetProgress;
        this.rewardPoints = rewardPoints;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getCurrentProgress() { return currentProgress; }
    public int getTargetProgress() { return targetProgress; }
    public int getRewardPoints() { return rewardPoints; }
}