# ✅ **INSTRUÇÕES PARA GEMINI - SISTEMA DE DELEÇÃO DE EXERCÍCIOS**

## 🎯 **OBJETIVO**

Implementar no frontend a funcionalidade de deleção de exercícios, controlada apenas para profissionais.

---

## 📋 **O QUE PRECISA SER IMPLEMENTADO**

### **🔧 1. Atualizar TaskApi.java**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/network/TaskApi.java`

**Adicionar método:**
```java
@DELETE("exercises/{exercise_id}")
Call<DeleteExerciseResponse> deleteExercise(
    @Header("Authorization") String token, 
    @Path("exercise_id") int exerciseId
);

@GET("exercises/manage/{patient_id}")
Call<ManageExercisesResponse> getExercisesForManagement(
    @Header("Authorization") String token,
    @Path("patient_id") int patientId
);
```

### **🔧 2. Criar modelos de resposta**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/models/DeleteExerciseResponse.java`

```java
package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class DeleteExerciseResponse {
    @SerializedName("success")
    private Boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("exercise_id")
    private Integer exerciseId;
    
    @SerializedName("deleted_from_patients")
    private List<Integer> deletedFromPatients;
    
    @SerializedName("deleted_by")
    private DeletedByInfo deletedBy;
    
    // Getters
    public Boolean isSuccess() { return success != null ? success : false; }
    public String getMessage() { return message; }
    public Integer getExerciseId() { return exerciseId; }
    public List<Integer> getDeletedFromPatients() { return deletedFromPatients; }
    public DeletedByInfo getDeletedBy() { return deletedBy; }
    
    // Classe aninhada
    public static class DeletedByInfo {
        @SerializedName("id")
        private Integer id;
        
        @SerializedName("role")
        private String role;
        
        @SerializedName("email")
        private String email;
        
        // Getters
        public Integer getId() { return id; }
        public String getRole() { return role; }
        public String getEmail() { return email; }
    }
}
```

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/models/ManageExercisesResponse.java`

```java
package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ManageExercisesResponse {
    @SerializedName("success")
    private Boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("patient_id")
    private Integer patientId;
    
    @SerializedName("total_exercises")
    private Integer totalExercises;
    
    @SerializedName("exercises")
    private List<ManageExerciseItem> exercises;
    
    // Getters
    public Boolean isSuccess() { return success != null ? success : false; }
    public String getMessage() { return message; }
    public Integer getPatientId() { return patientId; }
    public Integer getTotalExercises() { return totalExercises; }
    public List<ManageExerciseItem> getExercises() { return exercises; }
    
    public static class ManageExerciseItem {
        @SerializedName("id")
        private Integer id;
        
        @SerializedName("title")
        private String title;
        
        @SerializedName("description")
        private String description;
        
        @SerializedName("points_value")
        private Integer pointsValue;
        
        @SerializedName("frequency_per_week")
        private Integer frequencyPerWeek;
        
        @SerializedName("is_active")
        private Boolean isActive;
        
        @SerializedName("created_at")
        private String createdAt;
        
        @SerializedName("can_delete")
        private Boolean canDelete;
        
        @SerializedName("assigned_by")
        private String assignedBy;
        
        @SerializedName("assigned_at")
        private String assignedAt;
        
        // Getters
        public Integer getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public Integer getPointsValue() { return pointsValue; }
        public Integer getFrequencyPerWeek() { return frequencyPerWeek; }
        public Boolean getIsActive() { return isActive; }
        public String getCreatedAt() { return createdAt; }
        public Boolean getCanDelete() { return canDelete; }
        public String getAssignedBy() { return assignedBy; }
        public String getAssignedAt() { return assignedAt; }
    }
}
```

### **🔧 3. Criar ExerciseManagementActivity.java**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/ExerciseManagementActivity.java`

