package com.example.testbackend.network;

import com.example.testbackend.models.Exercise;
import com.example.testbackend.models.FileUploadResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ExerciseApi {
    
    @GET("exercises")
    Call<List<Exercise>> getExercises(@Header("Authorization") String token);

    @GET("exercises/{id}")
    Call<Exercise> getExerciseById(@Header("Authorization") String token, @Path("id") int id);

    @POST("exercises")
    Call<Exercise> createExercise(@Header("Authorization") String token, @Body Exercise exercise);

    @Multipart
    @POST("exercises/upload/image")
    Call<FileUploadResponse> uploadImage(@Header("Authorization") String token, @Part MultipartBody.Part file);

    @Multipart
    @POST("exercises/upload/video")
    Call<FileUploadResponse> uploadVideo(@Header("Authorization") String token, @Part MultipartBody.Part file);
}