package com.example.testbackend;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.models.Appointment;
import com.example.testbackend.models.AppointmentListResponse;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.AppointmentApi;
import com.example.testbackend.utils.TokenManager;

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

public class DayAppointmentsDialog extends Dialog {

    private static final String TAG = "DAY_APPT_DEBUG";
    private Context context;
    private int day, month, year;
    private TokenManager tokenManager;

    private RecyclerView rvAppointments;
    private TextView tvDayTitle;
    private DayAppointmentsAdapter adapter;
    private Button btnClose;

    public DayAppointmentsDialog(@NonNull Context context, int day, int month, int year, List<Appointment> appointments) {
        super(context);
        this.context = context;
        this.day = day;
        this.month = month; // 0-indexed do Java
        this.year = year;
        this.tokenManager = new TokenManager(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_day_appointments);

        setupViews();
        loadDayAppointmentsFromAPI();
    }

    private void setupViews() {
        tvDayTitle = findViewById(R.id.tvDayTitle);
        rvAppointments = findViewById(R.id.rvAppointments);
        btnClose = findViewById(R.id.btnClose);

        SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        tvDayTitle.setText(sdf.format(cal.getTime()));

        adapter = new DayAppointmentsAdapter(context, new ArrayList<>());
        rvAppointments.setLayoutManager(new LinearLayoutManager(context));
        rvAppointments.setAdapter(adapter);

        btnClose.setOnClickListener(v -> dismiss());
    }

    private void loadDayAppointmentsFromAPI() {
        String token = tokenManager.getAuthToken();
        if (token == null) return;

        AppointmentApi api = ApiClient.getAuthClient().create(AppointmentApi.class);
        
        // 🔥 API espera mês 1-12
        int apiMonth = month + 1;
        Log.d(TAG, "Buscando agendamentos do dia: " + day + "/" + apiMonth + "/" + year);

        api.getAppointmentsByDate(token, year, apiMonth, day).enqueue(new Callback<AppointmentListResponse>() {
            @Override
            public void onResponse(Call<AppointmentListResponse> call, Response<AppointmentListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Appointment> dayAppointments = convertToAppointmentList(response.body().getAppointments());
                    adapter.updateAppointments(dayAppointments);
                    if (dayAppointments.isEmpty()) {
                        Toast.makeText(context, "Nenhum agendamento neste dia", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Erro API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AppointmentListResponse> call, Throwable t) {
                Log.e(TAG, "Falha na conexão", t);
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
}
