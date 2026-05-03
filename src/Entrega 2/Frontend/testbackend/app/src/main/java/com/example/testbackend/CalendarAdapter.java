package com.example.testbackend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.testbackend.models.Appointment;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    
    private Context context;
    private List<Appointment> appointments;
    private OnDateClickListener onDateClickListener;
    private Calendar currentCalendar;
    private List<Integer> daysInMonth;
    
    public interface OnDateClickListener {
        void onDateClick(int day, int month, int year);
    }
    
    public CalendarAdapter(Context context, List<Appointment> appointments, OnDateClickListener listener) {
        this.context = context;
        this.appointments = appointments;
        this.onDateClickListener = listener;
        this.currentCalendar = Calendar.getInstance();
        generateDaysInMonth();
    }
    
    public void updateCalendar(Calendar calendar) {
        this.currentCalendar = (Calendar) calendar.clone();
        generateDaysInMonth();
        notifyDataSetChanged();
    }
    
    public void updateAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
        notifyDataSetChanged();
    }
    
    private void generateDaysInMonth() {
        daysInMonth = new ArrayList<>();
        Calendar cal = (Calendar) currentCalendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Domingo
        int daysInMonthCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Adicionar dias vazios antes do primeiro dia
        for (int i = 0; i < firstDayOfWeek; i++) {
            daysInMonth.add(0);
        }
        
        // Adicionar dias do mês
        for (int i = 1; i <= daysInMonthCount; i++) {
            daysInMonth.add(i);
        }
    }
    
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        int day = daysInMonth.get(position);
        
        if (day == 0) {
            holder.tvDay.setText("");
            holder.indicator.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(null);
            return;
        }
        
        holder.tvDay.setText(String.valueOf(day));
        
        // Verificar se tem agendamentos neste dia
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);
        
        boolean hasAppointments = hasAppointmentsOnDay(day, month, year);
        holder.indicator.setVisibility(hasAppointments ? View.VISIBLE : View.GONE);
        
        holder.itemView.setOnClickListener(v -> {
            if (onDateClickListener != null) {
                onDateClickListener.onDateClick(day, month, year);
            }
        });
    }
    
    private boolean hasAppointmentsOnDay(int day, int month, int year) {
        Calendar checkCal = Calendar.getInstance();
        for (Appointment app : appointments) {
            checkCal.setTime(app.getDate());
            if (checkCal.get(Calendar.DAY_OF_MONTH) == day &&
                checkCal.get(Calendar.MONTH) == month &&
                checkCal.get(Calendar.YEAR) == year) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int getItemCount() {
        return daysInMonth.size();
    }
    
    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        View indicator;
        
        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            indicator = itemView.findViewById(R.id.indicator);
        }
    }
}
