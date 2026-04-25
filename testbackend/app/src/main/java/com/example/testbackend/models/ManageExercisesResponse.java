package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ManageExercisesResponse {
    @SerializedName("success")
    private Boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("patient_id")
    private Integer patientId;
    
    @SerializedName("total_exercises")
    private Integer totalExercises;
    
    @SerializedName("exercises")
    private List<ManageExerciseItem> exercises;
    
    // Getters
    public Boolean isSuccess() { return success != null ? success : false; }
    public String getMessage() { return message; }
    public Integer getPatientId() { return patientId; }
    public Integer getTotalExercises() { return totalExercises; }
    public List<ManageExerciseItem> getExercises() { return exercises; }
    
    public static class ManageExerciseItem {
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
        
        @SerializedName("is_active")
        private Boolean isActive;
        
        @SerializedName("created_at")
        private String createdAt;
        
        @SerializedName("can_delete")
        private Boolean canDelete;
        
        @SerializedName("assigned_by")
        private String assignedBy;
        
        @SerializedName("assigned_at")
        private String assignedAt;
        
        // Getters
        public Integer getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public Integer getPointsValue() { return pointsValue; }
        public Integer getFrequencyPerWeek() { return frequencyPerWeek; }
        public Boolean getIsActive() { return isActive; }
        public String getCreatedAt() { return createdAt; }
        public Boolean getCanDelete() { return canDelete; }
        public String getAssignedBy() { return assignedBy; }
        public String getAssignedAt() { return assignedAt; }
    }
}
