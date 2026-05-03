package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class FileUploadResponse {
    @SerializedName("file_name")
    private String fileName;
    
    @SerializedName("file_url")
    private String fileUrl;
    
    @SerializedName("content_type")
    private String contentType;

    public String getFileName() { return fileName; }
    public String getFileUrl() { return fileUrl; }
    public String getContentType() { return contentType; }
}