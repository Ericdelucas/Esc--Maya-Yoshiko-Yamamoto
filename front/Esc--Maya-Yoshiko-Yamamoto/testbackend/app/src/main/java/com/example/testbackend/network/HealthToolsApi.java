package com.example.testbackend.network;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HealthToolsApi {
    
    @POST("health-tools/calculate-bmi")
    Call<BMIResponse> calculateBMI(@Header("Authorization") String token, @Body BMICalculationRequest request);
    
    @POST("health-tools/calculate-bmi-test")
    Call<BMIResponse> calculateBMITest(@Body BMICalculationRequest request);
    
    @POST("health-tools/calculate-body-fat")
    Call<BodyFatResponse> calculateBodyFat(@Header("Authorization") String token, @Body BodyFatCalculationRequest request);

    // ✅ ADICIONADO ENDPOINT DE TESTE PARA GORDURA CORPORAL
    @POST("health-tools/calculate-body-fat-test")
    Call<BodyFatResponse> calculateBodyFatTest(@Body BodyFatCalculationRequest request);
    
    @POST("health-tools/save-questionnaire")
    Call<QuestionnaireResponse> saveQuestionnaire(@Header("Authorization") String token, @Body QuestionnaireRequest request);
    
    @POST("health-tools/save-questionnaire-test")
    Call<QuestionnaireResponse> saveQuestionnaireTest(@Body QuestionnaireRequest request);
    
    @GET("health-tools/summary")
    Call<HealthSummaryResponse> getHealthSummary(@Header("Authorization") String token);
    
    @GET("health-tools/bmi-history")
    Call<BMIHistoryResponse> getBMIHistory(@Header("Authorization") String token, @Query("limit") int limit);

    // Request Classes
    class BMICalculationRequest {
        public float height;
        public float weight;
        public BMICalculationRequest(float h, float w) { this.height = h; this.weight = w; }
    }

    class BodyFatCalculationRequest {
        public float height;
        public float weight;
        public int age;
        public String gender;
    }

    class QuestionnaireRequest {
        public List<Answer> answers;
    }

    class Answer {
        public String question_id;
        public String answer;
    }

    // Response Classes
    class BMIResponse {
        public boolean success;
        public BMIData data;
    }

    class BMIData {
        public float bmi;
        public String category;
        public String created_at;
    }

    class BodyFatResponse {
        public boolean success;
        public BodyFatData data;
    }

    class BodyFatData {
        public float body_fat_percentage;
        public String category;
    }

    class QuestionnaireResponse {
        public boolean success;
        public int score;
        public String risk_level;
    }

    class HealthSummaryResponse {
        public BMIData latest_bmi;
        public BodyFatData latest_body_fat;
        public String risk_level;
    }

    class BMIHistoryResponse {
        public List<BMIData> history;
    }
}
