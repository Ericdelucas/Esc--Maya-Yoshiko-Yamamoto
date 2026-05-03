package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class DailyProgressData {
    @SerializedName("user_id")
    private Integer userId;
    
    @SerializedName("date")
    private String date;
    
    @SerializedName("total_daily_exercises")
    private Integer totalDailyExercises;
    
    @SerializedName("completed_today")
    private Integer completedToday;
    
    @SerializedName("remaining_today")
    private Integer remainingToday;
    
    @SerializedName("progress_percentage")
    private Double progressPercentage;
    
    @SerializedName("progress_fraction")
    private String progressFraction;
    
    @SerializedName("is_complete")
    private Boolean isComplete;
    
    @SerializedName("status_message")
    private String statusMessage;
    
    // Getters
    public Integer getUserId() { return userId; }
    public String getDate() { return date; }
    public Integer getTotalDailyExercises() { return totalDailyExercises; }
    public Integer getCompletedToday() { return completedToday; }
    public Integer getRemainingToday() { return remainingToday; }
    public Double getProgressPercentage() { return progressPercentage; }
    public String getProgressFraction() { return progressFraction; }
    public Boolean getIsComplete() { return isComplete; }
    public String getStatusMessage() { return statusMessage; }
}
