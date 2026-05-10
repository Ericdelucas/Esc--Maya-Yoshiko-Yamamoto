package com.example.testbackend.network;

import com.example.testbackend.models.ChartDataResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ChartDataApi {
    
    @GET("reports/chart-data")
    Call<ChartDataResponse> getChartData(@Header("Authorization") String token, @Query("professional_id") int professionalId);
}
