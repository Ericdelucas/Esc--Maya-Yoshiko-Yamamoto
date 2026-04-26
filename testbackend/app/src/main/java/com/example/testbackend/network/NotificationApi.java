package com.example.testbackend.network;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NotificationApi {
    
    @GET("notifications/pending")
    Call<List<NotificationResponse>> getPendingNotifications();
    
    @POST("notifications/mark-read/{notificationId}")
    Call<Void> markNotificationAsRead(@Path("notificationId") int notificationId);
    
    // Modelo de resposta sincronizado com o Backend
    public static class NotificationResponse {
        public int id;
        public String title;
        public String message;
        public String created_at;
        public boolean read;
    }
}
