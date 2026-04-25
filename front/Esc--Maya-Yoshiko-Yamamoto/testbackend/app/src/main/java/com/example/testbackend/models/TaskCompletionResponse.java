package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class TaskCompletionResponse {
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("task_id")
    private Integer taskId;
    
    @SerializedName("patient_id")
    private Integer patientId;
    
    @SerializedName("completion_date")
    private String completionDate;
    
    @SerializedName("points_awarded")
    private Integer pointsAwarded;
    
    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private Boolean success;

    @SerializedName("can_repeat_tomorrow")
    private Boolean canRepeatTomorrow;

    @SerializedName("tasks_completed_today")
    private Integer tasksCompletedToday;

    @SerializedName("remaining_tasks")
    private Integer remainingTasks;
    
    // Getters
    public Integer getId() { return id; }
    public Integer getTaskId() { return taskId; }
    public Integer getPatientId() { return patientId; }
    public String getCompletionDate() { return completionDate; }
    public Integer getPointsAwarded() { return pointsAwarded; }
    public String getMessage() { return message; }
    
    public Boolean isSuccess() { return success != null ? success : true; }
    public Boolean getCanRepeatTomorrow() { return canRepeatTomorrow; }
    public Integer getTasksCompletedToday() { return tasksCompletedToday; }
    public Integer getRemainingTasks() { return remainingTasks; }
}
