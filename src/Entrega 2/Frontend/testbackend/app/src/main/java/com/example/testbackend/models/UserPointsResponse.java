package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UserPointsResponse {
    @SerializedName("user_id")
    private Integer userId;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("total_points")
    private Integer totalPoints;
    
    @SerializedName("tasks_completed")
    private Integer tasksCompleted;
    
    @SerializedName("current_streak")
    private Integer currentStreak;
    
    @SerializedName("weekly_points")
    private Integer weeklyPoints;
    
    @SerializedName("monthly_points")
    private Integer monthlyPoints;
    
    @SerializedName("level")
    private String level;
    
    @SerializedName("next_level_points")
    private Integer nextLevelPoints;
    
    @SerializedName("badges")
    private List<String> badges;
    
    // Getters
    public Integer getUserId() { return userId; }
    public String getUsername() { return username; }
    public Integer getTotalPoints() { return totalPoints != null ? totalPoints : 0; }
    public Integer getTasksCompleted() { return tasksCompleted; }
    public Integer getCurrentStreak() { return currentStreak; }
    public Integer getWeeklyPoints() { return weeklyPoints; }
    public Integer getMonthlyPoints() { return monthlyPoints; }
    public String getLevel() { return level != null ? level : "Iniciante"; }
    public Integer getNextLevelPoints() { return nextLevelPoints; }
    public List<String> getBadges() { return badges; }
}
