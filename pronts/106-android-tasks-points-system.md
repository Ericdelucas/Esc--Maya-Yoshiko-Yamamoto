# 📱 Android Tasks & Points System Implementation

## 🎯 **Objetivo**

Implementar no Android o sistema completo de tarefas e pontos, permitindo que:
- Profissionais criem tarefas com pontuação
- Pacientes marquem tarefas como concluídas (radiobutton)
- Sistema de ranking real em tempo real
- Desafios globais e gamificação

---

## 📋 **Visão Geral das Alterações Necessárias**

### **1. Novas Models (Data Classes)**

#### **Task Models**
```java
// app/src/main/java/com/example/testbackend/models/Task.java
public class Task {
    private Integer id;
    private Integer professional_id;
    private Integer patient_id;
    private String title;
    private String description;
    private Integer points_value;
    private Integer exercise_id;
    private Integer frequency_per_week;
    private Boolean is_active;
    private String start_date;
    private String end_date;
    private String created_at;
    private Boolean completed_today; // Campo auxiliar para UI
}

// app/src/main/java/com/example/testbackend/models/TaskCompletion.java
public class TaskCompletion {
    private Integer id;
    private Integer task_id;
    private Integer patient_id;
    private String completed_at;
    private Integer points_earned;
    private String completion_notes;
    private Boolean verified_by_professional;
}

// app/src/main/java/com/example/testbackend/models/UserPoints.java
public class UserPoints {
    private Integer user_id;
    private Integer total_points;
    private Integer weekly_points;
    private Integer monthly_points;
    private Integer current_streak;
    private Integer longest_streak;
    private String last_completion_date;
    private Integer rank_position;
}

// app/src/main/java/com/example/testbackend/models/LeaderboardEntry.java (ATUALIZAR)
public class LeaderboardEntry {
    private Integer user_id;
    private String user_name;
    private String user_email;
    private Integer total_points;
    private Integer rank_position;
    private Integer current_streak;
}
```

### **2. Novas APIs (Retrofit Interfaces)**

#### **TaskApi**
```java
// app/src/main/java/com/example/testbackend/network/TaskApi.java
public interface TaskApi {
    
    // Criar tarefa (profissional)
    @POST("tasks")
    Call<Task> createTask(@Header("Authorization") String token, @Body TaskCreateRequest task);
    
    // Listar tarefas do paciente
    @GET("tasks/patient/{patient_id}")
    Call<PatientTaskList> getPatientTasks(@Header("Authorization") String token, @Path("patient_id") int patientId);
    
    // Listar tarefas diárias
    @GET("tasks/patient/{patient_id}/daily")
    Call<List<Task>> getDailyTasks(@Header("Authorization") String token, @Path("patient_id") int patientId);
    
    // Marcar tarefa como concluída
    @POST("tasks/{task_id}/complete")
    Call<TaskCompletion> completeTask(@Header("Authorization") String token, @Path("task_id") int taskId, @Body TaskCompletionRequest request);
    
    // Verificar se pode completar hoje
    @GET("tasks/{task_id}/can-complete")
    Call<CanCompleteResponse> canCompleteToday(@Header("Authorization") String token, @Path("task_id") int taskId);
    
    // Obter pontos do usuário
    @GET("tasks/points/{user_id}")
    Call<UserPoints> getUserPoints(@Header("Authorization") String token, @Path("user_id") int userId);
    
    // Obter ranking
    @GET("tasks/leaderboard")
    Call<List<LeaderboardEntry>> getLeaderboard(@Header("Authorization") String token, @Query("limit") int limit);
    
    // Obter histórico de pontos
    @GET("tasks/points/{user_id}/history")
    Call<List<PointsHistory>> getPointsHistory(@Header("Authorization") String token, @Path("user_id") int userId);
    
    // Obter desafios globais
    @GET("tasks/challenges")
    Call<List<GlobalChallenge>> getChallenges(@Header("Authorization") String token);
    
    // Participar de desafio
    @POST("tasks/challenges/join")
    Call<ChallengeParticipation> joinChallenge(@Header("Authorization") String token, @Body JoinChallengeRequest request);
}
```

### **3. Novas Activities**

