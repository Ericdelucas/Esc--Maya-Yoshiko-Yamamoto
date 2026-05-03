package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class PatientEvaluation implements Serializable {
    @SerializedName("id")
    private int id;
    
    @SerializedName("patient_id")
    private int patientId;
    
    @SerializedName("professional_id")
    private int professionalId;
    
    @SerializedName("evaluation_date")
    private Date evaluationDate;

    // Bloco 1: Identificação
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("address")
    private String address;
    @SerializedName("phone")
    private String phone;
    @SerializedName("email")
    private String email;
    @SerializedName("cpf")
    private String cpf;
    @SerializedName("birth_date")
    private Date birthDate;
    @SerializedName("gender")
    private String gender;
    @SerializedName("marital_status")
    private String maritalStatus;

    // Bloco 2: Administrativo
    @SerializedName("first_contact_date")
    private Date firstContactDate;
    @SerializedName("profession")
    private String profession;
    @SerializedName("health_plan")
    private String healthPlan;
    @SerializedName("patient_origin")
    private String patientOrigin;
    @SerializedName("session_fee")
    private Double sessionFee;
    @SerializedName("medications")
    private List<String> medications;
    @SerializedName("appointment_time")
    private String appointmentTime;
    @SerializedName("frequency")
    private String frequency;

    // Bloco 3: Queixa Principal
    @SerializedName("main_reason")
    private String mainReason;
    @SerializedName("complaint_description")
    private String complaintDescription;
    @SerializedName("pain_example")
    private String painExample;
    @SerializedName("patient_objective")
    private String patientObjective;
    @SerializedName("pain_scale")
    private Integer painScale;
    @SerializedName("symptom_start_date")
    private Date symptomStartDate;

    // Bloco 4: Histórico de Dor
    @SerializedName("pain_location")
    private String painLocation;
    @SerializedName("duration")
    private String duration;
    @SerializedName("frequency_pattern")
    private String frequencyPattern;
    @SerializedName("triggers")
    private List<String> triggers;
    @SerializedName("relievers")
    private List<String> relievers;
    @SerializedName("pain_type")
    private String painType;
    @SerializedName("evolution_pattern")
    private String evolutionPattern;

    // Bloco 5: Histórico Clínico
    @SerializedName("accidents")
    private List<String> accidents;
    @SerializedName("surgeries")
    private List<String> surgeries;
    @SerializedName("traumas")
    private List<String> traumas;
    @SerializedName("immobilizations")
    private List<String> immobilizations;
    @SerializedName("hospitalizations")
    private List<String> hospitalizations;
    @SerializedName("previous_diseases")
    private List<String> previousDiseases;
    @SerializedName("allergies")
    private List<String> allergies;
    @SerializedName("family_history")
    private String familyHistory;

    // Bloco 6: Exames
    @SerializedName("imaging_exams")
    private List<ImagingExam> imagingExams;
    @SerializedName("lab_exams")
    private List<String> labExams;
    @SerializedName("previous_diagnoses")
    private List<String> previousDiagnoses;
    @SerializedName("relevant_results")
    private String relevantResults;

    // Bloco 7: Avaliação Física
    @SerializedName("postural_assessment")
    private PosturalAssessment posturalAssessment;
    @SerializedName("tension_areas")
    private List<String> tensionAreas;
    @SerializedName("asymmetries")
    private List<String> asymmetries;
    @SerializedName("adhesions_scars")
    private List<String> adhesionsScars;
    @SerializedName("referred_pain_areas")
    private List<String> referredPainAreas;
    @SerializedName("posture_profile")
    private String postureProfile;

    // Bloco 8: Causa -> Consequência
    @SerializedName("probable_origin")
    private String probableOrigin;
    @SerializedName("connections")
    private List<String> connections;
    @SerializedName("biomechanical_pattern")
    private String biomechanicalPattern;
    @SerializedName("temporal_evolution")
    private String temporalEvolution;
    @SerializedName("contributing_factors")
    private List<String> contributingFactors;

    // Bloco 9: Plano de Tratamento
    @SerializedName("therapeutic_strategy")
    private String therapeuticStrategy;
    @SerializedName("postural_correction")
    private List<String> posturalCorrection;
    @SerializedName("muscle_chains")
    private List<String> muscleChains;
    @SerializedName("individualized_approach")
    private String individualizedApproach;
    @SerializedName("estimated_sessions")
    private Integer estimatedSessions;
    @SerializedName("treatment_goals")
    private List<String> treatmentGoals;
    @SerializedName("objective_summary")
    private String objectiveSummary;

    // Getters and Setters Fundamentais
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public int getProfessionalId() { return professionalId; }
    public void setProfessionalId(int professionalId) { this.professionalId = professionalId; }
    public Date getEvaluationDate() { return evaluationDate; }
    public void setEvaluationDate(Date evaluationDate) { this.evaluationDate = evaluationDate; }

    // Bloco 1
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }

    // Bloco 2
    public Date getFirstContactDate() { return firstContactDate; }
    public void setFirstContactDate(Date firstContactDate) { this.firstContactDate = firstContactDate; }
    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }
    public String getHealthPlan() { return healthPlan; }
    public void setHealthPlan(String healthPlan) { this.healthPlan = healthPlan; }
    public String getPatientOrigin() { return patientOrigin; }
    public void setPatientOrigin(String patientOrigin) { this.patientOrigin = patientOrigin; }
    public Double getSessionFee() { return sessionFee; }
    public void setSessionFee(Double sessionFee) { this.sessionFee = sessionFee; }
    public List<String> getMedications() { return medications; }
    public void setMedications(List<String> medications) { this.medications = medications; }
    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    // Subclasses
    public static class ImagingExam implements Serializable {
        @SerializedName("exam_type")
        private String examType;
        @SerializedName("exam_date")
        private Date examDate;
        @SerializedName("result")
        private String result;
        @SerializedName("conclusions")
        private String conclusions;
        
        public String getExamType() { return examType; }
        public void setExamType(String examType) { this.examType = examType; }
    }

    public static class PosturalAssessment implements Serializable {
        @SerializedName("anterior_view")
        private String anteriorView;
        @SerializedName("posterior_view")
        private String posteriorView;
        @SerializedName("lateral_view")
        private String lateralView;
        @SerializedName("observations")
        private String observations;
    }
}
