package com.example.testbackend.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.ExerciseDetailActivity;
import com.example.testbackend.R;
import com.example.testbackend.models.Exercise;
import com.example.testbackend.models.Task;
import com.example.testbackend.models.TaskCompletionRequest;
import com.example.testbackend.models.TaskCompletionResponse;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.TaskApi;
import com.example.testbackend.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_EXERCISE = 0;
    private static final int TYPE_TASK = 1;
    private final List<Object> items;
    private final String token;

    public ExerciseAdapter(List<Object> items, String token) {
        this.items = items;
        this.token = token;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Exercise) {
            return TYPE_EXERCISE;
        } else {
            return TYPE_TASK;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_EXERCISE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
            return new ExerciseViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_radio, parent, false);
            return new TaskViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_EXERCISE) {
            Exercise exercise = (Exercise) items.get(position);
            ExerciseViewHolder evh = (ExerciseViewHolder) holder;
            
            evh.tvName.setText(exercise.getTitle());
            evh.tvDescription.setText(exercise.getDescription());

            if (exercise.getImageUrl() != null && !exercise.getImageUrl().isEmpty()) {
                String baseUrl = Constants.EXERCISE_BASE_URL;
                if (baseUrl.endsWith("/")) {
                    baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
                }
                String fullImageUrl = baseUrl + exercise.getImageUrl();
                
                Picasso.get()
                    .load(fullImageUrl)
                    .placeholder(R.drawable.bg_preview)
                    .error(R.drawable.bg_preview)
                    .into(evh.ivExerciseImage);
            } else {
                evh.ivExerciseImage.setImageResource(R.drawable.bg_preview);
            }

            View.OnClickListener detailLauncher = v -> {
                Intent intent = new Intent(v.getContext(), ExerciseDetailActivity.class);
                intent.putExtra("exercise_id", exercise.getId());
                v.getContext().startActivity(intent);
            };

            evh.itemView.setOnClickListener(detailLauncher);
            evh.btnStart.setOnClickListener(detailLauncher);
        } else {
            Task task = (Task) items.get(position);
            TaskViewHolder tvh = (TaskViewHolder) holder;
            
            tvh.tvTitle.setText(task.getTitle());
            tvh.tvDescription.setText(task.getDescription());
            tvh.tvPoints.setText(String.format(Locale.getDefault(), "+%d pts", task.getPointsValue() != null ? task.getPointsValue() : 0));
            
            boolean completedToday = task.getCompletedToday() != null && task.getCompletedToday();
            tvh.radioButton.setEnabled(!completedToday);
            tvh.radioButton.setChecked(completedToday);
            
            tvh.radioButton.setOnClickListener(v -> {
                if (task.getCompletedToday() == null || !task.getCompletedToday()) {
                    completeTask(task, tvh);
                } else {
                    tvh.radioButton.setChecked(true);
                }
            });
            
            tvh.itemView.setOnClickListener(v -> {
                if (task.getCompletedToday() == null || !task.getCompletedToday()) {
                    tvh.radioButton.setChecked(true);
                    completeTask(task, tvh);
                }
            });
        }
    }

    private void completeTask(Task task, TaskViewHolder holder) {
        TaskApi api = ApiClient.getTaskClient().create(TaskApi.class);
        
        // Garantir o prefixo Bearer se necessário
        String authHeader = (token != null && !token.startsWith("Bearer ")) ? "Bearer " + token : token;
        
        // 🔥 Cria request com ID REAL da tarefa para controle individual
        TaskCompletionRequest request = new TaskCompletionRequest(task.getId());
        
        api.completeTask(authHeader, request).enqueue(new Callback<TaskCompletionResponse>() {
            @Override
            public void onResponse(Call<TaskCompletionResponse> call, Response<TaskCompletionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TaskCompletionResponse result = response.body();
                    
                    if (result.isSuccess()) {
                        task.setCompletedToday(true);
                        holder.radioButton.setChecked(true);
                        holder.radioButton.setEnabled(false);
                        Toast.makeText(holder.itemView.getContext(), "Tarefa concluída! +" + (task.getPointsValue() != null ? task.getPointsValue() : 0) + " pontos", Toast.LENGTH_SHORT).show();
                    } else {
                        // Trata bloqueio de repetição diária
                        holder.radioButton.setChecked(false);
                        String message = result.getMessage();
                        if (result.getCanRepeatTomorrow() != null && result.getCanRepeatTomorrow()) {
                            message += "\n\n📅 Você poderá repetir este exercício amanhã!";
                        }
                        Toast.makeText(holder.itemView.getContext(), message, Toast.LENGTH_LONG).show();
                    }
                } else {
                    holder.radioButton.setChecked(false);
                    Toast.makeText(holder.itemView.getContext(), "Erro ao completar tarefa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TaskCompletionResponse> call, Throwable t) {
                holder.radioButton.setChecked(false);
                Toast.makeText(holder.itemView.getContext(), "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        ImageView ivExerciseImage;
        TextView tvName, tvDescription;
        MaterialButton btnStart;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            ivExerciseImage = itemView.findViewById(R.id.ivExerciseImage);
            tvName = itemView.findViewById(R.id.tvExerciseName);
            tvDescription = itemView.findViewById(R.id.tvExerciseDescription);
            btnStart = itemView.findViewById(R.id.btnStartExercise);
        }
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvPoints;
        RadioButton radioButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvDescription = itemView.findViewById(R.id.tvTaskDescription);
            tvPoints = itemView.findViewById(R.id.tvTaskPoints);
            radioButton = itemView.findViewById(R.id.radioButton);
        }
    }
}
