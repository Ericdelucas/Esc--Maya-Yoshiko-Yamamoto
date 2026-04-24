package com.example.testbackend.network;

import com.example.testbackend.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;
import android.util.Log;

public class ApiClient {

    private static Retrofit authRetrofit = null;
    private static Retrofit aiRetrofit = null;
    private static Retrofit exerciseRetrofit = null;
    private static Retrofit healthRetrofit = null;
    private static Retrofit appointmentRetrofit = null;
    private static Retrofit taskRetrofit = null;
    private static Retrofit patientRetrofit = null;

    private static Gson getGson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .setLenient()
                .create();
    }

    private static OkHttpClient getOkHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(chain -> {
                    okhttp3.Request request = chain.request();
                    long startTime = System.currentTimeMillis();
                    try {
                        okhttp3.Response response = chain.proceed(request);
                        long endTime = System.currentTimeMillis();
                        Log.d("NETWORK_DEBUG", "URL: " + request.url() + " | Time: " + (endTime - startTime) + "ms | Code: " + response.code());
                        return response;
                    } catch (Exception e) {
                        Log.e("NETWORK_DEBUG", "URL: " + request.url() + " | Error: " + e.getMessage());
                        throw e;
                    }
                })
                .build();
    }

    public static Retrofit getAuthClient() {
        if (authRetrofit == null) {
            authRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.AUTH_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .client(getOkHttpClient())
                    .build();
        }
        return authRetrofit;
    }

    public static Retrofit getAppointmentClient() {
        if (appointmentRetrofit == null) {
            appointmentRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.PACIENTES_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .client(getOkHttpClient())
                    .build();
        }
        return appointmentRetrofit;
    }

    public static Retrofit getHealthClient() {
        if (healthRetrofit == null) {
            healthRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.HEALTH_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .client(getOkHttpClient())
                    .build();
        }
        return healthRetrofit;
    }

    public static Retrofit getAiClient() {
        if (aiRetrofit == null) {
            aiRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.AUTH_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .client(getOkHttpClient())
                    .build();
        }
        return aiRetrofit;
    }

    public static Retrofit getExerciseClient() {
        if (exerciseRetrofit == null) {
            exerciseRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.EXERCISE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .client(getOkHttpClient())
                    .build();
        }
        return exerciseRetrofit;
    }

    public static Retrofit getTaskClient() {
        if (taskRetrofit == null) {
            taskRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.AUTH_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .client(getOkHttpClient())
                    .build();
        }
        return taskRetrofit;
    }

    public static Retrofit getPatientClient() {
        if (patientRetrofit == null) {
            patientRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.AUTH_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .client(getOkHttpClient())
                    .build();
        }
        return patientRetrofit;
    }
}
