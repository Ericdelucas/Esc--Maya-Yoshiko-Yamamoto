package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PatientExercisesResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("patient_id")
    private int patientId;

    @SerializedName("patient_name")
    private String patientName;

    @SerializedName("total_exercises")
    private int totalExercises;

    @SerializedName("exercises")
    private List<Task> exercises;

    public PatientExercisesResponse() {}

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public int getTotalExercises() {
        return totalExercises;
    }

    public void setTotalExercises(int totalExercises) {
        this.totalExercises = totalExercises;
    }

    public List<Task> getExercises() {
        return exercises;
    }

    public void setExercises(List<Task> exercises) {
        this.exercises = exercises;
    }
}
