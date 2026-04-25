# 🤖 **INSTRUÇÕES PARA GEMINI - IMPLEMENTAÇÃO PROGRESSO DIÁRIO**

## 🎯 **OBJETIVO**

Implementar no frontend o sistema de progresso diário que mostra a porcentagem de exercícios completados hoje na tela home do paciente.

---

## 📋 **O QUE O GEMINI PRECISA FAZER**

### **🔧 1. Criar novos modelos de resposta**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/models/DailyProgressResponse.java`

**Cadastrar o arquivo com:**
```java
package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class DailyProgressResponse {
    @SerializedName("success")
    private Boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private DailyProgressData data;
    
    // Getters
    public Boolean isSuccess() { return success != null ? success : false; }
    public String getMessage() { return message; }
    public DailyProgressData getData() { return data; }
}
```

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/models/DailyProgressData.java`

**Cadastrar o arquivo com:**
```java
package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class DailyProgressData {
    @SerializedName("user_id")
    private Integer userId;
    
    @SerializedName("date")
    private String date;
    
    @SerializedName("total_daily_exercises")
    private Integer totalDailyExercises;
    
    @SerializedName("completed_today")
    private Integer completedToday;
    
    @SerializedName("remaining_today")
    private Integer remainingToday;
    
    @SerializedName("progress_percentage")
    private Double progressPercentage;
    
    @SerializedName("progress_fraction")
    private String progressFraction;
    
    @SerializedName("is_complete")
    private Boolean isComplete;
    
    @SerializedName("status_message")
    private String statusMessage;
    
    // Getters
    public Integer getUserId() { return userId; }
    public String getDate() { return date; }
    public Integer getTotalDailyExercises() { return totalDailyExercises; }
    public Integer getCompletedToday() { return completedToday; }
    public Integer getRemainingToday() { return remainingToday; }
    public Double getProgressPercentage() { return progressPercentage; }
    public String getProgressFraction() { return progressFraction; }
    public Boolean getIsComplete() { return isComplete; }
    public String getStatusMessage() { return statusMessage; }
}
```

---

### **🔧 2. Atualizar TaskApi.java**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/network/TaskApi.java`

**Adicionar novos métodos:**
```java
// Adicionar imports no topo do arquivo
import com.example.testbackend.models.DailyProgressResponse;
import com.example.testbackend.models.DailyProgressData;

// Adicionar estes métodos na classe TaskApi
@GET("progress/daily")
Call<DailyProgressResponse> getDailyProgress(@Header("Authorization") String token);

@GET("progress/detailed")
Call<DetailedProgressResponse> getDetailedProgress(@Header("Authorization") String token);
```

---

### **🔧 3. Modificar MainActivity.java**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/MainActivity.java`

**Adicionar imports no topo:**
```java
import com.example.testbackend.models.DailyProgressResponse;
import com.example.testbackend.models.DailyProgressData;
import com.google.android.material.progressindicator.CircularProgressIndicator;
```

**Adicionar variáveis globais (após as variáveis existentes):**
```java
private CircularProgressIndicator progressWeekly;
private TextView tvProgressValue;
```

**Modificar método `initViews()` para incluir:**
```java
private void initViews() {
    tvUserInitial = findViewById(R.id.tvUserInitial);
    tvGreeting = findViewById(R.id.tvGreeting);
    ivUserPhoto = findViewById(R.id.ivUserPhoto);
    cardAccountAvatar = findViewById(R.id.cardAccountAvatar);
    btnSettings = findViewById(R.id.btnSettings);
    cardProfessionalExams = findViewById(R.id.cardProfessionalExams);

    // 🔥 **INICIALIZAR COMPONENTES DE PROGRESSO**
    progressWeekly = findViewById(R.id.progressWeekly);
    tvProgressValue = findViewById(R.id.tvProgressValue);

    cardAccountAvatar.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    if (btnSettings != null) {
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }
}
```

