package com.example.testbackend.network;

import com.example.testbackend.models.Exercise;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ExerciseApi {
    @GET("exercises")
    Call<List<Exercise>> getExercises(@Header("Authorization") String token);
}