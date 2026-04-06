# 🎯 INSTRUÇÕES GEMINI - CALENDÁRIO COM BANCO DE DADOS

## 📋 **OBJETIVO**

Fazer o calendário funcionar com dados reais do banco de dados, sem dados mock/ilusórios.

## 🔧 **BACKEND JÁ ESTÁ PRONTO**

✅ **Tabela appointments criada**  
✅ **Endpoints REST funcionando**  
✅ **Repository completo**  
✅ **Integração com autenticação**  

## 📱 **O QUE O GEMINI PRECISA FAZER NO FRONTEND**

### **1. Criar AppointmentApi.java**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/network/AppointmentApi.java`

```java
package com.example.testbackend.network;

import retrofit2.Call;
import retrofit2.http.*;
import java.util.Map;
import java.util.List;

public interface AppointmentApi {
    @POST("appointments/")
    Call<Map<String, Object>> createAppointment(
        @Header("Authorization") String token,
        @Body AppointmentCreateRequest request
    );
    
    @GET("appointments/month/{year}/{month}")
    Call<AppointmentListResponse> getAppointmentsByMonth(
        @Header("Authorization") String token,
        @Path("year") int year,
        @Path("month") int month
    );
    
    @GET("appointments/day/{year}/{month}/{day}")
    Call<AppointmentListResponse> getAppointmentsByDate(
        @Header("Authorization") String token,
        @Path("year") int year,
        @Path("month") int month,
        @Path("day") int day
    );
    
    @DELETE("appointments/{appointment_id}")
    Call<Map<String, String>> deleteAppointment(
        @Header("Authorization") String token,
        @Path("appointment_id") int appointmentId
    );
}
```

### **2. Criar Models para API**

**Arquivo:** `front/.../models/AppointmentCreateRequest.java`

```java
package com.example.testbackend.models;

public class AppointmentCreateRequest {
    private String title;
    private String description;
    private String appointmentDate;  // yyyy-MM-dd format
    private String time;  // HH:MM format
    private Integer patientId;
    
    public AppointmentCreateRequest() {}
    
    // Getters e Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }
    
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    
    public Integer getPatientId() { return patientId; }
    public void setPatientId(Integer patientId) { this.patientId = patientId; }
}
```

**Arquivo:** `front/.../models/AppointmentListResponse.java`

```java
package com.example.testbackend.models;

import java.util.List;
import java.util.Map;

public class AppointmentListResponse {
    private List<Map<String, Object>> appointments;
    
    public List<Map<String, Object>> getAppointments() {
        return appointments;
    }
    
    public void setAppointments(List<Map<String, Object>> appointments) {
        this.appointments = appointments;
    }
}
```

### **3. Modificar CalendarActivity.java**

**Arquivo:** `front/.../CalendarActivity.java`

**Adicionar imports:**
```java
import com.example.testbackend.network.AppointmentApi;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.models.AppointmentCreateRequest;
import com.example.testbackend.models.AppointmentListResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
```

**Substituir método `loadAppointments()`:**
```java
private void loadAppointments() {
    String token = tokenManager.getAuthToken();
    if (token != null) {
        AppointmentApi api = ApiClient.getAuthClient().create(AppointmentApi.class);
        
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        
        api.getAppointmentsByMonth(token, year, month).enqueue(new Callback<AppointmentListResponse>() {
            @Override
            public void onResponse(Call<AppointmentListResponse> call, Response<AppointmentListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Appointment> apiAppointments = convertToAppointmentList(response.body().getAppointments());
                    appointments.clear();
                    appointments.addAll(apiAppointments);
                    calendarAdapter.updateAppointments(appointments);
                    Log.d(TAG, "✅ " + apiAppointments.size() + " agendamentos carregados do banco");
                } else {
                    Log.e(TAG, "❌ Erro ao carregar agendamentos: " + response.code());
                    Toast.makeText(CalendarActivity.this, "Erro ao carregar agendamentos", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<AppointmentListResponse> call, Throwable t) {
                Log.e(TAG, "❌ Falha na conexão", t);
                Toast.makeText(CalendarActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

private List<Appointment> convertToAppointmentList(List<Map<String, Object>> apiAppointments) {
    List<Appointment> appointments = new ArrayList<>();
    for (Map<String, Object> apt : apiAppointments) {
        try {
            // Converter data
            String dateStr = (String) apt.get("appointment_date");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            
            appointments.add(new Appointment(
                (Integer) apt.get("id"),
                (String) apt.get("title"),
                date,
                (String) apt.get("description")
            ));
        } catch (Exception e) {
            Log.e(TAG, "Erro ao converter agendamento: " + e.getMessage());
        }
    }
    return appointments;
}
```

