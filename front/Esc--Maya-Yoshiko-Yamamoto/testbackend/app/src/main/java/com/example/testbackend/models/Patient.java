package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;

public class Patient implements Serializable {
    @SerializedName("id")
    private int id;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("full_name")
    private String fullName;
    
    @SerializedName("role")
    private String role;
    
    @SerializedName("created_at")
    private Date createdAt;
    
    @SerializedName("profile_photo_url")
    private String profilePhotoUrl;
    
    public Patient() {}
    
    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public Date getCreatedAt() { return createdAt; }
    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    
    public String getDisplayName() {
        return (fullName != null && !fullName.isEmpty()) ? fullName : email.split("@")[0];
    }
    
    public String getInitials() {
        if (fullName != null && !fullName.isEmpty()) {
            String[] names = fullName.trim().split("\\s+");
            if (names.length >= 2) {
                return (names[0].substring(0, 1) + names[names.length-1].substring(0, 1)).toUpperCase();
            } else if (names.length == 1 && !names[0].isEmpty()){
                return names[0].substring(0, 1).toUpperCase();
            }
        }
        return (email != null && !email.isEmpty()) ? email.substring(0, 1).toUpperCase() : "?";
    }
}
