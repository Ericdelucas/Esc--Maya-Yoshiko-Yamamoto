package com.example.testbackend.network;

import com.example.testbackend.models.LeaderboardEntry;
import com.example.testbackend.models.PatientTasksResponse;
import com.example.testbackend.models.Task;
import com.example.testbackend.models.TaskCompletion;
import com.example.testbackend.models.TaskCompletionRequest;
import com.example.testbackend.models.TaskCompletionResponse;
import com.example.testbackend.models.TaskCreateRequest;
import com.example.testbackend.models.TestTasksResponse;
import com.example.testbackend.models.UserPoints;
import com.example.testbackend.models.UserPointsResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TaskApi {
    
    // Criar tarefa (profissional)
    @POST("tasks")
    Call<Task> createTask(@Header("Authorization") String token, @Body TaskCreateRequest task);
    
    // Listar tarefas do paciente logado
    @GET("tasks/patient-tasks")
    Call<PatientTasksResponse> getPatientTasks(@Header("Authorization") String token);

    // Listar tarefas de teste (novo endpoint)
    @GET("tasks/test")
    Call<TestTasksResponse> getTestTasks(@Header("Authorization") String token);
    
    // Listar tarefas diárias
    @GET("tasks/patient/{patient_id}/daily")
    Call<List<Task>> getDailyTasks(@Header("Authorization") String token, @Path("patient_id") int patientId);
    
    // Marcar tarefa como concluída (endpoint simplificado para evitar 422)
    @POST("tasks/simple")
    Call<TaskCompletionResponse> completeTask(@Header("Authorization") String token);
    
    // Obter pontos do usuário
    @GET("tasks/user-points")
    Call<UserPointsResponse> getUserPoints(@Header("Authorization") String token);

    // Obter pontos do usuário (ID específico)
    @GET("tasks/points/{user_id}")
    Call<UserPoints> getUserPointsOriginal(@Header("Authorization") String token, @Path("user_id") int userId);
    
    // Obter ranking
    @GET("tasks/leaderboard")
    Call<List<LeaderboardEntry>> getLeaderboard(@Header("Authorization") String token);
}
