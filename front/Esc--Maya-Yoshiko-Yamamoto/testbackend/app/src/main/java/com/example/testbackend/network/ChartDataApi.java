package com.example.testbackend.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ChartDataApi {
    
    @GET("reports/chart-data")
    Call<ChartDataResponse> getChartData(@Query("professional_id") int professionalId);
}
