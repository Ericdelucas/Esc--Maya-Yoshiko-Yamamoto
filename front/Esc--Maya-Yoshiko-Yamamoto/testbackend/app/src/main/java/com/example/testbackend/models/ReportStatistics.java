package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class ReportStatistics {
    @SerializedName("report_types")
    private Map<String, Object> reportTypes;
    @SerializedName("total_reports")
    private Integer totalReports;
    @SerializedName("recent_reports")
    private List<PatientReport> recentReports;

    // Getters and Setters
    public Map<String, Object> getReportTypes() { return reportTypes; }
    public void setReportTypes(Map<String, Object> reportTypes) { this.reportTypes = reportTypes; }
    public Integer getTotalReports() { return totalReports; }
    public void setTotalReports(Integer totalReports) { this.totalReports = totalReports; }
    public List<PatientReport> getRecentReports() { return recentReports; }
    public void setRecentReports(List<PatientReport> recentReports) { this.recentReports = recentReports; }
}
