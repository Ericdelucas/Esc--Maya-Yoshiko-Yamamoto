package com.example.testbackend.network;

import com.example.testbackend.models.PatientEvaluation;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface PatientEvaluationApi {
    
    @GET("evaluations/")
    Call<List<PatientEvaluation>> getAllEvaluations();
    
    @GET("evaluations/patient/{patientId}")
    Call<List<PatientEvaluation>> getEvaluationsByPatient(@Path("patientId") int patientId);
    
    @GET("evaluations/professional/{professionalId}")
    Call<List<PatientEvaluation>> getEvaluationsByProfessional(@Path("professionalId") int professionalId);
    
    @GET("evaluations/{evaluationId}")
    Call<PatientEvaluation> getEvaluationById(@Path("evaluationId") int evaluationId);
    
    @POST("evaluations/")
    Call<PatientEvaluation> createEvaluation(@Body PatientEvaluation evaluation);
    
    @PUT("evaluations/{evaluationId}")
    Call<PatientEvaluation> updateEvaluation(@Path("evaluationId") int evaluationId, @Body PatientEvaluation evaluation);
    
    @DELETE("evaluations/{evaluationId}")
    Call<Void> deleteEvaluation(@Path("evaluationId") int evaluationId);
    
    @GET("evaluations/search/patient")
    Call<List<PatientEvaluation>> searchEvaluationsByPatientName(@Query("name") String name);
}