#### **CreateTaskActivity** (Profissional)
```java
// app/src/main/java/com/example/testbackend/CreateTaskActivity.java
public class CreateTaskActivity extends AppCompatActivity {
    private EditText etTitle, etDescription, etPoints;
    private Spinner spPatient, spFrequency;
    private Button btnSave;
    private TaskApi taskApi;
    private List<Patient> patients;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        
        initViews();
        setupSpinner();
        loadPatients();
        setupClickListeners();
    }
    
    private void createTask() {
        TaskCreateRequest request = new TaskCreateRequest();
        request.setPatient_id(selectedPatient.getId());
        request.setTitle(etTitle.getText().toString());
        request.setDescription(etDescription.getText().toString());
        request.setPoints_value(Integer.parseInt(etPoints.getText().toString()));
        request.setFrequency_per_week(spFrequency.getSelectedItemPosition() + 1);
        request.setStart_date(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        
        taskApi.createTask("Bearer " + token, request).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateTaskActivity.this, "Tarefa criada com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CreateTaskActivity.this, "Erro ao criar tarefa", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Toast.makeText(CreateTaskActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```

#### **ExerciseListActivity** (Paciente - ATUALIZAR)
```java
// MODIFICAR ExerciseListActivity.java existente

// Adicionar na classe:
private TaskApi taskApi;
private List<Task> tasks = new ArrayList<>();
private UserPoints userPoints;

// Modificar o método fetchExercises():
private void fetchTasks() {
    int patientId = tokenManager.getUserId();
    taskApi.getPatientTasks("Bearer " + token, patientId).enqueue(new Callback<PatientTaskList>() {
        @Override
        public void onResponse(Call<PatientTaskList> call, Response<PatientTaskList> response) {
            if (response.isSuccessful() && response.body() != null) {
                PatientTaskList taskList = response.body();
                tasks.clear();
                tasks.addAll(taskList.getTasks());
                
                // Atualizar adapter com tarefas e radiobuttons
                adapter = new TaskWithRadioAdapter(tasks, ExerciseListActivity.this);
                rvExercises.setAdapter(adapter);
                
                // Mostrar estatísticas
                updateStats(taskList);
            }
        }
        
        @Override
        public void onFailure(Call<Task> call, Throwable t) {
            Toast.makeText(ExerciseListActivity.this, "Erro ao carregar tarefas", Toast.LENGTH_SHORT).show();
        }
    });
}

// Adicionar método para completar tarefa:
private void completeTask(Task task) {
    TaskCompletionRequest request = new TaskCompletionRequest();
    request.setTask_id(task.getId());
    
    taskApi.completeTask("Bearer " + token, request).enqueue(new Callback<TaskCompletion>() {
        @Override
        public void onResponse(Call<TaskCompletion> call, Response<TaskCompletion> response) {
            if (response.isSuccessful()) {
                Toast.makeText(ExerciseListActivity.this, "Tarefa concluída! +" + task.getPoints_value() + " pontos", Toast.LENGTH_SHORT).show();
                fetchTasks(); // Atualizar lista
                fetchUserPoints(); // Atualizar pontos
            } else {
                Toast.makeText(ExerciseListActivity.this, "Erro ao concluir tarefa", Toast.LENGTH_SHORT).show();
            }
        }
        
        @Override
        public void onFailure(Call<Task> call, Throwable t) {
            Toast.makeText(ExerciseListActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
        }
    });
}
```

#### **TaskWithRadioAdapter** (NOVO)
```java
// app/src/main/java/com/example/testbackend/adapters/TaskWithRadioAdapter.java
public class TaskWithRadioAdapter extends RecyclerView.Adapter<TaskWithRadioAdapter.TaskViewHolder> {
    private List<Task> tasks;
    private Context context;
    
    public TaskWithRadioAdapter(List<Task> tasks, Context context) {
        this.tasks = tasks;
        this.context = context;
    }
    
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_radio, parent, false);
        return new TaskViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        
        holder.tvTitle.setText(task.getTitle());
        holder.tvDescription.setText(task.getDescription());
        holder.tvPoints.setText("+" + task.getPoints_value() + " pts");
        
        // Verificar se já foi completada hoje
        holder.radioButton.setEnabled(!task.getCompleted_today());
        holder.radioButton.setChecked(task.getCompleted_today());
        
        holder.radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !task.getCompleted_today()) {
                // Completar tarefa
                ((ExerciseListActivity) context).completeTask(task);
            }
        });
        
        holder.itemView.setOnClickListener(v -> {
            if (!task.getCompleted_today()) {
                holder.radioButton.setChecked(true);
            }
        });
    }
    
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvPoints;
        RadioButton radioButton;
        
        public TaskViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvDescription = itemView.findViewById(R.id.tvTaskDescription);
            tvPoints = itemView.findViewById(R.id.tvTaskPoints);
            radioButton = itemView.findViewById(R.id.radioButton);
        }
    }
}
```

