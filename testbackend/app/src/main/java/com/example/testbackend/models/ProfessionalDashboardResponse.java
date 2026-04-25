package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class ProfessionalDashboardResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private DashboardData data;

    public boolean isSuccess() { return success; }
    public DashboardData getData() { return data; }

    public static class DashboardData {
        @SerializedName("total_patients")
        private int totalPatients;
        
        @SerializedName("total_exercises")
        private int totalExercises;
        
        @SerializedName("appointments_today")
        private int appointmentsToday;

        public int getTotalPatients() { return totalPatients; }
        public int getTotalExercises() { return totalExercises; }
        public int getAppointmentsToday() { return appointmentsToday; }
    }
}