**Adicionar método para carregar progresso:**
```java
private void loadDailyProgress() {
    String token = tokenManager.getAuthToken();
    if (token == null) return;
    
    taskApi.getDailyProgress(token).enqueue(new Callback<DailyProgressResponse>() {
        @Override
        public void onResponse(Call<DailyProgressResponse> call, Response<DailyProgressResponse> response) {
            if (response.isSuccessful() && response.body() != null) {
                DailyProgressResponse progressResponse = response.body();
                if (progressResponse.isSuccess()) {
                    updateProgressUI(progressResponse.getData());
                }
            } else {
                Log.e(TAG, "Erro ao carregar progresso: " + response.code());
                // Manter valores padrão em caso de erro
                updateProgressUI(null);
            }
        }
        
        @Override
        public void onFailure(Call<DailyProgressResponse> call, Throwable t) {
            Log.e(TAG, "Falha ao carregar progresso", t);
            // Manter valores padrão em caso de erro
            updateProgressUI(null);
        }
    });
}
```

**Adicionar método para atualizar UI:**
```java
private void updateProgressUI(DailyProgressData progressData) {
    if (progressData == null) {
        // Valores padrão em caso de erro
        progressWeekly.setProgress(0);
        tvProgressValue.setText("0%");
        return;
    }
    
    // 🔥 **ATUALIZAR PROGRESSO**
    int progressInt = progressData.getProgressPercentage().intValue();
    progressWeekly.setProgress(progressInt);
    tvProgressValue.setText(progressData.getProgressPercentage().intValue() + "%");
    
    // 🔥 **LOG PARA DEBUG**
    Log.d(TAG, "Progresso atualizado: " + progressData.getProgressFraction() + 
              " (" + progressData.getProgressPercentage() + "%)");
}
```

**Modificar método `onCreate()` para incluir o carregamento:**
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    // FORÇAR TEMA SALVO
    SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
    int themeMode = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_NO);
    AppCompatDelegate.setDefaultNightMode(themeMode);

    super.onCreate(savedInstanceState);
    
    tokenManager = new TokenManager(this);
    
    if (!tokenManager.isLoggedIn()) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        return;
    }

    // 🔥 ADICIONAR: Verificar se usuário é paciente
    if (!isPatientUser()) {
        redirectToCorrectActivity();
        return;
    }

    setContentView(R.layout.activity_main);
    
    initViews();
    loadUserProfile();
    loadDailyProgress();  // 🔥 **ADICIONAR ESTA LINHA**
    setupNavigation();
}
```

**Modificar método `onResume()` para atualizar progresso:**
```java
@Override
protected void onResume() {
    super.onResume();
    if (tokenManager.isLoggedIn()) {
        loadUserProfile();
        loadDailyProgress();  // 🔥 **ADICIONAR ESTA LINHA**
    }
}
```

**Modificar método `completeTaskOnBackend()` em ExerciseListActivity para atualizar progresso:**
```java
// No final do método onResponse onde tem sucesso
if (result.isSuccess()) {
    Toast.makeText(ExerciseListActivity.this, 
        "Tarefa concluída! +" + result.getPointsAwarded() + " pontos", 
        Toast.LENGTH_SHORT).show();
    updateTaskAsCompleted(task);
    updateUserPoints();
    
    // 🔥 **ATUALIZAR PROGRESSO NA HOME**
    // Se tiver referência à MainActivity, pode atualizar
    // Ou simplesmente o usuário verá ao voltar para a home
}
```

---

### **🔧 4. Atualizar ProgressActivity.java (se existir)**

**Se o arquivo existir, modificar para mostrar dados detalhados:**
```java
// Adicionar imports
import com.example.testbackend.models.DailyProgressResponse;
import com.example.testbackend.models.DailyProgressData;