### **4. Novos Layouts XML**

#### **activity_create_task.xml**
```xml
<!-- res/layout/activity_create_task.xml -->
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">
    
    <Toolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:title="Nova Tarefa" />
    
    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp">
        
        <EditText
            android:id="@+id/etTitle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Título da tarefa" />
    </com.google.android.material.textfield.TextInputLayout>
    
    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp">
        
        <EditText
            android:id="@+id/etDescription"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Descrição"
            android:minLines="3" />
    </com.google.android.material.textfield.TextInputLayout>
    
    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp">
        
        <EditText
            android:id="@+id/etPoints"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Pontos"
            android:inputType="number" />
    </com.google.android.material.textfield.TextInputLayout>
    
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Paciente:"
        android:layout_marginTop="16dp" />
    
    <Spinner
        android:id="@+id/spPatient"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
    
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Frequência semanal:"
        android:layout_marginTop="16dp" />
    
    <Spinner
        android:id="@+id/spFrequency"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:entries="@array/frequency_options" />
    
    <Button
        android:id="@+id/btnSave"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Criar Tarefa"
        android:layout_marginTop="24dp" />
    
</LinearLayout>
```

#### **item_task_radio.xml**
```xml
<!-- res/layout/item_task_radio.xml -->
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="8dp"
    app:cardCornerRadius="8dp"
    app:cardElevation="4dp">
    
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="16dp">
        
        <RadioButton
            android:id="@+id/radioButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_vertical"
            android:layout_marginEnd="16dp" />
        
        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical">
            
            <TextView
                android:id="@+id/tvTaskTitle"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Título da tarefa"
                android:textStyle="bold"
                android:textSize="16sp" />
            
            <TextView
                android:id="@+id/tvTaskDescription"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Descrição da tarefa"
                android:textColor="#666666"
                android:layout_marginTop="4dp" />
            
            <TextView
                android:id="@+id/tvTaskPoints"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="+10 pontos"
                android:textColor="#4CAF50"
                android:textStyle="bold"
                android:layout_marginTop="8dp" />
            
        </LinearLayout>
        
    </LinearLayout>
    
</androidx.cardview.widget.CardView>
```

### **5. LeaderboardActivity** (ATUALIZAR)

```java
// MODIFICAR LeaderboardActivity.java existente

private void fetchLeaderboard() {
    TaskApi taskApi = ApiClient.getTaskClient().create(TaskApi.class);
    taskApi.getLeaderboard("Bearer " + token, 50).enqueue(new Callback<List<LeaderboardEntry>>() {
        @Override
        public void onResponse(Call<List<LeaderboardEntry>> call, Response<List<LeaderboardEntry>> response) {
            if (response.isSuccessful() && response.body() != null) {
                List<LeaderboardEntry> entries = response.body();
                
                // Adicionar usuário atual se não estiver no ranking
                int currentUserId = tokenManager.getUserId();
                boolean userInRanking = entries.stream().anyMatch(e -> e.getUser_id() == currentUserId);
                
                if (!userInRanking) {
                    // Buscar pontos do usuário atual
                    taskApi.getUserPoints("Bearer " + token, currentUserId).enqueue(new Callback<UserPoints>() {
                        @Override
                        public void onResponse(Call<UserPoints> call, Response<UserPoints> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                UserPoints userPoints = response.body();
                                LeaderboardEntry currentUser = new LeaderboardEntry(
                                    -1, // Sem posição no ranking
                                    "Você",
                                    "",
                                    userPoints.getTotal_points(),
                                    userPoints.getRank_position(),
                                    userPoints.getCurrent_streak()
                                );
                                entries.add(currentUser);
                                
                                LeaderboardAdapter adapter = new LeaderboardAdapter(entries);
                                recyclerView.setAdapter(adapter);
                            }
                        }
                        
                        @Override
                        public void onFailure(Call<UserPoints> call, Throwable t) {
                            // Erro ao buscar pontos do usuário
                        }
                    });
                } else {
                    LeaderboardAdapter adapter = new LeaderboardAdapter(entries);
                    recyclerView.setAdapter(adapter);
                }
            }
        }
        
        @Override
        public void onFailure(Call<List<LeaderboardEntry>> call, Throwable t) {
            Toast.makeText(LeaderboardActivity.this, "Erro ao carregar ranking", Toast.LENGTH_SHORT).show();
        }
    });
}
```

