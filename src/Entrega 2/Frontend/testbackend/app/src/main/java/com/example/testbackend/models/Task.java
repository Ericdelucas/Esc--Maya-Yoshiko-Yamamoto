package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Task implements Serializable {
    private Integer id;
    @SerializedName("professional_id")
    private Integer professionalId;
    @SerializedName("patient_id")
    private Integer patientId;
    private String title;
    private String description;
    @SerializedName("points_value")
    private Integer pointsValue;
    @SerializedName("exercise_id")
    private Integer exerciseId;
    @SerializedName("frequency_per_week")
    private Integer frequencyPerWeek;
    @SerializedName("is_active")
    private Boolean isActive;
    @SerializedName("start_date")
    private String startDate;
    @SerializedName("end_date")
    private String endDate;
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("completed_today")
    private Boolean completedToday; // Auxiliar para UI
    
    @SerializedName("exercise_image_url")
    private String exerciseImageUrl;
    
    @SerializedName("exercise_video_url")
    private String exerciseVideoUrl;

    public Task() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getProfessionalId() { return professionalId; }
    public void setProfessionalId(Integer professionalId) { this.professionalId = professionalId; }

    public Integer getPatientId() { return patientId; }
    public void setPatientId(Integer patientId) { this.patientId = patientId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPointsValue() { return pointsValue; }
    public void setPointsValue(Integer pointsValue) { this.pointsValue = pointsValue; }

    public Integer getExerciseId() { return exerciseId; }
    public void setExerciseId(Integer exerciseId) { this.exerciseId = exerciseId; }

    public Integer getFrequencyPerWeek() { return frequencyPerWeek; }
    public void setFrequencyPerWeek(Integer frequencyPerWeek) { this.frequencyPerWeek = frequencyPerWeek; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public Boolean getCompletedToday() { return completedToday != null && completedToday; }
    public void setCompletedToday(Boolean completedToday) { this.completedToday = completedToday; }

    public String getExerciseImageUrl() { return exerciseImageUrl; }
    public void setExerciseImageUrl(String exerciseImageUrl) { this.exerciseImageUrl = exerciseImageUrl; }

    public String getExerciseVideoUrl() { return exerciseVideoUrl; }
    public void setExerciseVideoUrl(String exerciseVideoUrl) { this.exerciseVideoUrl = exerciseVideoUrl; }
}
