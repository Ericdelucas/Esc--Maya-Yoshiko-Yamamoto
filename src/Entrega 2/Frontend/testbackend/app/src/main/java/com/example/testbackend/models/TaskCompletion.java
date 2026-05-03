package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class TaskCompletion {
    private Integer id;
    @SerializedName("task_id")
    private Integer taskId;
    @SerializedName("patient_id")
    private Integer patientId;
    @SerializedName("completed_at")
    private String completedAt;
    @SerializedName("points_earned")
    private Integer pointsEarned;
    @SerializedName("completion_notes")
    private String completionNotes;
    @SerializedName("verified_by_professional")
    private Boolean verifiedByProfessional;

    public Integer getId() { return id; }
    public Integer getTaskId() { return taskId; }
    public Integer getPatientId() { return patientId; }
    public String getCompletedAt() { return completedAt; }
    public Integer getPointsEarned() { return pointsEarned; }
    public String getCompletionNotes() { return completionNotes; }
    public Boolean getVerifiedByProfessional() { return verifiedByProfessional; }
}