### **6. MainActivity** (ATUALIZAR - Profissional)

```java
// NO ProfessionalMainActivity.java - Adicionar botão para criar tarefas

private FloatingActionButton fabCreateTask;

private void initViews() {
    // ... código existente ...
    
    fabCreateTask = findViewById(R.id.fabCreateTask);
    fabCreateTask.setOnClickListener(v -> {
        startActivity(new Intent(this, CreateTaskActivity.class));
    });
}
```

### **7. ApiClient** (ATUALIZAR)

```java
// Adicionar em ApiClient.java:
public static Retrofit getTaskClient() {
    return new Retrofit.Builder()
        .baseUrl(Constants.AUTH_BASE_URL) // Usar auth-service
        .addConverterFactory(GsonConverterFactory.create())
        .build();
}
```

### **8. Strings e Resources**

```xml
<!-- res/values/strings.xml - Adicionar -->
<string-array name="frequency_options">
    <item>1 vez por semana</item>
    <item>2 vezes por semana</item>
    <item>3 vezes por semana</item>
    <item>4 vezes por semana</item>
    <item>5 vezes por semana</item>
    <item>6 vezes por semana</item>
    <item>Todos os dias</item>
</string-array>
```

---

## 🔧 **Implementação Passo a Passo**

### **Passo 1: Criar Models**
1. Criar `Task.java`
2. Criar `TaskCompletion.java`  
3. Criar `UserPoints.java`
4. Atualizar `LeaderboardEntry.java`

### **Passo 2: Implementar APIs**
1. Criar `TaskApi.java`
2. Adicionar método `getTaskClient()` em `ApiClient.java`

### **Passo 3: Criar Activities**
1. Criar `CreateTaskActivity.java` + layout
2. Criar `TaskWithRadioAdapter.java` + layout
3. Atualizar `ExerciseListActivity.java`

### **Passo 4: Atualizar Telas Existentes**
1. Modificar `LeaderboardActivity.java`
2. Adicionar FAB em `ProfessionalMainActivity.java`
3. Atualizar `MainActivity.java` (paciente)

### **Passo 5: Testar Integração**
1. Testar criação de tarefas (profissional)
2. Testar conclusão de tarefas (paciente)
3. Verificar ranking em tempo real
4. Testar sistema de pontos

---

## 🎮 **Fluxo de Uso**

### **Profissional:**
1. Acessa `ProfessionalMainActivity`
2. Clica no FAB (+) para criar tarefa
3. Preenche título, descrição, pontos
4. Seleciona paciente e frequência
5. Salva tarefa

### **Paciente:**
1. Acessa "Meus Exercícios"
2. Vê lista de tarefas com radiobuttons
3. Clica no radiobutton para completar tarefa
4. Recebe confirmação e pontos
5. Acessa "Ranking" para ver posição

---

## 🚀 **Próximos Passos**

1. **Implementar notificações** quando tarefas são criadas
2. **Adicionar sistema de badges/conquistas**
3. **Implementar desafios globais**
4. **Gráficos de progresso semanal/mensal**
5. **Sincronização offline**

---

## ⚠️ **Considerações Importantes**

- **Permissões**: Verificar se usuário é profissional/paciente
- **Validações**: Campos obrigatórios na criação de tarefas
- **Tratamento de erros**: Conexão, API, etc.
- **Performance**: Carregar tarefas com paginação se necessário
- **UX**: Feedback visual ao completar tarefas

---

## 📞 **Suporte**

Qualquer dúvida na implementação, verificar:
1. Logs do backend: `docker compose logs auth-service`
2. Documentação da API: `http://localhost:8080/docs`
3. Testar endpoints diretamente com curl

**Sistema completo de tarefas e pontos pronto para uso! 🎯**
