package com.example.testbackend.models;

import java.util.List;
import java.util.Map;

public class AppointmentListResponse {
    private List<Map<String, Object>> appointments;
    
    public List<Map<String, Object>> getAppointments() {
        return appointments;
    }
    
    public void setAppointments(List<Map<String, Object>> appointments) {
        this.appointments = appointments;
    }
}
