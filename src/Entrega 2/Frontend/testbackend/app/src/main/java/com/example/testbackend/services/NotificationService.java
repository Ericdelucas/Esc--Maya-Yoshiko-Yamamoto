package com.example.testbackend.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.example.testbackend.NotificationHelper;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.NotificationApi;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService {
    
    private static final String TAG = "NotificationService";
    private static final long POLLING_INTERVAL = 300000; // 5 minutos
    
    private Handler handler;
    private Runnable pollingRunnable;
    private Context context;
    
    public NotificationService(Context context) {
        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());
    }
    
    public void startPolling() {
        stopPolling(); // Parar polling anterior se existir
        
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                checkNotifications();
                handler.postDelayed(this, POLLING_INTERVAL);
            }
        };
        
        handler.post(pollingRunnable);
        Log.d(TAG, "Polling de notificações iniciado");
    }
    
    public void stopPolling() {
        if (handler != null && pollingRunnable != null) {
            handler.removeCallbacks(pollingRunnable);
            Log.d(TAG, "Polling de notificações parado");
        }
    }
    
    private void checkNotifications() {
        try {
            NotificationApi api = ApiClient.getAuthClient().create(NotificationApi.class);
            
            api.getPendingNotifications().enqueue(new Callback<List<NotificationApi.NotificationResponse>>() {
                @Override
                public void onResponse(Call<List<NotificationApi.NotificationResponse>> call, 
                                     Response<List<NotificationApi.NotificationResponse>> response) {
                    
                    if (response.isSuccessful() && response.body() != null) {
                        List<NotificationApi.NotificationResponse> notifications = response.body();
                        
                        for (NotificationApi.NotificationResponse notification : notifications) {
                            // MOSTRAR NOTIFICAÇÃO NO CELULAR
                            NotificationHelper.showAppointmentNotification(
                                context,
                                notification.title,
                                notification.message
                            );
                            
                            // MARCAR COMO LIDA NO BACKEND
                            markAsRead(notification.id);
                            
                            Log.d(TAG, "Notificação mostrada: " + notification.title);
                        }
                    }
                }
                
                @Override
                public void onFailure(Call<List<NotificationApi.NotificationResponse>> call, Throwable t) {
                    Log.e(TAG, "Erro ao buscar notificações: " + t.getMessage());
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Erro no polling: " + e.getMessage());
        }
    }

    private void markAsRead(int notificationId) {
        NotificationApi api = ApiClient.getAuthClient().create(NotificationApi.class);
        api.markNotificationAsRead(notificationId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Notificação " + notificationId + " marcada como lida no servidor");
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Falha ao marcar como lida: " + t.getMessage());
            }
        });
    }
}
