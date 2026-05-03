package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class HealthMetricResponse {
    @SerializedName("id")
    private int id;
    
    @SerializedName("metric_type")
    private String metricType;
    
    @SerializedName("value")
    private double value;
    
    @SerializedName("unit")
    private String unit;
    
    @SerializedName("classification")
    private String classification;
    
    @SerializedName("measured_at")
    private String measuredAt;
    
    @SerializedName("user_id")
    private int userId;

    public int getId() { return id; }
    public String getMetricType() { return metricType; }
    public double getValue() { return value; }
    public String getUnit() { return unit; }
    public String getClassification() { return classification; }
    public String getMeasuredAt() { return measuredAt; }
    public int getUserId() { return userId; }
}
