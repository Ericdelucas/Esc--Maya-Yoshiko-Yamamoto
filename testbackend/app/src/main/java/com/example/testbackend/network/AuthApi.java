package com.example.testbackend.network;

import com.example.testbackend.models.LoginRequest;
import com.example.testbackend.models.LoginResponse;
import com.example.testbackend.models.RegisterRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<ResponseBody> register(@Body RegisterRequest registerRequest);
}