**Substituir método `openAddAppointmentDialog()`:**
```java
private void openAddAppointmentDialog() {
    AddAppointmentDialog dialog = new AddAppointmentDialog(this, appointment -> {
        // Salvar na API em vez de lista local
        saveAppointmentToAPI(appointment);
    });
    dialog.show();
}

private void saveAppointmentToAPI(Appointment appointment) {
    String token = tokenManager.getAuthToken();
    if (token != null) {
        AppointmentApi api = ApiClient.getAuthClient().create(AppointmentApi.class);
        
        AppointmentCreateRequest request = new AppointmentCreateRequest();
        request.setTitle(appointment.getTitle());
        request.setDescription(appointment.getDescription());
        
        // Formatar data para ISO
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        request.setAppointmentDate(sdf.format(appointment.getDate()));
        request.setTime("14:30");  // Default time
        
        api.createAppointment(token, request).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "✅ Agendamento salvo no banco");
                    Toast.makeText(CalendarActivity.this, "Agendamento salvo", Toast.LENGTH_SHORT).show();
                    loadAppointments();  // Recarregar do banco
                } else {
                    Log.e(TAG, "❌ Erro ao salvar agendamento: " + response.code());
                    Toast.makeText(CalendarActivity.this, "Erro ao salvar agendamento", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "❌ Falha ao salvar", t);
                Toast.makeText(CalendarActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```

**Adicionar método para carregar mês específico:**
```java
private void loadAppointmentsForMonth(int year, int month) {
    String token = tokenManager.getAuthToken();
    if (token != null) {
        AppointmentApi api = ApiClient.getAuthClient().create(AppointmentApi.class);
        
        api.getAppointmentsByMonth(token, year, month).enqueue(new Callback<AppointmentListResponse>() {
            @Override
            public void onResponse(Call<AppointmentListResponse> call, Response<AppointmentListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Appointment> apiAppointments = convertToAppointmentList(response.body().getAppointments());
                    appointments.clear();
                    appointments.addAll(apiAppointments);
                    calendarAdapter.updateAppointments(appointments);
                }
            }
            
            @Override
            public void onFailure(Call<AppointmentListResponse> call, Throwable t) {
                Log.e(TAG, "Falha ao carregar mês", t);
            }
        });
    }
}
```

**Atualizar métodos de navegação:**
```java
private void previousMonth() {
    currentCalendar.add(Calendar.MONTH, -1);
    updateMonthYear();
    calendarAdapter.updateCalendar(currentCalendar);
    loadAppointmentsForMonth(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH));
}

private void nextMonth() {
    currentCalendar.add(Calendar.MONTH, 1);
    updateMonthYear();
    calendarAdapter.updateCalendar(currentCalendar);
    loadAppointmentsForMonth(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH));
}
```

**Remover método `generateMockAppointments()` completamente!**

### **4. Atualizar DayAppointmentsDialog (se existir)**

**Para buscar agendamentos do dia específico:**
```java
private void loadDayAppointments() {
    String token = tokenManager.getAuthToken();
    if (token != null) {
        AppointmentApi api = ApiClient.getAuthClient().create(AppointmentApi.class);
        
        api.getAppointmentsByDate(token, year, month, day).enqueue(new Callback<AppointmentListResponse>() {
            @Override
            public void onResponse(Call<AppointmentListResponse> call, Response<AppointmentListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Appointment> dayAppointments = convertToAppointmentList(response.body().getAppointments());
                    adapter.updateAppointments(dayAppointments);
                }
            }
            
            @Override
            public void onFailure(Call<AppointmentListResponse> call, Throwable t) {
                Log.e(TAG, "Falha ao carregar dia", t);
            }
        });
    }
}
```

## 🧪 **COMO TESTAR**

### **1. Compilar e executar**
- Build sem erros
- Abrir app

### **2. Testar fluxo completo**
1. **Login como profissional**
2. **Clicar em "Agenda"**
3. **Verificar se carrega agendamentos reais** (vazio no início)
4. **Criar novo agendamento** → Salva no banco
5. **Navegar meses** → Mostra agendamentos corretos
6. **Logout/Login** → Dados persistem

### **3. Verificar logs**
- ✅ "X agendamentos carregados do banco"
- ✅ "Agendamento salvo no banco"
- ❌ Sem erros de conexão

## 🚨 **IMPORTANTE**

1. **Remover todos os dados mock** - `generateMockAppointments()`
2. **Usar apenas API real** - Sem dados hardcoded
3. **Testar persistência** - Logout/login deve manter dados
4. **Verificar tokens** - Usar tokenManager.getAuthToken()

## 📊 **RESULTADO ESPERADO**

```
✅ Calendário com dados reais do banco
✅ Criação de agendamentos salva no banco
✅ Persistência após logout/login
✅ Navegação entre meses com dados corretos
✅ Sem dados mock/ilusórios
```

---

**Status:** 🔄 **AGUARDANDO IMPLEMENTAÇÃO DA INTEGRAÇÃO COM BANCO**
