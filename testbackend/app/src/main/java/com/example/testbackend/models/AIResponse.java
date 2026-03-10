package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AIResponse {
    private List<Landmark> landmarks;
    
    @SerializedName("validation_status")
    private String validationStatus;
    
    @SerializedName("audio_feedback_url")
    private String audioFeedbackUrl;

    @SerializedName("rep_count")
    private Integer repCount;

    public List<Landmark> getLandmarks() { return landmarks; }
    public String getValidationStatus() { return validationStatus; }
    public String getAudioFeedbackUrl() { return audioFeedbackUrl; }
    public Integer getRepCount() { return repCount; }
}