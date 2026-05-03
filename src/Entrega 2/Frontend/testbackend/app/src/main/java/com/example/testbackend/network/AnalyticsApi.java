package com.example.testbackend.network;

import com.example.testbackend.models.ProgressResponse;
import com.example.testbackend.models.ProfessionalDashboardResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface AnalyticsApi {
    @GET("analytics/progress")
    Call<ProgressResponse> getProgress(@Header("Authorization") String token);

    // 🔥 NOVO: Dashboard do Profissional
    @GET("analytics/professional/dashboard")
    Call<ProfessionalDashboardResponse> getProfessionalDashboard(@Header("Authorization") String token);
}
