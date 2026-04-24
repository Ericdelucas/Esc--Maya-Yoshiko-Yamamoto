package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class LeaderboardEntry {
    @SerializedName("user_id")
    private Integer userId;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("total_points")
    private Integer totalPoints;
    
    @SerializedName("rank")
    private Integer rank;
    
    @SerializedName("tasks_completed")
    private Integer tasksCompleted;

    @SerializedName("is_real_user")
    private Boolean isRealUser;

    public LeaderboardEntry() {}

    public Integer getUserId() { return userId; }
    public String getUsername() { return username; }
    public Integer getTotalPoints() { return totalPoints; }
    public Integer getRank() { return rank; }
    public Integer getTasksCompleted() { return tasksCompleted; }
    public Boolean getIsRealUser() { return isRealUser != null && isRealUser; }
    
    // Getters for adapter compatibility
    public String getName() { return username != null ? username : "Usuário"; }
    public int getPoints() { return totalPoints != null ? totalPoints : 0; }
    public int getPosition() { return rank != null ? rank : 0; }
    public boolean isRealUser() { return isRealUser != null && isRealUser; }
}
