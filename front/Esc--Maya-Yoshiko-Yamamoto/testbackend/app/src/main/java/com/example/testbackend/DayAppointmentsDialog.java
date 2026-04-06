package com.example.testbackend;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.models.Appointment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DayAppointmentsDialog extends Dialog {

    private Context context;
    private int day, month, year;
    private List<Appointment> allAppointments;

    private RecyclerView rvAppointments;
    private TextView tvDayTitle;
    private DayAppointmentsAdapter adapter;
    private Button btnClose;

    public DayAppointmentsDialog(@NonNull Context context, int day, int month, int year, List<Appointment> appointments) {
        super(context);
        this.context = context;
        this.day = day;
        this.month = month;
        this.year = year;
        this.allAppointments = appointments;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_day_appointments);

        setupViews();
        loadDayAppointments();
    }

    private void setupViews() {
        tvDayTitle = findViewById(R.id.tvDayTitle);
        rvAppointments = findViewById(R.id.rvAppointments);
        btnClose = findViewById(R.id.btnClose);

        // Formatar título
        SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        tvDayTitle.setText(sdf.format(cal.getTime()));

        // Setup RecyclerView
        adapter = new DayAppointmentsAdapter(context, new ArrayList<>());
        rvAppointments.setLayoutManager(new LinearLayoutManager(context));
        rvAppointments.setAdapter(adapter);

        btnClose.setOnClickListener(v -> dismiss());
    }

    private void loadDayAppointments() {
        List<Appointment> dayAppointments = new ArrayList<>();

        // 🔥 CORREÇÃO: Calendar.set() aceita no máximo 6 argumentos (year, month, date, hourOfDay, minute, second)
        // Para milissegundos, usamos o método set separado.
        Calendar startCal = Calendar.getInstance();
        startCal.set(year, month, day, 0, 0, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.set(year, month, day, 23, 59, 59);
        endCal.set(Calendar.MILLISECOND, 999);

        for (Appointment appointment : allAppointments) {
            Calendar appCal = Calendar.getInstance();
            appCal.setTime(appointment.getDate());

            if (appCal.getTimeInMillis() >= startCal.getTimeInMillis() &&
                    appCal.getTimeInMillis() <= endCal.getTimeInMillis()) {
                dayAppointments.add(appointment);
            }
        }

        adapter.updateAppointments(dayAppointments);

        if (dayAppointments.isEmpty()) {
            Toast.makeText(context, "Nenhum agendamento neste dia", Toast.LENGTH_SHORT).show();
        }
    }
}
