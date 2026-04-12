package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class UserProfileResponse {
    @SerializedName("id")
    private int id;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("full_name")
    private String fullName;
    
    @SerializedName("role")
    private String role;
    
    @SerializedName("profile_photo_url")
    private String profilePhotoUrl;
    
    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    
    public void setId(int id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setRole(String role) { this.role = role; }
    public void setProfilePhotoUrl(String url) { this.profilePhotoUrl = url; }
}