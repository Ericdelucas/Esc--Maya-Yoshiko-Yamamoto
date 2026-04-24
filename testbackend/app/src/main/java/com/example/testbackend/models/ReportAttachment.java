package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class ReportAttachment {
    @SerializedName("id")
    private int id;
    
    @SerializedName("report_id")
    private int reportId;
    
    @SerializedName("attachment_type")
    private String attachmentType;
    
    @SerializedName("file_name")
    private String fileName;
    
    @SerializedName("file_path")
    private String filePath;
    
    @SerializedName("file_size")
    private Integer fileSize;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("uploaded_at")
    private String uploadedAt;
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }
    
    public String getAttachmentType() { return attachmentType; }
    public void setAttachmentType(String attachmentType) { this.attachmentType = attachmentType; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public Integer getFileSize() { return fileSize; }
    public void setFileSize(Integer fileSize) { this.fileSize = fileSize; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(String uploadedAt) { this.uploadedAt = uploadedAt; }
}
