package com.example.testbackend.network;

import com.example.testbackend.models.Patient;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PatientApi {

    class PacientesResponse {
        public boolean success;
        public List<Patient> pacientes;
        public int total;
        public String message;
    }

    @GET("pacientes")
    Call<PacientesResponse> getPacientes();

    @POST("pacientes")
    Call<Map<String, Object>> createPaciente(@Body Map<String, Object> pacienteData);
}
