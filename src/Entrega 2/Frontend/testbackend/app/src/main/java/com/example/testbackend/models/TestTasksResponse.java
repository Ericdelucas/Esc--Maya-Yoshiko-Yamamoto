package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TestTasksResponse {
    @SerializedName("message")
    private String message;
    
    @SerializedName("tasks")
    private List<Task> tasks;
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public List<Task> getTasks() { return tasks; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }
}
