package com.example.testbackend;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.testbackend.models.Appointment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddAppointmentDialog extends Dialog {

    private static final String TAG = "ADD_APPT_DEBUG";
    private Context context;
    private OnAppointmentSavedListener listener;
    private Date selectedDate; // 🔥 Data selecionada inicial

    private EditText etTitle, etDescription, etTime, etDate;
    private Button btnSave, btnCancel;

    public interface OnAppointmentSavedListener {
        void onAppointmentSaved(Appointment appointment);
    }

    public AddAppointmentDialog(@NonNull Context context, Date selectedDate, OnAppointmentSavedListener listener) {
        super(context);
        this.context = context;
        this.selectedDate = selectedDate;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_appointment);

        setupViews();
        setupDateField();
    }

    private void setupViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etTime = findViewById(R.id.etTime);
        etDate = findViewById(R.id.etDate);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(v -> saveAppointment());
        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void setupDateField() {
        if (etDate != null && selectedDate != null) {
            // 🔥 Mostrar a data selecionada como valor inicial
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            etDate.setText(sdf.format(selectedDate));
            
            // 🔥 Tornar o campo clicável para abrir o DatePicker
            etDate.setFocusable(false); // Evita teclado
            etDate.setClickable(true);
            etDate.setOnClickListener(v -> showDatePicker());
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            context,
            (view, year, month, dayOfMonth) -> {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);
                selectedDate = selectedCalendar.getTime(); // Atualiza a variável
                
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                etDate.setText(sdf.format(selectedDate));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.show();
    }

    private void saveAppointment() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String time = etTime.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(context, "Digite o título do agendamento", Toast.LENGTH_SHORT).show();
            return;
        }

        // Usar a selectedDate que pode ter sido alterada pelo DatePicker
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);

        // Processar horário se houver
        if (!time.isEmpty() && time.contains(":")) {
            try {
                String[] parts = time.split(":");
                if (parts.length == 2) {
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
                    cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Horário inválido: " + time);
            }
        }

        Appointment appointment = new Appointment(
                (int) (System.currentTimeMillis() / 1000),
                title,
                cal.getTime(),
                description
        );

        if (listener != null) {
            listener.onAppointmentSaved(appointment);
        }

        dismiss();
        Toast.makeText(context, "Agendamento salvo para " + 
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate), 
            Toast.LENGTH_SHORT).show();
    }
}
