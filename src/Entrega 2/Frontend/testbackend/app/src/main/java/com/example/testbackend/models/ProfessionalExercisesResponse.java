package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProfessionalExercisesResponse {
    @SerializedName("success")
    private Boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("total_exercises")
    private Integer totalExercises;
    
    @SerializedName("total_patients")
    private Integer totalPatients;
    
    @SerializedName("exercises")
    private List<ExerciseItem> exercises;
    
    // Getters
    public Boolean isSuccess() { return success != null ? success : false; }
    public String getMessage() { return message; }
    public Integer getTotalExercises() { return totalExercises; }
    public Integer getTotalPatients() { return totalPatients; }
    public List<ExerciseItem> getExercises() { return exercises; }
    
    public static class ExerciseItem {
        @SerializedName("id")
        private Integer id;
        
        @SerializedName("title")
        private String title;
        
        @SerializedName("description")
        private String description;
        
        @SerializedName("points_value")
        private Integer pointsValue;
        
        @SerializedName("frequency_per_week")
        private Integer frequencyPerWeek;
        
        @SerializedName("patient_id")
        private Integer patientId;
        
        @SerializedName("can_delete")
        private Boolean canDelete;
        
        @SerializedName("assigned_by")
        private String assignedBy;
        
        @SerializedName("created_at")
        private String createdAt;
        
        // Getters
        public Integer getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public Integer getPointsValue() { return pointsValue; }
        public Integer getFrequencyPerWeek() { return frequencyPerWeek; }
        public Integer getPatientId() { return patientId; }
        public Boolean getCanDelete() { return canDelete; }
        public String getAssignedBy() { return assignedBy; }
        public String getCreatedAt() { return createdAt; }
    }
}