```java
package com.example.testbackend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.adapters.ManageExerciseAdapter;
import com.example.testbackend.models.DeleteExerciseResponse;
import com.example.testbackend.models.ManageExercisesResponse;
import com.example.testbackend.models.Task;
import com.example.testbackend.models.UserProfileResponse;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.TaskApi;
import com.example.testbackend.utils.TokenManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseManagementActivity extends AppCompatActivity {
    
    private static final String TAG = "EXERCISE_MANAGEMENT";
    
    private RecyclerView recyclerView;
    private ManageExerciseAdapter adapter;
    private TaskApi taskApi;
    private TokenManager tokenManager;
    private TextView tvEmptyState;
    private List<ManageExercisesResponse.ManageExerciseItem> exerciseList;
    private int currentPatientId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_management);
        
        tokenManager = new TokenManager(this);
        taskApi = ApiClient.getTaskApi();
        
        // Obter ID do paciente dos extras
        currentPatientId = getIntent().getIntExtra("patient_id", 0);
        
        initViews();
        loadExercises();
    }
    
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewExercises);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        
        adapter = new ManageExerciseAdapter(exerciseList, this::deleteExercise, this::canDeleteExercise);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        // Botão voltar
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
    
    private void canDeleteExercise(int exerciseId): boolean {
        // Verificar se usuário é profissional
        String userRole = tokenManager.getUserRole();
        return "professional".equals(userRole) || "doctor".equals(userRole) || "admin".equals(userRole);
    }
    
    private void loadExercises() {
        String token = tokenManager.getAuthToken();
        if (token == null) return;
        
        Log.d(TAG, "Carregando exercícios para gerenciamento do paciente: " + currentPatientId);
        
        taskApi.getExercisesForManagement(currentPatientId, token).enqueue(new Callback<ManageExercisesResponse>() {
            @Override
            public void onResponse(Call<ManageExercisesResponse> call, Response<ManageExercisesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ManageExercisesResponse manageResponse = response.body();
                    if (manageResponse.isSuccess()) {
                        exerciseList = manageResponse.getExercises();
                        updateUI();
                        Log.d(TAG, "Carregados " + exerciseList.size() + " exercícios para gerenciamento");
                    } else {
                        showError(manageResponse.getMessage());
                    }
                } else {
                    showError("Erro ao carregar exercícios");
                }
            }
            
            @Override
            public void onFailure(Call<ManageExercisesResponse> call, Throwable t) {
                Log.e(TAG, "Falha ao carregar exercícios", t);
                showError("Falha de conexão");
            }
        });
    }
    
    private void deleteExercise(int exerciseId) {
        if (!canDeleteExercise(exerciseId)) {
            Toast.makeText(this, "Apenas profissionais podem deletar exercícios", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Encontrar exercício para mostrar nome na confirmação
        ManageExercisesResponse.ManageExerciseItem exerciseToDelete = null;
        for (ManageExercisesResponse.ManageExerciseItem exercise : exerciseList) {
            if (exercise.getId().equals(exerciseId)) {
                exerciseToDelete = exercise;
                break;
            }
        }
        
        if (exerciseToDelete == null) {
            Toast.makeText(this, "Exercício não encontrado", Toast.LENGTH_SHORT).show();
            return;
        }
        
        new AlertDialog.Builder(this)
            .setTitle("Deletar Exercício")
            .setMessage("Tem certeza que deseja deletar o exercício \"" + exerciseToDelete.getTitle() + "\"?")
            .setPositiveButton("Deletar", (dialog, which) -> {
                performDelete(exerciseId);
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }
    
    private void performDelete(int exerciseId) {
        String token = tokenManager.getAuthToken();
        if (token == null) return;
        
        Log.d(TAG, "Deletando exercício: " + exerciseId);
        
        taskApi.deleteExercise(exerciseId, token).enqueue(new Callback<DeleteExerciseResponse>() {
            @Override
            public void onResponse(Call<DeleteExerciseResponse> call, Response<DeleteExerciseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DeleteExerciseResponse deleteResponse = response.body();
                    if (deleteResponse.isSuccess()) {
                        Toast.makeText(ExerciseManagementActivity.this, deleteResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        
                        // Remover da lista local
                        exerciseList.removeIf(exercise -> exercise.getId().equals(exerciseId));
                        adapter.notifyDataSetChanged();
                        
                        Log.d(TAG, "Exercício deletado: " + deleteResponse.getExerciseId());
                        updateUI();
                    } else {
                        showError(deleteResponse.getMessage());
                    }
                } else {
                    showError("Erro ao deletar exercício");
                }
            }
            
            @Override
            public void onFailure(Call<DeleteExerciseResponse> call, Throwable t) {
                Log.e(TAG, "Falha ao deletar exercício", t);
                showError("Falha de conexão");
            }
        });
    }
    
    private void updateUI() {
        if (exerciseList == null || exerciseList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
```

