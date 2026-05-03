package com.example.testbackend;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    
    public static void showAppointmentNotification(Context context, String title, String message) {
        NotificationManager notificationManager = 
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        // Criar canal (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                "appointments",
                "Consultas e Agendamentos",
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificações de consultas médicas");
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
        }
        
        // Intent para abrir o app ao clicar na notificação
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Criar notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "appointments")
            .setSmallIcon(R.drawable.ic_calendar) // 🔥 Usando o ícone de calendário
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        
        // Mostrar notificação com ID único
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
    
    // Método alternativo para compatibilidade se necessário
    public static void showNotification(Context context, String title, String message) {
        showAppointmentNotification(context, title, message);
    }
    
    public static void showDailySummary(Context context, int appointmentCount, String firstTime) {
        String title = "📅 Suas Consultas de Hoje";
        String message = "Você tem " + appointmentCount + " consulta(s) hoje. Primeira às " + firstTime;
        
        showAppointmentNotification(context, title, message);
    }
}
