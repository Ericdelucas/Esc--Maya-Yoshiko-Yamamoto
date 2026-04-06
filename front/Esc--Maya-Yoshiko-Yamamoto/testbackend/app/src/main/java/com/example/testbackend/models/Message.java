package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    @SerializedName("id")
    private int id;
    
    @SerializedName("conversation_id")
    private int conversationId;
    
    @SerializedName("sender_id")
    private int senderId;
    
    @SerializedName("receiver_id")
    private int receiverId;
    
    @SerializedName("content")
    private String content;
    
    @SerializedName("message_type")
    private String messageType;
    
    @SerializedName("file_url")
    private String fileUrl;
    
    @SerializedName("is_read")
    private boolean isRead;
    
    @SerializedName("created_at")
    private Date createdAt;
    
    public Message() {
        this.messageType = "text";
        this.createdAt = new Date();
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getConversationId() { return conversationId; }
    public void setConversationId(int conversationId) { this.conversationId = conversationId; }
    
    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }
    
    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public boolean isFromCurrentUser(int currentUserId) {
        return senderId == currentUserId;
    }
}