### **🔧 4. Criar ManageExerciseAdapter.java**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/adapters/ManageExerciseAdapter.java`

```java
package com.example.testbackend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testbackend.R;
import com.example.testbackend.models.ManageExercisesResponse;

import java.util.List;
import java.util.function.Function;

public class ManageExerciseAdapter extends RecyclerView.Adapter<ManageExerciseAdapter.ExerciseViewHolder> {
    
    private List<ManageExercisesResponse.ManageExerciseItem> exerciseList;
    private Function<Integer, Void> onDeleteClick;
    private Function<Integer, Boolean> canDelete;
    
    public ManageExerciseAdapter(List<ManageExercisesResponse.ManageExerciseItem> exerciseList, 
                              Function<Integer, Void> onDeleteClick,
                              Function<Integer, Boolean> canDelete) {
        this.exerciseList = exerciseList;
        this.onDeleteClick = onDeleteClick;
        this.canDelete = canDelete;
    }
    
    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_manage_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        ManageExercisesResponse.ManageExerciseItem exercise = exerciseList.get(position);
        
        holder.tvTitle.setText(exercise.getTitle());
        holder.tvDescription.setText(exercise.getDescription());
        holder.tvPoints.setText(exercise.getPointsValue() + " pontos");
        holder.tvFrequency.setText(exercise.getFrequencyPerWeek() + "x/semana");
        
        // Controlar visibilidade do botão deletar
        boolean canDeleteThis = canDelete.apply(exercise.getId());
        holder.btnDelete.setVisibility(canDeleteThis ? View.VISIBLE : View.GONE);
        
        // Configurar clique do botão
        holder.btnDelete.setOnClickListener(v -> {
            if (canDeleteThis) {
                onDeleteClick.apply(exercise.getId());
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return exerciseList != null ? exerciseList.size() : 0;
    }
    
    public void updateExercises(List<ManageExercisesResponse.ManageExerciseItem> newExercises) {
        this.exerciseList = newExercises;
        notifyDataSetChanged();
    }
    
    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvPoints, tvFrequency;
        ImageButton btnDelete;
        
        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvExerciseTitle);
            tvDescription = itemView.findViewById(R.id.tvExerciseDescription);
            tvPoints = itemView.findViewById(R.id.tvExercisePoints);
            tvFrequency = itemView.findViewById(R.id.tvExerciseFrequency);
            btnDelete = itemView.findViewById(R.id.btnDeleteExercise);
        }
    }
}
```

### **🔧 5. Criar layouts**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/layout/activity_exercise_management.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp">

    <!-- Toolbar -->
    <com.google.android.material.appbar.MaterialToolbar
        android:id="@+id/toolbar"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        app:title="Gerenciar Exercícios"
        app:navigationIcon="@drawable/ic_arrow_back"
        style="@style/Widget.Material3.Toolbar" />

    <!-- Estado vazio -->
    <TextView
        android:id="@+id/tvEmptyState"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Nenhum exercício encontrado"
        android:textAppearance="@style/TextAppearance.Material3.BodyLarge"
        android:visibility="gone"
        app:layout_constraintTop_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- Lista de exercícios -->
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerViewExercises"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_marginTop="16dp"
        app:layout_constraintTop_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- Botão voltar -->
    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnBack"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="Voltar"
        app:layout_constraintTop_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/layout/item_manage_exercise.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="8dp"
    app:cardCornerRadius="8dp"
    app:cardElevation="4dp">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="16dp">

        <!-- Título -->
        <TextView
            android:id="@+id/tvExerciseTitle"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:text="Título do Exercício"
            android:textAppearance="@style/TextAppearance.Material3.BodyLarge"
            android:textStyle="bold"
            android:maxLines="2"
            android:ellipsize="end"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

        <!-- Descrição -->
        <TextView
            android:id="@+id/tvExerciseDescription"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:text="Descrição do exercício"
            android:textAppearance="@style/TextAppearance.Material3.BodyMedium"
            android:maxLines="3"
            android:ellipsize="end"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@id/tvExerciseTitle" />

        <!-- Informações -->
        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:orientation="horizontal"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@id/tvExerciseDescription">

            <TextView
                android:id="@+id/tvExercisePoints"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="15 pontos"
                android:textAppearance="@style/TextAppearance.Material3.BodySmall"
                android:textColor="?android:attr/colorPrimary" />

            <TextView
                android:id="@+id/tvExerciseFrequency"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginStart="8dp"
                android:text="3x/semana"
                android:textAppearance="@style/TextAppearance.Material3.BodySmall"
                android:textColor="?android:attr/colorSecondary" />

        </LinearLayout>

        <!-- Botão deletar -->
        <ImageButton
            android:id="@+id/btnDeleteExercise"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:layout_marginTop="8dp"
            android:src="@android:drawable/ic_delete"
            android:background="?attr/selectableItemBackground"
            android:contentDescription="Deletar exercício"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

    </androidx.constraintlayout.widget.ConstraintLayout>

