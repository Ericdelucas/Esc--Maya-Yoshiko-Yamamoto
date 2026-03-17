package com.example.testbackend.models;

public class Professional {
    private String name;
    private String specialty;
    private String status;
    private String photoUrl;

    public Professional(String name, String specialty, String status, String photoUrl) {
        this.name = name;
        this.specialty = specialty;
        this.status = status;
        this.photoUrl = photoUrl;
    }

    public String getName() { return name; }
    public String getSpecialty() { return specialty; }
    public String getStatus() { return status; }
    public String getPhotoUrl() { return photoUrl; }
}