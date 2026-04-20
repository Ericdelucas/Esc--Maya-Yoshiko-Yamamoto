package com.example.testbackend.network;

import com.example.testbackend.models.PatientReport;
import com.example.testbackend.models.ReportCreate;
import com.example.testbackend.models.ReportStatistics;
import com.example.testbackend.models.ReportUpdate;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PatientReportApi {
    @GET("reports/professional/{professionalId}")
    Call<List<PatientReport>> getProfessionalReports(@Path("professionalId") int professionalId);

    @POST("reports/")
    Call<PatientReport> createReport(@Body ReportCreate report);

    @GET("reports/{reportId}")
    Call<PatientReport> getReport(@Path("reportId") int reportId);

    @PUT("reports/{reportId}")
    Call<PatientReport> updateReport(@Path("reportId") int reportId, @Body ReportUpdate report);

    @DELETE("reports/{reportId}")
    Call<Void> deleteReport(@Path("reportId") int reportId);

    @GET("reports/patient/{patientId}")
    Call<List<PatientReport>> getPatientReports(@Path("patientId") int patientId);

    @GET("reports/statistics")
    Call<ReportStatistics> getStatistics(@Query("professional_id") int professionalId);

    @GET("reports/search/patient")
    Call<List<PatientReport>> searchReports(@Query("name") String name);
}
