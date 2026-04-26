package com.example.testbackend.network;

import com.example.testbackend.models.HealthMetricResponse;
import com.example.testbackend.models.IMCRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface HealthApi {
    @POST("health-tools/calculate-bmi-test-query")
    Call<HealthMetricResponse> calculateIMC(
        @Query("user_id") int userId,
        @Query("weight") double weight,
        @Query("height") double height
    );

    @GET("health-tools/history")
    Call<List<HealthMetricResponse>> getHealthHistory(
        @Query("user_id") int userId,
        @Query("metric_type") String metricType
    );

    @POST("health-tools/save-questionnaire-test")
    Call<Map<String, Object>> saveQuestionnaire(
        @Body Map<String, Object> questionnaireData
    );
}
