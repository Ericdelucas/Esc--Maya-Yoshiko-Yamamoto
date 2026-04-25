package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class DailyProgressResponse {
    @SerializedName("success")
    private Boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private DailyProgressData data;
    
    // Getters
    public Boolean isSuccess() { return success != null ? success : false; }
    public String getMessage() { return message; }
    public DailyProgressData getData() { return data; }
}
