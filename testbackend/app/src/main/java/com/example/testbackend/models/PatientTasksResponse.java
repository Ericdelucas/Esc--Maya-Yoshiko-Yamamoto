package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PatientTasksResponse {
    @SerializedName("patient_id")
    private int patientId;
    
    @SerializedName("tasks")
    private List<Task> tasks;

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
