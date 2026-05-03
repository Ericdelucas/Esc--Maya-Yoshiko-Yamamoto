package com.example.testbackend.network;

import com.example.testbackend.models.PatientEvaluation;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EvaluationApi {
    
    @GET("evaluations/patient/{patientId}")
    Call<List<PatientEvaluation>> getEvaluationsByPatient(@Path("patientId") int patientId);
    
    @GET("evaluations/{evaluationId}")
    Call<PatientEvaluation> getEvaluationById(@Path("evaluationId") int evaluationId);
    
    @POST("evaluations")
    Call<PatientEvaluation> createEvaluation(@Body PatientEvaluation evaluation);
    
    @PUT("evaluations/{evaluationId}")
    Call<PatientEvaluation> updateEvaluation(@Path("evaluationId") int evaluationId, @Body PatientEvaluation evaluation);
    
    @DELETE("evaluations/{evaluationId}")
    Call<Void> deleteEvaluation(@Path("evaluationId") int evaluationId);
    
    @GET("evaluations/professional/{professionalId}")
    Call<List<PatientEvaluation>> getEvaluationsByProfessional(@Path("professionalId") int professionalId);
}
