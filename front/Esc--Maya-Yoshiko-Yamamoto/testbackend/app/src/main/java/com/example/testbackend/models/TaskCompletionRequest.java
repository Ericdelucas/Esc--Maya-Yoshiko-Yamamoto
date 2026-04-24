package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class TaskCompletionRequest {
    @SerializedName("task_id")
    private Integer taskId;
    @SerializedName("completion_notes")
    private String completionNotes;

    public void setTaskId(Integer taskId) { this.taskId = taskId; }
    public void setCompletionNotes(String completionNotes) { this.completionNotes = completionNotes; }
}
