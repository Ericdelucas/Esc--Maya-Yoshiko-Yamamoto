package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class DashboardStats {
    @SerializedName("total_patients")
    private int totalPatients;

    @SerializedName("appointments_today")
    private int appointmentsToday;

    @SerializedName("active_exercises")
    private int activeExercises;

    public int getTotalPatients() { return totalPatients; }
    public int getAppointmentsToday() { return appointmentsToday; }
    public int getActiveExercises() { return activeExercises; }
}
