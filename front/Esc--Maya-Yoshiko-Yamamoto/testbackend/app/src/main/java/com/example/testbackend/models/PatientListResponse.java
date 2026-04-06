package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PatientListResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("patients")
    private List<Patient> patients;
    
    @SerializedName("total")
    private int total;
    
    @SerializedName("message")
    private String message;
    
    public boolean isSuccess() { return success; }
    public List<Patient> getPatients() { return patients; }
    public int getTotal() { return total; }
    public String getMessage() { return message; }
    
    public void setSuccess(boolean success) { this.success = success; }
    public void setPatients(List<Patient> patients) { this.patients = patients; }
    public void setTotal(int total) { this.total = total; }
    public void setMessage(String message) { this.message = message; }
}
