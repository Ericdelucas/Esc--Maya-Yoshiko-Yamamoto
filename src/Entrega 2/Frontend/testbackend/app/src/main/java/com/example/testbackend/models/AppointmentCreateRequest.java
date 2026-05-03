package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class AppointmentCreateRequest {
    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("appointment_date")
    private String appointmentDate; // yyyy-MM-dd

    @SerializedName("time")
    private String time; // HH:mm

    @SerializedName("patient_id")
    private Integer patientId;
    
    public AppointmentCreateRequest() {}
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }
    
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    
    public Integer getPatientId() { return patientId; }
    public void setPatientId(Integer patientId) { this.patientId = patientId; }
}
