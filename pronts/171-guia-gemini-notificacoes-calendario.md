# 🎯 **GUIA GEMINI - IMPLEMENTAR NOTIFICAÇÕES DE CALENDÁRIO NO FRONTEND**

## 📋 **BACKEND JÁ ESTÁ PRONTO!**

### **✅ O que já foi implementado:**
- **AppointmentNotificationService** - agenda notificações automaticamente
- **Integração** com criação de consultas (já agenda lembretes)
- **Endpoint** `/appointments/send-daily-notifications` para resumo diário
- **Notificações agendadas:** 1 dia antes + 1 hora antes

---

## 🎯 **O QUE GEMINI PRECISA FAZER NO FRONTEND**

### **📋 Passo 1 - Adicionar permissões no AndroidManifest.xml**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/AndroidManifest.xml`

**Adicionar DENTRO de <manifest> (antes de <application>):**
```xml
<!-- 🔥 PERMISSÕES PARA NOTIFICAÇÕES -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

---

### **📋 Passo 2 - Solicitar permissão em tempo de execução**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/MainActivity.java`

**Adicionar no onCreate():**
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    // 🔥 SOLICITAR PERMISSÃO DE NOTIFICAÇÃO
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
            != PackageManager.PERMISSION_GRANTED) {
            
            ActivityCompat.requestPermissions(
                this, 
                new String[]{Manifest.permission.POST_NOTIFICATIONS}, 
                100
            );
        }
    }
}

@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    
    if (requestCode == 100) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Notificações permitidas!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Notificações negadas", Toast.LENGTH_SHORT).show();
        }
    }
}
```

---

### **📋 Passo 3 - Criar NotificationHelper**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/NotificationHelper.java`

```java
package com.example.testbackend;

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
        
        // Intent para abrir app quando clicar
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Criar notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "appointments")
            .setSmallIcon(R.drawable.ic_calendar)  // 🔥 PRECISA CRIAR ÍCONE
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)  // Som + vibração
            .setContentIntent(pendingIntent)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        
        // Mostrar notificação
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
    
    public static void showDailySummary(Context context, int appointmentCount, String firstTime) {
        String title = "📅 Suas Consultas de Hoje";
        String message = "Você tem " + appointmentCount + " consulta(s) hoje. Primeira às " + firstTime;
        
        showAppointmentNotification(context, title, message);
    }
}
```

---

### **📋 Passo 4 - Criar ícone de calendário**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/drawable/ic_calendar.xml`

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?attr/colorOnSurface">
  <path
      android:fillColor="@android:color/white"
      android:pathData="M19,3h-1V1h-2v2H8V1H6v2H5c-1.11,0 -1.99,0.9 -1.99,2L3,19c0,1.1 0.89,2 2,2h14c1.1,0 2,-0.9 2,-2V5c0,-1.1 -0.9,-2 -2,-2zM19,19H5V8h14v11zM7,10h5v5H7z"/>
</vector>
```

---

### **📋 Passo 5 - Criar API de notificações**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/network/NotificationApi.java`

```java
package com.example.testbackend.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import java.util.List;

public class NotificationApi {
    
    @GET("notifications/pending")
    Call<List<NotificationResponse>> getPendingNotifications();
    
    @POST("notifications/mark-read")
    Call<Void> markNotificationAsRead(int notificationId);
    
    // Modelo de resposta
    public static class NotificationResponse {
        public int id;
        public String title;
        public String message;
        public String created_at;
        public boolean read;
    }
}
```

---

### **📋 Passo 6 - Serviço para buscar notificações**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/services/NotificationService.java`

```java
package com.example.testbackend.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
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
                            // 🔥 MOSTRAR NOTIFICAÇÃO NO CELULAR
                            NotificationHelper.showAppointmentNotification(
                                context,
                                notification.title,
                                notification.message
                            );
                            
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
}
```

---

### **📋 Passo 7 - Integrar nas Activities principais**

**Na ProfessionalMainActivity.java:**
```java
private NotificationService notificationService;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_professional_main);
    
    // 🔥 INICIAR SERVIÇO DE NOTIFICAÇÕES
    notificationService = new NotificationService(this);
    notificationService.startPolling();
    
    // Resto do código...
}

@Override
protected void onDestroy() {
    super.onDestroy();
    
    // 🔥 PARAR SERVIÇO DE NOTIFICAÇÕES
    if (notificationService != null) {
        notificationService.stopPolling();
    }
}
```

**Na MainActivity.java (paciente):**
```java
private NotificationService notificationService;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    // 🔥 INICIAR SERVIÇO DE NOTIFICAÇÕES
    notificationService = new NotificationService(this);
    notificationService.startPolling();
    
    // Resto do código...
}

@Override
protected void onDestroy() {
    super.onDestroy();
    
    // 🔥 PARAR SERVIÇO DE NOTIFICAÇÕES
    if (notificationService != null) {
        notificationService.stopPolling();
    }
}
```

---

## 🎮 **COMO TESTAR**

### **✅ Teste 1 - Criar consulta:**
1. **Criar consulta** para amanhã
2. **Verificar logs** do backend: "✅ Notificações agendadas"
3. **Verificar tabela** notifications no banco

### **✅ Teste 2 - Polling de notificações:**
1. **Abrir app** e esperar 5 minutos
2. **Verificar logs** "Polling de notificações iniciado"
3. **Deve aparecer** notificação se houver pendentes

### **✅ Teste 3 - Permissões:**
1. **Instalar app** e permitir notificações
2. **Criar consulta** e aguardar
3. **Notificação deve aparecer** no celular

---

## 🚨 **IMPORTANTE**

### **🎯 Não esquecer:**
1. ✅ **Adicionar permissões** no AndroidManifest.xml
2. ✅ **Criar NotificationHelper** com canal de notificação
3. ✅ **Criar ícone** ic_calendar.xml
4. ✅ **Implementar polling** a cada 5 minutos
5. ✅ **Iniciar/parar serviço** nas activities

### **🎯 Resultado esperado:**
- **📱 Notificações aparecem** igual WhatsApp
- **🔔 Barra de status** com ícone
- **📅 Resumo diário** às 8:00 AM
- **⏰ Lembretes** 1 dia antes + 1 hora antes

**Backend está 100% pronto! Só falta implementar frontend! 🎯**
