package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class PatientReport implements Serializable {
    @SerializedName("id")
    private Integer id;
    @SerializedName("patient_id")
    private Integer patientId;
    @SerializedName("professional_id")
    private Integer professionalId;
    @SerializedName("report_date")
    private String reportDate;
    @SerializedName("report_type")
    private String reportType;
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;
    @SerializedName("clinical_evolution")
    private String clinicalEvolution;
    @SerializedName("objective_data")
    private String objectiveData;
    @SerializedName("subjective_data")
    private String subjectiveData;
    @SerializedName("treatment_plan")
    private String treatmentPlan;
    @SerializedName("recommendations")
    private String recommendations;
    @SerializedName("next_steps")
    private String nextSteps;
    @SerializedName("pain_scale")
    private Integer painScale;
    @SerializedName("functional_status")
    private String functionalStatus;
    @SerializedName("achievements")
    private List<String> achievements;
    @SerializedName("limitations")
    private List<String> limitations;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("created_by")
    private String createdBy;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getPatientId() { return patientId; }
    public void setPatientId(Integer patientId) { this.patientId = patientId; }
    public Integer getProfessionalId() { return professionalId; }
    public void setProfessionalId(Integer professionalId) { this.professionalId = professionalId; }
    public String getReportDate() { return reportDate; }
    public void setReportDate(String reportDate) { this.reportDate = reportDate; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getClinicalEvolution() { return clinicalEvolution; }
    public void setClinicalEvolution(String clinicalEvolution) { this.clinicalEvolution = clinicalEvolution; }
    public String getObjectiveData() { return objectiveData; }
    public void setObjectiveData(String objectiveData) { this.objectiveData = objectiveData; }
    public String getSubjectiveData() { return subjectiveData; }
    public void setSubjectiveData(String subjectiveData) { this.subjectiveData = subjectiveData; }
    public String getTreatmentPlan() { return treatmentPlan; }
    public void setTreatmentPlan(String treatmentPlan) { this.treatmentPlan = treatmentPlan; }
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    public String getNextSteps() { return nextSteps; }
    public void setNextSteps(String nextSteps) { this.nextSteps = nextSteps; }
    public Integer getPainScale() { return painScale; }
    public void setPainScale(Integer painScale) { this.painScale = painScale; }
    public String getFunctionalStatus() { return functionalStatus; }
    public void setFunctionalStatus(String functionalStatus) { this.functionalStatus = functionalStatus; }
    public List<String> getAchievements() { return achievements; }
    public void setAchievements(List<String> achievements) { this.achievements = achievements; }
    public List<String> getLimitations() { return limitations; }
    public void setLimitations(List<String> limitations) { this.limitations = limitations; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
