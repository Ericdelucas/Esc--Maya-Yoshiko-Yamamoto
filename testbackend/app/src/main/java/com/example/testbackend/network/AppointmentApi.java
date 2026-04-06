package com.example.testbackend.network;

import com.example.testbackend.models.AppointmentCreateRequest;
import com.example.testbackend.models.AppointmentListResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AppointmentApi {
    @POST("appointments/")
    Call<Map<String, Object>> createAppointment(
        @Header("Authorization") String token,
        @Body AppointmentCreateRequest request
    );
    
    @GET("appointments/month/{year}/{month}")
    Call<AppointmentListResponse> getAppointmentsByMonth(
        @Header("Authorization") String token,
        @Path("year") int year,
        @Path("month") int month
    );
    
    @GET("appointments/day/{year}/{month}/{day}")
    Call<AppointmentListResponse> getAppointmentsByDate(
        @Header("Authorization") String token,
        @Path("year") int year,
        @Path("month") int month,
        @Path("day") int day
    );
    
    @DELETE("appointments/{appointment_id}")
    Call<Map<String, String>> deleteAppointment(
        @Header("Authorization") String token,
        @Path("appointment_id") int appointmentId
    );
}
