# 🤖 **INSTRUÇÕES PARA GEMINI - IMPLEMENTAÇÃO FRONTEND**

## 🎯 **OBJETIVO**

Implementar no frontend o suporte para **Task ID específico** para que cada exercício tenha seu próprio controle de repetição diária.

---

## 📋 **O QUE O GEMINI PRECISA FAZER**

### **🔧 1. Atualizar TaskApi.java**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/network/TaskApi.java`

**Mudança necessária:**
```java
// ANTES:
@POST("tasks/complete-task")
Call<TaskCompletionResponse> completeTask(@Header("Authorization") String token);

// DEPOIS:
@POST("tasks/complete-task")
Call<TaskCompletionResponse> completeTask(@Header("Authorization") String token, @Body TaskCompletionRequest request);
```

---

### **🔧 2. Atualizar TaskCompletionRequest.java**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/models/TaskCompletionRequest.java`

**Adicionar getters e construtores:**
```java
package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class TaskCompletionRequest {
    @SerializedName("task_id")
    private Integer taskId;
    @SerializedName("completion_notes")
    private String completionNotes;

    // Getters (ADICIONAR)
    public Integer getTaskId() { return taskId; }
    public String getCompletionNotes() { return completionNotes; }
    
    // Construtores (ADICIONAR)
    public TaskCompletionRequest(Integer taskId) {
        this.taskId = taskId;
        this.completionNotes = "";
    }
    
    public TaskCompletionRequest(Integer taskId, String completionNotes) {
        this.taskId = taskId;
        this.completionNotes = completionNotes;
    }

    // Setters (JÁ EXISTEM)
    public void setTaskId(Integer taskId) { this.taskId = taskId; }
    public void setCompletionNotes(String completionNotes) { this.completionNotes = completionNotes; }
}
```

---

### **🔧 3. Atualizar ExerciseListActivity.java**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/ExerciseListActivity.java`

**Mudança no método `completeTaskOnBackend()`:**
```java
private void completeTaskOnBackend(Task task) {
    String token = tokenManager.getAuthToken();
    if (token == null || taskApi == null) return;
    
    // 🔥 **CRIA REQUEST COM ID REAL DA TAREFA**
    TaskCompletionRequest request = new TaskCompletionRequest(task.getId());
    
    taskApi.completeTask(token, request).enqueue(new Callback<TaskCompletionResponse>() {
        @Override
        public void onResponse(Call<TaskCompletionResponse> call, Response<TaskCompletionResponse> response) {
            if (isFinishing()) return;
            
            if (response.isSuccessful() && response.body() != null) {
                TaskCompletionResponse result = response.body();
                
                // 🔥 **VERIFICA SE TAREFA FOI PERMITIDA**
                if (result.isSuccess()) {
                    Toast.makeText(ExerciseListActivity.this, 
                        "Tarefa concluída! +" + result.getPointsAwarded() + " pontos", 
                        Toast.LENGTH_SHORT).show();
                    updateTaskAsCompleted(task);
                    updateUserPoints();
                    
                    // 🔥 **MOSTRA PROGRESSÃO DIÁRIA**
                    if (result.getTasksCompletedToday() != null && result.getRemainingTasks() != null) {
                        String progressMsg = "Progresso: " + result.getTasksCompletedToday() + 
                                          "/5 tarefas hoje";
                        Toast.makeText(ExerciseListActivity.this, progressMsg, Toast.LENGTH_LONG).show();
                    }
                } else {
                    // 🔥 **TRATA BLOQUEIO DE REPETIÇÃO**
                    String message = result.getMessage();
                    if (result.getCanRepeatTomorrow() != null && result.getCanRepeatTomorrow()) {
                        message += "\n\n📅 Você poderá repetir este exercício amanhã!";
                    }
                    Toast.makeText(ExerciseListActivity.this, message, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(ExerciseListActivity.this, "Erro ao completar tarefa", Toast.LENGTH_SHORT).show();
            }
        }
        
        @Override
        public void onFailure(Call<TaskCompletionResponse> call, Throwable t) {
            Toast.makeText(ExerciseListActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
        }
    });
}
```

---

### **🔧 4. Atualizar TaskCompletionResponse.java**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/models/TaskCompletionResponse.java`

