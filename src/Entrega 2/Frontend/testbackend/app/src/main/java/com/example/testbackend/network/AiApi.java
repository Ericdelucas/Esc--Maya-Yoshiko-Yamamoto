package com.example.testbackend.network;

import com.example.testbackend.models.AIResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AiApi {
    @Multipart
    @POST("ai/process-frame")
    Call<AIResponse> processFrame(@Part MultipartBody.Part image);
}