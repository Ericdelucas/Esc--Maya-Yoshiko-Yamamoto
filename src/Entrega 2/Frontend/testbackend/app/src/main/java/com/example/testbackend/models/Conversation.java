package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;

public class Conversation implements Serializable {
    @SerializedName("id")
    private int id;
    
    @SerializedName("patient_id")
    private int patientId;
    
    @SerializedName("professional_id")
    private int professionalId;
    
    @SerializedName("created_at")
    private Date createdAt;
    
    @SerializedName("updated_at")
    private Date updatedAt;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("last_message")
    private Message lastMessage;
    
    @SerializedName("unread_count")
    private int unreadCount;
    
    public Conversation() {}
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    
    public int getProfessionalId() { return professionalId; }
    public void setProfessionalId(int professionalId) { this.professionalId = professionalId; }
    
    public Message getLastMessage() { return lastMessage; }
    public void setLastMessage(Message lastMessage) { this.lastMessage = lastMessage; }
    
    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
}