**Adicionar novos campos:**
```java
// ADICIONAR ESTES CAMPOS:
@SerializedName("success")
private Boolean success;

@SerializedName("can_repeat_tomorrow")
private Boolean canRepeatTomorrow;

@SerializedName("tasks_completed_today")
private Integer tasksCompletedToday;

@SerializedName("remaining_tasks")
private Integer remainingTasks;

// ADICIONAR ESTES GETTERS:
public Boolean isSuccess() { return success != null ? success : true; }
public Boolean getCanRepeatTomorrow() { return canRepeatTomorrow; }
public Integer getTasksCompletedToday() { return tasksCompletedToday; }
public Integer getRemainingTasks() { return remainingTasks; }
```

---

## 🎯 **FLUXO ESPERADO APÓS IMPLEMENTAÇÃO**

### **📱 Exercício A (ID: 1) - Primeira vez:**
```
✅ Tarefa concluída! +15 pontos
📊 Progresso: 1/5 tarefas hoje
```

### **📱 Exercício B (ID: 2) - Diferente:**
```
✅ Tarefa concluída! +15 pontos
📊 Progresso: 2/5 tarefas hoje
```

### **📱 Exercício A (ID: 1) - Repetição:**
```
❌ Este exercício já foi completado hoje. Tente novamente amanhã!
📅 Você poderá repetir este exercício amanhã!
```

### **📱 Exercício C (ID: 3) - Novo:**
```
✅ Tarefa concluída! +15 pontos
📊 Progresso: 3/5 tarefas hoje
```

---

## 🔍 **O QUE JÁ FOI FEITO NO BACKEND**

### **✅ Backend pronto:**
- Endpoint `/complete-task` agora recebe `task_id` dinâmico
- Sistema de controle usa ID específico da tarefa
- Retorna resposta com campos específicos:
  - `success`: true/false
  - `can_repeat_tomorrow`: true/false
  - `tasks_completed_today`: número
  - `remaining_tasks`: número

---

## 🚨 **IMPORTANTE**

### **❌ NÃO MEXER EM:**
- Nenhuma outra parte do frontend
- Layouts XML
- Outras Activities
- Configurações de build

### **✅ FOCAR APENAS EM:**
- `TaskApi.java` - Atualizar método completeTask
- `TaskCompletionRequest.java` - Adicionar getters/construtores
- `TaskCompletionResponse.java` - Adicionar novos campos
- `ExerciseListActivity.java` - Atualizar completeTaskOnBackend

---

## 🧪 **COMO TESTAR**

### **1. Build e run do app**
### **2. Faça login**
### **3. Complete exercício A**
- Deve mostrar: "Tarefa concluída! +15 pontos"
- Deve mostrar: "Progresso: 1/5 tarefas hoje"

### **4. Complete exercício B**
- Deve mostrar: "Tarefa concluída! +15 pontos"
- Deve mostrar: "Progresso: 2/5 tarefas hoje"

### **5. Tente repetir exercício A**
- Deve mostrar: "Este exercício já foi completado hoje. Tente novamente amanhã!"
- Deve mostrar: "📅 Você poderá repetir este exercício amanhã!"

### **6. Complete exercício C**
- Deve mostrar: "Tarefa concluída! +15 pontos"
- Deve mostrar: "Progresso: 3/5 tarefas hoje"

---

## 🎯 **RESULTADO FINAL**

Cada exercício terá seu próprio controle:
- **Exercício A**: Controlado pelo ID 1
- **Exercício B**: Controlado pelo ID 2
- **Exercício C**: Controlado pelo ID 3

O usuário pode completar **5 exercícios diferentes por dia**, mas **não pode repetir o mesmo exercício no mesmo dia**.

---

## 📋 **RESUMO DAS MUDANÇAS**

1. **TaskApi.java** - Adicionar body parameter
2. **TaskCompletionRequest.java** - Adicionar getters/construtores
3. **TaskCompletionResponse.java** - Adicionar novos campos
4. **ExerciseListActivity.java** - Enviar ID real e tratar resposta

---

## 🚀 **PRÓXIMOS PASSOS**

1. **Implementar as 4 mudanças acima**
2. **Testar o fluxo completo**
3. **Verificar se cada exercício tem controle individual**
4. **Confirmar que pontos somam para exercícios diferentes**

---

## 📞 **SUPPORT**

Se tiver dúvidas, o backend já está pronto e funcionando. O endpoint `/complete-task` está esperando receber:
```json
{
  "task_id": 123
}
```

E retornando:
```json
{
  "success": true,
  "message": "Exercício completado com sucesso!",
  "task_id": 123,
  "points_awarded": 15,
  "tasks_completed_today": 1,
  "remaining_tasks": 4
}
```

**O Gemini só precisa implementar a comunicação no frontend! 🎯**
