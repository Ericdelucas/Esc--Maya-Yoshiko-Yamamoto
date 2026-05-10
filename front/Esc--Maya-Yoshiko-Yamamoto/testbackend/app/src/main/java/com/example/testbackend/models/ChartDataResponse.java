package com.example.testbackend.models;

import java.util.List;
import java.util.Map;

public class ChartDataResponse {
    private List<PainDistributionData> pain_distribution;
    private List<MonthlyReportData> monthly_reports;
    private List<FunctionalStatusData> functional_status;

    // Getters and Setters
    public List<PainDistributionData> getPainDistribution() {
        return pain_distribution;
    }

    public void setPainDistribution(List<PainDistributionData> pain_distribution) {
        this.pain_distribution = pain_distribution;
    }

    public List<MonthlyReportData> getMonthlyReports() {
        return monthly_reports;
    }

    public void setMonthlyReports(List<MonthlyReportData> monthly_reports) {
        this.monthly_reports = monthly_reports;
    }

    public List<FunctionalStatusData> getFunctionalStatus() {
        return functional_status;
    }

    public void setFunctionalStatus(List<FunctionalStatusData> functional_status) {
        this.functional_status = functional_status;
    }

    public static class PainDistributionData {
        private String report_type;
        private int pain_scale;

        public String getReportType() {
            return report_type;
        }

        public void setReportType(String report_type) {
            this.report_type = report_type;
        }

        public int getPainScale() {
            return pain_scale;
        }

        public void setPainScale(int pain_scale) {
            this.pain_scale = pain_scale;
        }
    }

    public static class MonthlyReportData {
        private String month;
        private String report_type;
        private int count;

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getReportType() {
            return report_type;
        }

        public void setReportType(String report_type) {
            this.report_type = report_type;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static class FunctionalStatusData {
        private String status;
        private int count;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
