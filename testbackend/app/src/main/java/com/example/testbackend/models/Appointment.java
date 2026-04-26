package com.example.testbackend.models;

import java.util.Date;

public class Appointment {
    private int id;
    private String title;
    private Date date;
    private String description;
    private Integer patientId; // 🔥 NOVO: ID do paciente

    public Appointment(int id, String title, Date date, String description) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.description = description;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    // 🔥 NOVO: Getter e Setter para patientId
    public Integer getPatientId() { return patientId; }
    public void setPatientId(Integer patientId) { this.patientId = patientId; }
}
