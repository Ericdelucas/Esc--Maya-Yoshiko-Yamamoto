package com.example.testbackend.network;

import com.example.testbackend.models.ProgressResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface AnalyticsApi {
    @GET("analytics/progress")
    Call<ProgressResponse> getProgress(@Header("Authorization") String token);
}