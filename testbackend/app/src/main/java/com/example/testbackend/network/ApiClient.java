package com.example.testbackend.network;

import com.example.testbackend.utils.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {

    private static Retrofit authRetrofit = null;
    private static Retrofit aiRetrofit = null;
    private static Retrofit exerciseRetrofit = null;
    private static Retrofit healthRetrofit = null;

    private static OkHttpClient getOkHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    public static Retrofit getAuthClient() {
        if (authRetrofit == null) {
            authRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.AUTH_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getOkHttpClient())
                    .build();
        }
        return authRetrofit;
    }

    public static Retrofit getHealthClient() {
        if (healthRetrofit == null) {
            healthRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.HEALTH_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getOkHttpClient())
                    .build();
        }
        return healthRetrofit;
    }

    public static Retrofit getAiClient() {
        if (aiRetrofit == null) {
            aiRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.AUTH_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getOkHttpClient())
                    .build();
        }
        return aiRetrofit;
    }

    public static Retrofit getExerciseClient() {
        if (exerciseRetrofit == null) {
            exerciseRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.EXERCISE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getOkHttpClient())
                    .build();
        }
        return exerciseRetrofit;
    }
}
