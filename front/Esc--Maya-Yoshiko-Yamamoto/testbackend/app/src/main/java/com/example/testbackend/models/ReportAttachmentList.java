package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ReportAttachmentList {
    @SerializedName("attachments")
    private List<ReportAttachment> attachments;
    
    @SerializedName("total")
    private int total;
    
    public List<ReportAttachment> getAttachments() { return attachments; }
    public void setAttachments(List<ReportAttachment> attachments) { this.attachments = attachments; }
    
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
}
