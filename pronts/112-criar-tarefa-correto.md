# 🔧 **CRIAR TAREFA - GUIA CORRIGIDO**

## ❌ **ERRO 422 - PROBLEMA IDENTIFICADO**

**Erro 422 (Unprocessable Entity) significa que os dados enviados estão inválidos!**

---

## 🔍 **PROVÁVEIS CAUSAS DO ERRO 422**

### **1. Campos obrigatórios faltando**
### **2. Formato de data inválido**  
### **3. Valores fora dos limites**
### **4. Tipos de dados incorretos**

---

## 📋 **SCHEMA CORRETO PARA CRIAÇÃO**

### **TaskCreate Schema (Backend):**
```python
class TaskCreate(BaseModel):
    patient_id: int          # OBRIGATÓRIO
    title: str              # OBRIGATÓRIO, 1-120 caracteres
    description: str        # OBRIGATÓRIO, 1-2000 caracteres
    points_value: int       # OPCIONAL, default=10, range=1-1000
    exercise_id: int        # OPCIONAL, pode ser null
    frequency_per_week: int # OPCIONAL, default=1, range=1-7
    start_date: date        # OBRIGATÓRIO - formato YYYY-MM-DD
    end_date: date         # OPCIONAL, pode ser null
```

---

## 📱 **IMPLEMENTAÇÃO ANDROID CORRETA**

### **PASSO 1: TaskCreateRequest Model**

#### **Arquivo:** `app/src/main/java/com/example/testbackend/models/TaskCreateRequest.java`

```java
package com.example.testbackend.models;

public class TaskCreateRequest {
    private Integer patient_id;      // OBRIGATÓRIO
    private String title;            // OBRIGATÓRIO
    private String description;      // OBRIGATÓRIO
    private Integer points_value;    // OPCIONAL (default=10)
    private Integer exercise_id;     // OPCIONAL (pode ser null)
    private Integer frequency_per_week; // OPCIONAL (default=1)
    private String start_date;       // OBRIGATÓRIO - formato "YYYY-MM-DD"
    private String end_date;        // OPCIONAL (pode ser null)
    
    // Getters e Setters
    public Integer getPatient_id() { return patient_id; }
    public void setPatient_id(Integer patient_id) { this.patient_id = patient_id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getPoints_value() { return points_value; }
    public void setPoints_value(Integer points_value) { this.points_value = points_value; }
    
    public Integer getExercise_id() { return exercise_id; }
    public void setExercise_id(Integer exercise_id) { this.exercise_id = exercise_id; }
    
    public Integer getFrequency_per_week() { return frequency_per_week; }
    public void setFrequency_per_week(Integer frequency_per_week) { this.frequency_per_week = frequency_per_week; }
    
    public String getStart_date() { return start_date; }
    public void setStart_date(String start_date) { this.start_date = start_date; }
    
    public String getEnd_date() { return end_date; }
    public void setEnd_date(String end_date) { this.end_date = end_date; }
}
```

### **PASSO 2: TaskApi Interface**

#### **Arquivo:** `app/src/main/java/com/example/testbackend/network/TaskApi.java`

```java
package com.example.testbackend.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import com.example.testbackend.models.Task;
import com.example.testbackend.models.TaskCreateRequest;

public interface TaskApi {
    @POST("tasks")
    Call<Task> createTask(@Header("Authorization") String token, @Body TaskCreateRequest task);
}
```

### **PASSO 3: Método createTask CORRIGIDO**

#### **Arquivo:** `ProfessionalMainActivity.java`

