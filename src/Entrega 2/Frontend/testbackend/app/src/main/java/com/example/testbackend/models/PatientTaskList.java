package com.example.testbackend.models;

import java.util.List;

public class PatientTaskList {
    private List<Task> tasks;
    private Integer total_tasks;
    private Integer completed_tasks;
    private Integer total_points;

    public List<Task> getTasks() { return tasks; }
    public Integer getTotalTasks() { return total_tasks; }
    public Integer getCompletedTasks() { return completed_tasks; }
    public Integer getTotalPoints() { return total_points; }
}
