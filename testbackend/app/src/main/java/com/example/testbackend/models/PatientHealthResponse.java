package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class PatientHealthResponse {
    private boolean success;
    @SerializedName("patient_info")
    private PatientInfo patientInfo;
    private List<QuestionnaireData> questionnaires;
    private List<BmiData> bmis;
    @SerializedName("total_records")
    private TotalRecords totalRecords;

    public boolean isSuccess() { return success; }
    public PatientInfo getPatientInfo() { return patientInfo; }
    public List<QuestionnaireData> getQuestionnaires() { return questionnaires; }
    public List<BmiData> getBmis() { return bmis; }
    public TotalRecords getTotalRecords() { return totalRecords; }

    public static class PatientInfo {
        private int id;
        private String name;
        private String email;

        public int getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
    }

    public static class QuestionnaireData {
        private int id;
        @SerializedName("total_score")
        private int totalScore;
        @SerializedName("max_score")
        private int maxScore;
        @SerializedName("risk_level")
        private String riskLevel;
        private Map<String, String> answers;
        @SerializedName("created_at")
        private String createdAt;

        // Construtor vazio para Gson
        public QuestionnaireData() {}

        // Construtor para dados de exemplo
        public QuestionnaireData(int id, int totalScore, int maxScore, String riskLevel, String createdAt) {
            this.id = id;
            this.totalScore = totalScore;
            this.maxScore = maxScore;
            this.riskLevel = riskLevel;
            this.createdAt = createdAt;
        }

        public int getId() { return id; }
        public int getTotalScore() { return totalScore; }
        public int getMaxScore() { return maxScore; }
        public String getRiskLevel() { return riskLevel; }
        public Map<String, String> getAnswers() { return answers; }
        public String getCreatedAt() { return createdAt; }
    }

    public static class BmiData {
        private int id;
        private double bmi;
        private double height;
        private double weight;
        private String category;
        @SerializedName("created_at")
        private String createdAt;

        // Construtor vazio para Gson
        public BmiData() {}

        // Construtor para dados de exemplo
        public BmiData(int id, double bmi, double height, double weight, String category, String createdAt) {
            this.id = id;
            this.bmi = bmi;
            this.height = height;
            this.weight = weight;
            this.category = category;
            this.createdAt = createdAt;
        }

        public int getId() { return id; }
        public double getBmi() { return bmi; }
        public double getHeight() { return height; }
        public double getWeight() { return weight; }
        public String getCategory() { return category; }
        public String getCreatedAt() { return createdAt; }
    }

    public static class TotalRecords {
        private int questionnaires;
        private int bmis;

        public int getQuestionnaires() { return questionnaires; }
        public int getBmis() { return bmis; }
    }
}
