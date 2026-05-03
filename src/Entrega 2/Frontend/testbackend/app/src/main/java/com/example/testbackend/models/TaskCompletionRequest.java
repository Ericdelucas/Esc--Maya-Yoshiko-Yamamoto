package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class TaskCompletionRequest {
    @SerializedName("task_id")
    private Integer taskId;
    
    @SerializedName("completion_notes")
    private String completionNotes;

    // Construtores
    public TaskCompletionRequest(Integer taskId) {
        this.taskId = taskId;
        this.completionNotes = "";
    }
    
    public TaskCompletionRequest(Integer taskId, String completionNotes) {
        this.taskId = taskId;
        this.completionNotes = completionNotes;
    }

    // Getters
    public Integer getTaskId() { return taskId; }
    public String getCompletionNotes() { return completionNotes; }

    // Setters
    public void setTaskId(Integer taskId) { this.taskId = taskId; }
    public void setCompletionNotes(String completionNotes) { this.completionNotes = completionNotes; }
}
