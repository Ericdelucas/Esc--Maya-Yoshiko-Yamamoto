package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class UserPoints {
    @SerializedName("user_id")
    private Integer userId;
    @SerializedName("total_points")
    private Integer totalPoints;
    @SerializedName("weekly_points")
    private Integer weeklyPoints;
    @SerializedName("monthly_points")
    private Integer monthlyPoints;
    @SerializedName("current_streak")
    private Integer currentStreak;
    @SerializedName("longest_streak")
    private Integer longestStreak;
    @SerializedName("last_completion_date")
    private String lastCompletionDate;
    @SerializedName("rank_position")
    private Integer rankPosition;

    public Integer getUserId() { return userId; }
    public Integer getTotalPoints() { return totalPoints != null ? totalPoints : 0; }
    public Integer getWeeklyPoints() { return weeklyPoints != null ? weeklyPoints : 0; }
    public Integer getMonthlyPoints() { return monthlyPoints != null ? monthlyPoints : 0; }
    public Integer getCurrentStreak() { return currentStreak != null ? currentStreak : 0; }
    public Integer getLongestStreak() { return longestStreak != null ? longestStreak : 0; }
    public String getLastCompletionDate() { return lastCompletionDate; }
    public Integer getRankPosition() { return rankPosition != null ? rankPosition : 0; }
}