```java
private void createTask(String title, String description, int points, int patientId, int frequency) {
    // Validação dos dados ANTES de enviar
    if (title == null || title.trim().isEmpty()) {
        Toast.makeText(this, "Título é obrigatório", Toast.LENGTH_SHORT).show();
        return;
    }
    
    if (title.length() > 120) {
        Toast.makeText(this, "Título muito longo (máx 120 caracteres)", Toast.LENGTH_SHORT).show();
        return;
    }
    
    if (description == null || description.trim().isEmpty()) {
        Toast.makeText(this, "Descrição é obrigatória", Toast.LENGTH_SHORT).show();
        return;
    }
    
    if (description.length() > 2000) {
        Toast.makeText(this, "Descrição muito longa (máx 2000 caracteres)", Toast.LENGTH_SHORT).show();
        return;
    }
    
    if (points < 1 || points > 1000) {
        Toast.makeText(this, "Pontos devem estar entre 1 e 1000", Toast.LENGTH_SHORT).show();
        return;
    }
    
    if (frequency < 1 || frequency > 7) {
        Toast.makeText(this, "Frequência deve estar entre 1 e 7 dias", Toast.LENGTH_SHORT).show();
        return;
    }
    
    // Criar request com dados VALIDADOS
    TaskCreateRequest request = new TaskCreateRequest();
    request.setPatient_id(patientId);
    request.setTitle(title.trim());
    request.setDescription(description.trim());
    request.setPoints_value(points);
    request.setExercise_id(null);  // Opcional - null se não associado a exercício
    request.setFrequency_per_week(frequency);
    
    // Data atual no formato correto YYYY-MM-DD
    String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    request.setStart_date(currentDate);
    request.setEnd_date(null);  // Opcional - null se não tiver data fim
    
    // Debug: mostrar o que está sendo enviado
    Log.d("TASK_CREATE", "Enviando tarefa:");
    Log.d("TASK_CREATE", "patient_id: " + request.getPatient_id());
    Log.d("TASK_CREATE", "title: '" + request.getTitle() + "'");
    Log.d("TASK_CREATE", "description: '" + request.getDescription() + "'");
    Log.d("TASK_CREATE", "points_value: " + request.getPoints_value());
    Log.d("TASK_CREATE", "frequency_per_week: " + request.getFrequency_per_week());
    Log.d("TASK_CREATE", "start_date: " + request.getStart_date());
    
    // Chamar API
    TaskApi taskApi = ApiClient.getTaskClient().create(TaskApi.class);
    String token = getToken();
    
    if (token == null || token.isEmpty()) {
        Toast.makeText(this, "Você não está logado", Toast.LENGTH_SHORT).show();
        return;
    }
    
    taskApi.createTask("Bearer " + token, request).enqueue(new Callback<Task>() {
        @Override
        public void onResponse(Call<Task> call, Response<Task> response) {
            Log.d("TASK_RESPONSE", "Response code: " + response.code());
            Log.d("TASK_RESPONSE", "Response successful: " + response.isSuccessful());
            
            if (response.isSuccessful()) {
                Task createdTask = response.body();
                Toast.makeText(ProfessionalMainActivity.this, 
                    "Tarefa criada com sucesso! ID: " + createdTask.getId(), 
                    Toast.LENGTH_LONG).show();
                
                // Fechar diálogo se estiver aberto
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                
                // Opcional: recarregar lista de tarefas
                // loadTasks();
                
            } else if (response.code() == 422) {
                // Erro de validação - mostrar detalhes
                Toast.makeText(ProfessionalMainActivity.this, 
                    "Erro de validação: Verifique os campos obrigatórios", 
                    Toast.LENGTH_LONG).show();
                Log.e("TASK_ERROR", "Validation error - 422");
                
                // Tentar pegar detalhes do erro
                try {
                    if (response.errorBody() != null) {
                        String errorBody = response.errorBody().string();
                        Log.e("TASK_ERROR", "Error body: " + errorBody);
                    }
                } catch (Exception e) {
                    Log.e("TASK_ERROR", "Failed to read error body", e);
                }
                
            } else {
                Toast.makeText(ProfessionalMainActivity.this, 
                    "Erro ao criar tarefa: " + response.code(), 
                    Toast.LENGTH_LONG).show();
                Log.e("TASK_ERROR", "Error code: " + response.code());
                Log.e("TASK_ERROR", "Error message: " + response.message());
            }
        }
        
        @Override
        public void onFailure(Call<Task> call, Throwable t) {
            Toast.makeText(ProfessionalMainActivity.this, 
                "Erro de conexão: " + t.getMessage(), 
                Toast.LENGTH_LONG).show();
            Log.e("TASK_ERROR", "Network error", t);
        }
    });
}
```

### **PASSO 4: Diálogo de Criação CORRIGIDO**

