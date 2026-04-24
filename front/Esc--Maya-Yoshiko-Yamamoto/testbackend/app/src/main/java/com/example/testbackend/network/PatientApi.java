package com.example.testbackend.network;

import com.example.testbackend.models.Patient;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface PatientApi {
    @GET("professional/pacientes")
    Call<List<Patient>> getPatients(@Header("Authorization") String token);
}
