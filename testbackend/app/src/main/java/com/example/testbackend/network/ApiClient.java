package com.example.testbackend.network;

import com.example.testbackend.utils.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit authRetrofit = null;
    private static Retrofit aiRetrofit = null;
    private static Retrofit exerciseRetrofit = null;

    public static Retrofit getAuthClient() {
        if (authRetrofit == null) {
            authRetrofit = buildRetrofit(Constants.AUTH_BASE_URL);
        }
        return authRetrofit;
    }

    public static Retrofit getAiClient() {
        if (aiRetrofit == null) {
            // Note: For AI, we might need a different base depending on the endpoint structure
            // but providing a consistent one for now
            aiRetrofit = buildRetrofit("http://" + Constants.HOST + ":8090/");
        }
        return aiRetrofit;
    }

    public static Retrofit getExerciseClient() {
        if (exerciseRetrofit == null) {
            exerciseRetrofit = buildRetrofit(Constants.EXERCISE_BASE_URL);
        }
        return exerciseRetrofit;
    }

    private static Retrofit buildRetrofit(String baseUrl) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
}