```java
private AlertDialog dialog; // Para controlar o diálogo

private void showCreateTaskDialog() {
    // Carregar pacientes primeiro
    loadPatientsForSpinner();
    
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_task, null);
    
    // Referências aos campos
    EditText etTitle = dialogView.findViewById(R.id.etTitle);
    EditText etDescription = dialogView.findViewById(R.id.etDescription);
    EditText etPoints = dialogView.findViewById(R.id.etPoints);
    Spinner spPatient = dialogView.findViewById(R.id.spPatient);
    Spinner spFrequency = dialogView.findViewById(R.id.spFrequency);
    
    // Configurar spinner de frequência com valores corretos
    String[] frequencyOptions = {"1 vez por semana", "2 vezes por semana", 
                                 "3 vezes por semana", "4 vezes por semana", 
                                 "5 vezes por semana", "6 vezes por semana", 
                                 "Todos os dias"};
    ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(this, 
        android.R.layout.simple_spinner_item, frequencyOptions);
    frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spFrequency.setAdapter(frequencyAdapter);
    
    builder.setView(dialogView)
           .setTitle("Nova Tarefa")
           .setPositiveButton("Criar", null)  // Vamos configurar depois
           .setNegativeButton("Cancelar", null);
    
    dialog = builder.create();
    dialog.show();
    
    // Configurar botão positivo para não fechar automaticamente
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
        // Validação dos campos
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String pointsStr = etPoints.getText().toString().trim();
        
        if (title.isEmpty()) {
            etTitle.setError("Título obrigatório");
            return;
        }
        
        if (description.isEmpty()) {
            etDescription.setError("Descrição obrigatória");
            return;
        }
        
        if (pointsStr.isEmpty()) {
            etPoints.setError("Pontos obrigatórios");
            return;
        }
        
        int points;
        try {
            points = Integer.parseInt(pointsStr);
        } catch (NumberFormatException e) {
            etPoints.setError("Pontos inválidos");
            return;
        }
        
        // Verificar se paciente foi selecionado
        if (spPatient.getSelectedItem() == null) {
            Toast.makeText(this, "Selecione um paciente", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Patient selectedPatient = (Patient) spPatient.getSelectedItem();
        int frequency = spFrequency.getSelectedItemPosition() + 1;  // 1-7
        
        // Criar tarefa com dados validados
        createTask(title, description, points, selectedPatient.getId(), frequency);
    });
}
```

---

## 🧪 **TESTAR CRIAÇÃO MANUALMENTE**

### **Como testar com curl:**

```bash
# 1. Fazer login para pegar token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"seu_email@profissional.com","password":"sua_senha"}'

# 2. Criar tarefa com dados CORRETOS
curl -X POST http://localhost:8080/tasks \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "patient_id": 2,
    "title": "Fazer alongamento",
    "description": "Realizar alongamento por 10 minutos pela manhã",
    "points_value": 15,
    "frequency_per_week": 3,
    "start_date": "2024-01-01"
  }'
```

---

## 🚨 **ERROS COMUNS E SOLUÇÕES**

### **Erro 422 - Causas Comuns:**

1. **patient_id inválido ou inexistente**
   ```json
   // ERRADO
   {"patient_id": 999}
   
   // CORRETO - usar ID real
   {"patient_id": 2}
   ```

2. **title vazio ou muito longo**
   ```json
   // ERRADO
   {"title": ""}
   
   // CORRETO
   {"title": "Fazer alongamento"}
   ```

3. **description vazia**
   ```json
   // ERRADO
   {"description": ""}
   
   // CORRETO
   {"description": "Realizar alongamento por 10 minutos"}
   ```

4. **start_date em formato inválido**
   ```json
   // ERRADO
   {"start_date": "01/01/2024"}
   
   // CORRETO
   {"start_date": "2024-01-01"}
   ```

5. **points_value fora do range**
   ```json
   // ERRADO
   {"points_value": 0}
   {"points_value": 1001}
   
   // CORRETO
   {"points_value": 15}
   ```

---

## ✅ **CHECKLIST ANTES DE ENVIAR**

- [ ] **patient_id** é um ID de paciente real (2 ou 3)
- [ ] **title** não está vazio e tem < 120 caracteres
- [ ] **description** não está vazia e tem < 2000 caracteres
- [ ] **points_value** está entre 1 e 1000
- [ ] **frequency_per_week** está entre 1 e 7
- [ ] **start_date** está no formato YYYY-MM-DD
- [ ] **Token JWT** é válido
- [ ] **Usuário** tem permissão de profissional

---

## 🎯 **RESULTADO ESPERADO**

1. **Preencher todos os campos obrigatórios**
2. **Selecionar paciente real**
3. **Clicar em "Criar"**
4. **Receber sucesso** com ID da tarefa
5. **Tarefa aparece** na lista do paciente

---

## 🚀 **FLUXO CORRETO**

```
Preencher campos → Validar → Criar Request → Enviar API → Sucesso
```

**Agora a criação de tarefas vai funcionar! 🎉**