// No método que carrega dados, usar o novo endpoint
private void loadDetailedProgress() {
    String token = tokenManager.getAuthToken();
    if (token == null) return;
    
    taskApi.getDetailedProgress(token).enqueue(new Callback<DetailedProgressResponse>() {
        @Override
        public void onResponse(Call<DetailedProgressResponse> call, Response<DetailedProgressResponse> response) {
            if (response.isSuccessful() && response.body() != null) {
                DetailedProgressResponse detailedResponse = response.body();
                if (detailedResponse.isSuccess()) {
                    updateDetailedProgressUI(detailedResponse);
                }
            }
        }
        
        @Override
        public void onFailure(Call<DetailedProgressResponse> call, Throwable t) {
            Log.e("ProgressActivity", "Falha ao carregar progresso detalhado", t);
        }
    });
}
```

---

## 🎮 **FLUXO ESPERADO APÓS IMPLEMENTAÇÃO**

### **📱 MainActivity com progresso dinâmico:**
1. **Ao abrir o app** - Carrega progresso do backend
2. **Mostra porcentagem** - Ex: "40%" (2/5 exercícios)
3. **Atualiza automaticamente** - Após completar exercícios
4. **Mensagens motivacionais** - Status baseado no progresso

### **📅 Exemplos de UI:**
- **0 exercícios:** "0%" + "Comece seus exercícios hoje! 💪"
- **1 exercício:** "20%" + "Bom começo! Continue assim! 🌱"
- **2 exercícios:** "40%" + "Você está no caminho certo! 🚶‍♂️"
- **4 exercícios:** "80%" + "Quase lá! Você consegue! 🔥"
- **5 exercícios:** "100%" + "Parabéns! Meta diária alcançada! 🎉"

---

## 🔍 **COMO TESTAR**

### **📋 Teste 1 - Progresso inicial:**
1. **Abra o app** - Deve mostrar "0%"
2. **Verifique logs** - Deve mostrar "Progresso atualizado: 0/5 (0.0%)"

### **📋 Teste 2 - Após completar 1 exercício:**
1. **Complete 1 exercício** - Vá para ExerciseListActivity
2. **Volte para home** - Deve mostrar "20%"
3. **Verifique logs** - Deve mostrar "Progresso atualizado: 1/5 (20.0%)"

### **📋 Teste 3 - Após completar 2 exercícios:**
1. **Complete outro exercício** - ID diferente
2. **Volte para home** - Deve mostrar "40%"
3. **Verifique logs** - Deve mostrar "Progresso atualizado: 2/5 (40.0%)"

---

## 🚨 **IMPORTANTE**

### **❌ NÃO MEXER EM:**
- Nenhuma outra parte do frontend
- Layouts XML (exceto se necessário)
- Outras Activities
- Configurações de build

### **✅ FOCAR APENAS EM:**
- Criar os 2 novos modelos (DailyProgressResponse e DailyProgressData)
- Atualizar TaskApi com novos endpoints
- Modificar MainActivity para carregar e mostrar progresso
- Atualizar ProgressActivity se existir

---

## 📋 **VERIFICAÇÃO FINAL**

### **✅ Backend pronto:**
- Endpoint `/progress/daily` funcionando
- Sistema de cálculo automático
- Logs detalhados para debug

### **✅ Frontend esperado:**
- MainActivity mostra progresso dinâmico
- Porcentagem atualizada automaticamente
- Logs para debug funcionando
- Experiência motivacional implementada

---

## 🎯 **RESULTADO FINAL ESPERADO**

### **✅ Experiência do usuário:**
```
📱 Tela Home:
├── Saudação: "Olá, João!"
├── Progresso: "40%" (círculo progressivo)
├── Fração: "2/5 exercícios hoje"
└── Status: "Você está no caminho certo! 🚶‍♂️"

📅 Após completar exercício:
├── Toast: "Tarefa concluída! +15 pontos"
├── Volta para home
└── Progresso atualizado: "60%" (3/5)
```

---

## 📋 **IMPLEMENTAÇÃO PRIORITÁRIA**

1. **Criar modelos** - DailyProgressResponse.java e DailyProgressData.java
2. **Atualizar TaskApi** - Adicionar getDailyProgress()
3. **Modificar MainActivity** - loadDailyProgress() e updateProgressUI()
4. **Testar fluxo completo** - Do 0% ao 100%

---

## 🚀 **STATUS FINAL**

**Backend está 100% pronto e funcionando!**

**O Gemini precisa implementar:**
1. ✅ Modelos de resposta
2. ✅ TaskApi atualizada  
3. ✅ MainActivity modificada
4. ✅ Sistema de progresso dinâmico

**O sistema mostrará a porcentagem real de exercícios completados hoje na home do paciente! 🎯**
