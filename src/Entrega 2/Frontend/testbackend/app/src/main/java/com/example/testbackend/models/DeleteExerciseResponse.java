package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DeleteExerciseResponse {
    @SerializedName("success")
    private Boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("exercise_id")
    private Integer exerciseId;
    
    @SerializedName("deleted_from_patients")
    private List<Integer> deletedFromPatients;
    
    @SerializedName("deleted_by")
    private DeletedByInfo deletedBy;
    
    // Getters
    public Boolean isSuccess() { return success != null ? success : false; }
    public String getMessage() { return message; }
    public Integer getExerciseId() { return exerciseId; }
    public List<Integer> getDeletedFromPatients() { return deletedFromPatients; }
    public DeletedByInfo getDeletedBy() { return deletedBy; }
    
    // Classe aninhada
    public static class DeletedByInfo {
        @SerializedName("id")
        private Integer id;
        
        @SerializedName("role")
        private String role;
        
        @SerializedName("email")
        private String email;
        
        // Getters
        public Integer getId() { return id; }
        public String getRole() { return role; }
        public String getEmail() { return email; }
    }
}
