package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ReportList {
    @SerializedName("reports")
    private List<PatientReport> reports;
    @SerializedName("total")
    private Integer total;
    @SerializedName("page")
    private Integer page;
    @SerializedName("per_page")
    private Integer perPage;

    // Getters and Setters
    public List<PatientReport> getReports() { return reports; }
    public void setReports(List<PatientReport> reports) { this.reports = reports; }
    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getPerPage() { return perPage; }
    public void setPerPage(Integer perPage) { this.perPage = perPage; }
}
