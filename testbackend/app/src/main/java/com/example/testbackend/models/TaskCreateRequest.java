package com.example.testbackend.models;

public class TaskCreateRequest {
    private Integer patient_id;      // OBRIGATÓRIO
    private String title;            // OBRIGATÓRIO
    private String description;      // OBRIGATÓRIO
    private Integer points_value;    // OPCIONAL (default=10)
    private Integer exercise_id;     // OPCIONAL (pode ser null)
    private Integer frequency_per_week; // OPCIONAL (default=1)
    private String start_date;       // OBRIGATÓRIO - formato "YYYY-MM-DD"
    private String end_date;        // OPCIONAL (pode ser null)
    private String exercise_image_url;  // OPCIONAL - URL da imagem
    private String exercise_video_url;  // OPCIONAL - URL do vídeo
    
    // Getters e Setters
    public Integer getPatient_id() { return patient_id; }
    public void setPatient_id(Integer patient_id) { this.patient_id = patient_id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getPoints_value() { return points_value; }
    public void setPoints_value(Integer points_value) { this.points_value = points_value; }
    
    public Integer getExercise_id() { return exercise_id; }
    public void setExercise_id(Integer exercise_id) { this.exercise_id = exercise_id; }
    
    public Integer getFrequency_per_week() { return frequency_per_week; }
    public void setFrequency_per_week(Integer frequency_per_week) { this.frequency_per_week = frequency_per_week; }
    
    public String getStart_date() { return start_date; }
    public void setStart_date(String start_date) { this.start_date = start_date; }
    
    public String getEnd_date() { return end_date; }
    public void setEnd_date(String end_date) { this.end_date = end_date; }
    
    public String getExercise_image_url() { return exercise_image_url; }
    public void setExercise_image_url(String exercise_image_url) { this.exercise_image_url = exercise_image_url; }
    
    public String getExercise_video_url() { return exercise_video_url; }
    public void setExercise_video_url(String exercise_video_url) { this.exercise_video_url = exercise_video_url; }

    // Compatibility methods for camelCase if needed
    public Integer getPatientId() { return patient_id; }
    public void setPatientId(Integer id) { this.patient_id = id; }
    public Integer getPointsValue() { return points_value; }
    public void setPointsValue(Integer val) { this.points_value = val; }
    public Integer getFrequencyPerWeek() { return frequency_per_week; }
    public void setFrequencyPerWeek(Integer val) { this.frequency_per_week = val; }
    public String getStartDate() { return start_date; }
    public void setStartDate(String date) { this.start_date = date; }
    
    public String getExerciseImageUrl() { return exercise_image_url; }
    public void setExerciseImageUrl(String url) { this.exercise_image_url = url; }
    
    public String getExerciseVideoUrl() { return exercise_video_url; }
    public void setExerciseVideoUrl(String url) { this.exercise_video_url = url; }
}