</com.google.android.material.card.MaterialCardView>
```

### **🔧 6. Modificar ExerciseListActivity.java**

**Adicionar botão de gerenciamento:**
```java
// No método setupNavigation() ou onde configura os cliques:
if (canManageExercises()) {
    findViewById(R.id.btnManageExercises).setVisibility(View.VISIBLE);
    findViewById(R.id.btnManageExercises).setOnClickListener(v -> {
        Intent intent = new Intent(ExerciseListActivity.this, ExerciseManagementActivity.class);
        intent.putExtra("patient_id", getCurrentPatientId());
        startActivity(intent);
    });
}

private boolean canManageExercises() {
    String userRole = tokenManager.getUserRole();
    return "professional".equals(userRole) || "doctor".equals(userRole) || "admin".equals(userRole);
}

private int getCurrentPatientId() {
    // Obter ID do paciente atual (pode vir dos exercícios)
    if (!exerciseList.isEmpty()) {
        return exerciseList.get(0).getPatientId();
    }
    return 0; // Default ou obter de outra forma
}
```

---

## 🎮 **FLUXO ESPERADO**

### **✅ Para profissionais:**
1. **ExerciseListActivity** → Botão "Gerenciar Exercícios" visível
2. **ExerciseManagementActivity** → Lista completa com botões de deletar
3. **Confirmação** → Dialog antes de deletar
4. **Deleção** → Chamada à API com DELETE
5. **Feedback** → Toast e atualização da lista

### **🛡️ Para pacientes:**
1. **Botão "Gerenciar" invisível**
2. **Sem acesso** à deleção (erro 403)
3. **Interface limpa** focada apenas em visualização

---

## 🎯 **BENEFÍCIOS**

### **✅ Segurança:**
- **Controle de permissão** - Apenas profissionais podem deletar
- **Verificação de role** - Em tempo real no frontend
- **Confirmação obrigatória** - Prevenção de deleção acidental

### **✅ Experiência:**
- **Interface intuitiva** - Cards claros com informações
- **Feedback imediato** - Toast de sucesso/erro
- **Navegação fluida** - Voltar para lista principal

### **✅ Funcionalidade:**
- **Deleção em múltiplos pacientes** - Backend remove de todos
- **Logs completos** - Auditoria de ações
- **Atualização automática** - Lista atualizada após deleção

---

## 🚀 **IMPLEMENTAÇÃO PRIORITÁRIA**

### **Backend:**
- ✅ Endpoint DELETE `/exercises/{exercise_id}`
- ✅ Endpoint GET `/exercises/manage/{patient_id}`
- ✅ Controle de permissão
- ✅ Logs detalhados

### **Frontend (para Gemini):**
1. ✅ TaskApi atualizada
2. ✅ Modelos criados
3. ✅ ExerciseManagementActivity
4. ✅ ManageExerciseAdapter
5. ✅ Layouts criados
6. ✅ ExerciseListActivity modificada

**O sistema completo de deleção está pronto para implementação! Apenas profissionais poderão deletar exercícios com segurança e auditoria completa! 🎯**
