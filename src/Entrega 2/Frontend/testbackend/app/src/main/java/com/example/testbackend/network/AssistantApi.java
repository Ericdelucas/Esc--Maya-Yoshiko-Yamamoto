package com.example.testbackend.network;

import com.example.testbackend.models.AssistantRequest;
import com.example.testbackend.models.AssistantResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AssistantApi {
    @POST("ai/chat")
    Call<AssistantResponse> chat(@Body AssistantRequest request);
}