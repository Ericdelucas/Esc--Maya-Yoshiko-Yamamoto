package com.example.testbackend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.models.Appointment;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.AppointmentApi;
import com.example.testbackend.utils.TokenManager;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DayAppointmentsAdapter extends RecyclerView.Adapter<DayAppointmentsAdapter.AppointmentViewHolder> {

    private Context context;
    private List<Appointment> appointments;
    private TokenManager tokenManager;

    public DayAppointmentsAdapter(Context context, List<Appointment> appointments) {
        this.context = context;
        this.appointments = appointments;
        this.tokenManager = new TokenManager(context);
    }

    public void updateAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);

        holder.tvTitle.setText(appointment.getTitle());
        holder.tvDescription.setText(appointment.getDescription());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", new Locale("pt", "BR"));
        holder.tvTime.setText(timeFormat.format(appointment.getDate()));
        
        // 🔥 CONFIGURAR BOTÃO DELETAR
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                .setTitle("Deletar Agendamento")
                .setMessage("Tem certeza que deseja deletar \"" + appointment.getTitle() + "\"?")
                .setPositiveButton("Deletar", (dialog, which) -> deleteAppointment(appointment))
                .setNegativeButton("Cancelar", null)
                .show();
        });
    }
    
    // 🔥 MÉTODO PARA DELETAR AGENDAMENTO
    private void deleteAppointment(Appointment appointment) {
        String token = tokenManager.getAuthToken();
        if (token == null) return;
        
        AppointmentApi api = ApiClient.getAuthClient().create(AppointmentApi.class);
        api.deleteAppointment(token, appointment.getId()).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    // Remover da lista
                    int index = appointments.indexOf(appointment);
                    if (index != -1) {
                        appointments.remove(index);
                        notifyItemRemoved(index);
                    }
                    
                    android.widget.Toast.makeText(context, "Agendamento deletado com sucesso", android.widget.Toast.LENGTH_SHORT).show();
                } else {
                    android.widget.Toast.makeText(context, "Erro ao deletar agendamento", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                android.widget.Toast.makeText(context, "Erro de conexão", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvTime;
        Button btnDelete;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
