package com.example.testbackend.network;

import com.example.testbackend.models.ChangePasswordRequest;
import com.example.testbackend.models.DashboardStats;
import com.example.testbackend.models.FileUploadResponse;
import com.example.testbackend.models.LoginRequest;
import com.example.testbackend.models.LoginResponse;
import com.example.testbackend.models.LoginStatus;
import com.example.testbackend.models.RegisterRequest;
import com.example.testbackend.models.UserProfileResponse;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface AuthApi {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<ResponseBody> register(@Body RegisterRequest registerRequest);

    @PUT("auth/change-password")
    Call<ResponseBody> changePassword(
        @Header("Authorization") String token,
        @Body ChangePasswordRequest request
    );

    @GET("auth/me")
    Call<UserProfileResponse> getProfile(@Header("Authorization") String token);

    @Multipart
    @POST("auth/profile/photo")
    Call<FileUploadResponse> uploadProfilePhoto(
        @Header("Authorization") String token,
        @Part MultipartBody.Part file
    );

    @GET("professional/dashboard-stats")
    Call<DashboardStats> getDashboardStats(@Header("Authorization") String token);

    // 🔥 NOVO: Endpoint para verificar o status de login (Rate Limiting)
    @GET("auth/login-status/{email}")
    Call<LoginStatus> getLoginStatus(@Path("email") String email);
}
