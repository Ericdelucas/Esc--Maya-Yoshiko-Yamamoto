package com.example.testbackend.network;

import com.example.testbackend.models.Patient;
import com.example.testbackend.models.PatientHealthResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PatientApi {
    @GET("professional/pacientes")
    Call<List<Patient>> getPatients(@Header("Authorization") String token);
    
    @GET("health-tools/bmi-history-test")
    Call<String> getBMIHistoryTest(@Query("user_id") int userId);
    
    @GET("health-tools/questionnaire-history-test")
    Call<String> getQuestionnaireHistoryTest(@Query("user_id") int userId);
    
    @DELETE("professional/pacientes/{patient_id}/test")
    Call<Void> deletePatient(@Header("Authorization") String token, @Path("patient_id") int patientId);
}
