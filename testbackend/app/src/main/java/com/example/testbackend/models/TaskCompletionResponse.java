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
    
    // Getters
    public Integer getId() { return id; }
    public Integer getTaskId() { return taskId; }
    public Integer getPatientId() { return patientId; }
    public String getCompletionDate() { return completionDate; }
    public Integer getPointsAwarded() { return pointsAwarded; }
    public String getMessage() { return message; }
}
