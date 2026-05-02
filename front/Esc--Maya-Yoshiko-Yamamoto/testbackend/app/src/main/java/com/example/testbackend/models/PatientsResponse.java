package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PatientsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("total_patients")
    private int totalPatients;

    @SerializedName("patients")
    private List<Patient> patients;

    public PatientsResponse() {}

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(int totalPatients) {
        this.totalPatients = totalPatients;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }
}
