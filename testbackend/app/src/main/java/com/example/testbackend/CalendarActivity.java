package com.example.testbackend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.models.Appointment;
import com.example.testbackend.models.AppointmentCreateRequest;
import com.example.testbackend.models.AppointmentListResponse;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.AppointmentApi;
import com.example.testbackend.utils.TokenManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarActivity extends AppCompatActivity {
    
    private static final String TAG = "CALENDAR_DEBUG";
    private TokenManager tokenManager;
    private RecyclerView rvCalendar;
    private CalendarAdapter calendarAdapter;
    private TextView tvMonthYear;
    private ImageButton btnPrevMonth, btnNextMonth;
    private FloatingActionButton fabAddAppointment;
    
    private List<Appointment> appointments = new ArrayList<>();
    private Calendar currentCalendar;
    private Date selectedDate; // 🔥 Guardar data selecionada
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        
        tokenManager = new TokenManager(this);
        currentCalendar = Calendar.getInstance();
        selectedDate = currentCalendar.getTime(); // Inicializa com hoje
        
        setupViews();
        setupCalendar();
        
        // 🔥 Inicia carregando os dados do mês atual (API espera 1-12)
        loadAppointmentsForMonth(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH) + 1);
    }
    
    private void setupViews() {
        tvMonthYear = findViewById(R.id.tvMonthYear);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        rvCalendar = findViewById(R.id.rvCalendar);
        fabAddAppointment = findViewById(R.id.fabAddAppointment);
        
        btnPrevMonth.setOnClickListener(v -> previousMonth());
        btnNextMonth.setOnClickListener(v -> nextMonth());
        
        fabAddAppointment.setOnClickListener(v -> openAddAppointmentDialog());
    }
    
    private void setupCalendar() {
        calendarAdapter = new CalendarAdapter(this, appointments, this::onDateClick);
        rvCalendar.setLayoutManager(new GridLayoutManager(this, 7));
        rvCalendar.setAdapter(calendarAdapter);
        updateMonthYear();
    }
    
    private void loadAppointmentsForMonth(int year, int month) {
        String token = tokenManager.getAuthToken();
        if (token == null) return;

        Log.d(TAG, "Chamando API para: Month=" + month + ", Year=" + year);
        
        // ✅ CORREÇÃO: Usar getAuthClient() que aponta para a porta correta 8080
        AppointmentApi api = ApiClient.getAuthClient().create(AppointmentApi.class);
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
                    Log.e(TAG, "❌ Erro API (" + response.code() + "): " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<AppointmentListResponse> call, Throwable t) {
                Log.e(TAG, "❌ Falha na conexão", t);
                Toast.makeText(CalendarActivity.this, "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Appointment> convertToAppointmentList(List<Map<String, Object>> apiAppointments) {
        List<Appointment> list = new ArrayList<>();
        if (apiAppointments == null) return list;

        for (Map<String, Object> apt : apiAppointments) {
            try {
                String dateStr = (String) apt.get("appointment_date");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = sdf.parse(dateStr);
                
                int id;
                Object idObj = apt.get("id");
                if (idObj instanceof Double) {
                    id = ((Double) idObj).intValue();
                } else if (idObj instanceof Integer) {
                    id = (Integer) idObj;
                } else {
                    id = 0;
                }
                
                list.add(new Appointment(
                    id,
                    (String) apt.get("title"),
                    date,
                    (String) apt.get("description")
                ));
            } catch (Exception e) {
                Log.e(TAG, "Erro ao converter agendamento: " + e.getMessage());
            }
        }
        return list;
    }
    
    private void previousMonth() {
        currentCalendar.add(Calendar.MONTH, -1);
        updateMonthYear();
        calendarAdapter.updateCalendar(currentCalendar);
        loadAppointmentsForMonth(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH) + 1);
    }
    
    private void nextMonth() {
        currentCalendar.add(Calendar.MONTH, 1);
        updateMonthYear();
        calendarAdapter.updateCalendar(currentCalendar);
        loadAppointmentsForMonth(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH) + 1);
    }
    
    private void updateMonthYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("pt", "BR"));
        String monthYear = sdf.format(currentCalendar.getTime());
        tvMonthYear.setText(monthYear.substring(0, 1).toUpperCase() + monthYear.substring(1));
    }
    
    private void onDateClick(int day, int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        selectedDate = cal.getTime();
        
        DayAppointmentsDialog dialog = new DayAppointmentsDialog(this, day, month, year, appointments);
        dialog.show();
    }
    
    private void openAddAppointmentDialog() {
        AddAppointmentDialog dialog = new AddAppointmentDialog(this, selectedDate, appointment -> {
            saveAppointmentToAPI(appointment);
        });
        dialog.show();
    }

    private void saveAppointmentToAPI(Appointment appointment) {
        String token = tokenManager.getAuthToken();
        if (token == null) return;

        // ✅ CORREÇÃO: Usar getAuthClient() para salvar (Porta 8080)
        AppointmentApi api = ApiClient.getAuthClient().create(AppointmentApi.class);
        
        AppointmentCreateRequest request = new AppointmentCreateRequest();
        request.setTitle(appointment.getTitle());
        request.setDescription(appointment.getDescription());
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        request.setAppointmentDate(sdf.format(appointment.getDate()));
        
        SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        request.setTime(timeSdf.format(appointment.getDate()));
        
        // 🔥 NOVO: Enviar ID do paciente
        if (appointment.getPatientId() != null) {
            request.setPatientId(appointment.getPatientId());
        }
        
        api.createAppointment(token, request).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "✅ Agendamento salvo no banco");
                    Toast.makeText(CalendarActivity.this, "Agendamento salvo", Toast.LENGTH_SHORT).show();
                    loadAppointmentsForMonth(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH) + 1);
                } else {
                    Log.e(TAG, "❌ Erro ao salvar agendamento: " + response.code());
                    Toast.makeText(CalendarActivity.this, "Erro ao salvar agendamento", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "❌ Falha ao salvar", t);
                Toast.makeText(CalendarActivity.this, "Erro de conexão ao salvar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
