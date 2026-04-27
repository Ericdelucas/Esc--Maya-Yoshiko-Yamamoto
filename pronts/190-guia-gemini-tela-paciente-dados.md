# 🏥 **GUIA GEMINI - TELA COMPLETA DE DADOS DO PACIENTE**

## 🎯 **OBJETIVO:**

Criar uma tela completa e profissional para mostrar todos os dados das ferramentas de saúde de um paciente quando clicado em "Meus Pacientes".

---

## 📋 **SITUAÇÃO ATUAL:**

### **✅ Backend:** 100% FUNCIONAL
- **Endpoint pronto:** `/professional/pacientes/{patient_id}/health-tools-test`
- **Dados disponíveis:** Questionários, IMCs, estatísticas
- **Status:** Testado e funcionando

### **❌ Frontend:** PRECISA SER CRIADO
- **Lista pacientes:** existe mas clique não funciona
- **Tela de detalhes:** não existe
- **Navegação:** precisa ser implementada

---

## 🛠️ **TAREFAS PARA O GEMINI:**

### **🔧 ETAPA 1 - VERIFICAR BACKEND**

**Testar endpoint:**
```bash
curl -X GET "http://localhost:8080/professional/pacientes/3/health-tools-test"
```

**Resposta esperada:**
```json
{
  "success": true,
  "patient_info": {
    "id": 3,
    "name": "Nome do Paciente",
    "email": "paciente@email.com"
  },
  "questionnaires": [
    {
      "id": 1,
      "total_score": 15,
      "max_score": 50,
      "risk_level": "Baixo",
      "answers": {"symptoms": "no", "allergies": "no"},
      "created_at": "2026-04-25T20:23:06"
    }
  ],
  "bmis": [
    {
      "id": 1,
      "bmi": 22.86,
      "height": 1.75,
      "weight": 70.0,
      "category": "Normal",
      "created_at": "2026-04-25T20:33:20"
    }
  ],
  "total_records": {
    "questionnaires": 10,
    "bmis": 6
  }
}
```

---

### **📱 ETAPA 2 - ENCONTRAR TELA DE PACIENTES**

**Procurar por:**
- `PatientsListActivity.java`
- `PatientListFragment.java`
- Arquivos com "paciente" ou "patient"

**Usar Ctrl+Shift+F:**
```
pacientes
patients
RecyclerView pacientes
```

---

### **🎯 ETAPA 3 - MODIFICAR CLIQUE NA LISTA**

**Encontrar onde trata clique:**
```java
// Provavelmente algo assim:
@Override
public void onItemClick(Patient patient) {
    // Código atual (vazio ou Toast)
    Toast.makeText(this, "Clicou: " + patient.getName(), Toast.LENGTH_SHORT).show();
}
```

**Trocar para:**
```java
@Override
public void onItemClick(Patient patient) {
    Intent intent = new Intent(this, PatientHealthDetailsActivity.class);
    intent.putExtra("patient_id", patient.getId());
    intent.putExtra("patient_name", patient.getName());
    intent.putExtra("patient_email", patient.getEmail());
    startActivity(intent);
}
```

---

### **📄 ETAPA 4 - CRIAR TELA DE DETALHES**

**Criar arquivo:** `PatientHealthDetailsActivity.java`

```java
package com.example.testbackend;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.testbackend.adapters.QuestionnaireAdapter;
import com.example.testbackend.adapters.BMIAdapter;
import com.example.testbackend.models.PatientHealthResponse;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.ProfessionalApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class PatientHealthDetailsActivity extends AppCompatActivity {
    
    private int patientId;
    private String patientName, patientEmail;
    private TextView patientNameText, patientEmailText, statsText;
    private RecyclerView questionnairesRecyclerView, bmisRecyclerView;
    private ProgressBar progressBar;
    private QuestionnaireAdapter questionnaireAdapter;
    private BMIAdapter bmiAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_health_details);
        
        // Pegar dados da Intent
        patientId = getIntent().getIntExtra("patient_id", 0);
        patientName = getIntent().getStringExtra("patient_name");
        patientEmail = getIntent().getStringExtra("patient_email");
        
        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Dados de Saúde");
            getSupportActionBar().setSubtitle(patientName);
            getSupportActionBar().setHomeAsUpEnabled(true);
        }
        
        // Inicializar views
        initViews();
        
        // Carregar dados
        loadPatientHealthData();
    }
    
    private void initViews() {
        patientNameText = findViewById(R.id.patientNameText);
        patientEmailText = findViewById(R.id.patientEmailText);
        statsText = findViewById(R.id.statsText);
        questionnairesRecyclerView = findViewById(R.id.questionnairesRecyclerView);
        bmisRecyclerView = findViewById(R.id.bmisRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        
        // Configurar informações do paciente
        patientNameText.setText("Paciente: " + patientName);
        patientEmailText.setText("Email: " + patientEmail);
        
        // Configurar RecyclerViews
        questionnairesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bmisRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    
    private void loadPatientHealthData() {
        progressBar.setVisibility(View.VISIBLE);
        
        ProfessionalApi api = ApiClient.getAuthClient().create(ProfessionalApi.class);
        api.getPatientHealthToolsTest(patientId)
            .enqueue(new Callback<PatientHealthResponse>() {
                @Override
                public void onResponse(Call<PatientHealthResponse> call, Response<PatientHealthResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        PatientHealthResponse data = response.body();
                        displayHealthData(data);
                    } else {
                        showError("Erro ao carregar dados: " + response.code());
                    }
                }
                
                @Override
                public void onFailure(Call<PatientHealthResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    showError("Erro de conexão: " + t.getMessage());
                }
            });
    }
    
    private void displayHealthData(PatientHealthResponse data) {
        // Configurar estatísticas
        String stats = "📋 Questionários: " + data.getTotalRecords().getQuestionnaires() + 
                     " | 📊 IMCs: " + data.getTotalRecords().getBmis();
        statsText.setText(stats);
        
        // Configurar adaptadores
        questionnaireAdapter = new QuestionnaireAdapter(data.getQuestionnaires());
        questionnairesRecyclerView.setAdapter(questionnaireAdapter);
        
        bmiAdapter = new BMIAdapter(data.getBmis());
        bmisRecyclerView.setAdapter(bmiAdapter);
        
        // Mostrar sucesso
        Toast.makeText(this, "Dados carregados com sucesso!", Toast.LENGTH_SHORT).show();
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
```

---

### **🎨 ETAPA 5 - CRIAR LAYOUT PROFISSIONAL**

**Criar arquivo:** `res/layout/activity_patient_health_details.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="#f5f5f5">

    <!-- Toolbar -->
    <androidx.appcompat.widget.Toolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:background="?attr/colorPrimary"
        android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
        app:titleTextColor="@color/white" />

    <!-- Conteúdo Principal -->
    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:padding="16dp">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical">

            <!-- Card Informações do Paciente -->
            <com.google.android.material.card.MaterialCardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginBottom="16dp"
                app:cardCornerRadius="12dp"
                app:cardElevation="4dp"
                app:cardBackgroundColor="@color/white">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="20dp">

                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="🏥 INFORMAÇÕES DO PACIENTE"
                        android:textSize="16sp"
                        android:textStyle="bold"
                        android:textColor="?attr/colorPrimary"
                        android:layout_marginBottom="12dp" />

                    <TextView
                        android:id="@+id/patientNameText"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="Paciente: Nome do Paciente"
                        android:textSize="18sp"
                        android:textStyle="bold"
                        android:layout_marginBottom="8dp" />

                    <TextView
                        android:id="@+id/patientEmailText"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="Email: paciente@email.com"
                        android:textSize="14sp"
                        android:textColor="#666666" />

                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>

            <!-- Card Estatísticas -->
            <com.google.android.material.card.MaterialCardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginBottom="16dp"
                app:cardCornerRadius="12dp"
                app:cardElevation="4dp"
                app:cardBackgroundColor="@color/white">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="20dp">

                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="📊 ESTATÍSTICAS GERAIS"
                        android:textSize="16sp"
                        android:textStyle="bold"
                        android:textColor="?attr/colorPrimary"
                        android:layout_marginBottom="12dp" />

                    <TextView
                        android:id="@+id/statsText"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="📋 Questionários: 0 | 📊 IMCs: 0"
                        android:textSize="16sp"
                        android:textStyle="bold"
                        android:background="#e3f2fd"
                        android:padding="12dp"
                        android:gravity="center" />

                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>

            <!-- Progress -->
            <ProgressBar
                android:id="@+id/progressBar"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="center"
                android:layout_marginBottom="16dp"
                android:visibility="gone" />

            <!-- Card Questionários -->
            <com.google.android.material.card.MaterialCardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginBottom="16dp"
                app:cardCornerRadius="12dp"
                app:cardElevation="4dp"
                app:cardBackgroundColor="@color/white">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="20dp">

                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="📋 HISTÓRICO DE QUESTIONÁRIOS"
                        android:textSize="16sp"
                        android:textStyle="bold"
                        android:textColor="?attr/colorPrimary"
                        android:layout_marginBottom="12dp" />

                    <androidx.recyclerview.widget.RecyclerView
                        android:id="@+id/questionnairesRecyclerView"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:nestedScrollingEnabled="false" />

                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>

            <!-- Card IMCs -->
            <com.google.android.material.card.MaterialCardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginBottom="16dp"
                app:cardCornerRadius="12dp"
                app:cardElevation="4dp"
                app:cardBackgroundColor="@color/white">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="20dp">

                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="📊 HISTÓRICO DE IMC"
                        android:textSize="16sp"
                        android:textStyle="bold"
                        android:textColor="?attr/colorPrimary"
                        android:layout_marginBottom="12dp" />

                    <androidx.recyclerview.widget.RecyclerView
                        android:id="@+id/bmisRecyclerView"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:nestedScrollingEnabled="false" />

                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>

        </LinearLayout>
    </ScrollView>

</LinearLayout>
```

---

### **🔌 ETAPA 6 - CRIAR API E MODELOS**

**Adicionar em ProfessionalApi.java:**
```java
@GET("professional/pacientes/{patient_id}/health-tools-test")
Call<PatientHealthResponse> getPatientHealthToolsTest(@Path("patient_id") int patientId);
```

**Criar modelo PatientHealthResponse.java:**
```java
package com.example.testbackend.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PatientHealthResponse {
    private boolean success;
    @SerializedName("patient_info")
    private PatientInfo patientInfo;
    private List<QuestionnaireData> questionnaires;
    private List<BmiData> bmis;
    @SerializedName("total_records")
    private TotalRecords totalRecords;
    
    // Getters e Setters
    public boolean isSuccess() { return success; }
    public PatientInfo getPatientInfo() { return patientInfo; }
    public List<QuestionnaireData> getQuestionnaires() { return questionnaires; }
    public List<BmiData> getBmis() { return bmis; }
    public TotalRecords getTotalRecords() { return totalRecords; }
}

class PatientInfo {
    private int id;
    private String name;
    private String email;
    
    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}

class TotalRecords {
    @SerializedName("questionnaires")
    private int questionnaires;
    private int bmis;
    
    // Getters
    public int getQuestionnaires() { return questionnaires; }
    public int getBmis() { return bmis; }
}
```

---

### **📋 ETAPA 7 - CRIAR ADAPTADORES**

**QuestionnaireAdapter.java:**
```java
package com.example.testbackend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.testbackend.R;
import com.example.testbackend.models.QuestionnaireData;
import java.util.List;

public class QuestionnaireAdapter extends RecyclerView.Adapter<QuestionnaireAdapter.ViewHolder> {
    
    private List<QuestionnaireData> questionnaires;
    
    public QuestionnaireAdapter(List<QuestionnaireData> questionnaires) {
        this.questionnaires = questionnaires;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_questionnaire, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuestionnaireData questionnaire = questionnaires.get(position);
        
        holder.scoreText.setText("Pontuação: " + questionnaire.getTotalScore() + "/" + questionnaire.getMaxScore());
        holder.riskText.setText("Risco: " + questionnaire.getRiskLevel());
        holder.dateText.setText("Data: " + formatDate(questionnaire.getCreatedAt()));
        
        // Cor baseada no risco
        int color = getRiskColor(questionnaire.getRiskLevel());
        holder.riskText.setTextColor(color);
    }
    
    private String formatDate(String dateStr) {
        try {
            return dateStr.substring(0, 10).replace("-", "/");
        } catch (Exception e) {
            return dateStr;
        }
    }
    
    private int getRiskColor(String risk) {
        switch (risk.toLowerCase()) {
            case "alto": return 0xFFFF5252; // Vermelho
            case "moderado": return 0xFFFF9800; // Laranja
            default: return 0xFF4CAF50; // Verde
        }
    }
    
    @Override
    public int getItemCount() {
        return questionnaires.size();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView scoreText, riskText, dateText;
        
        public ViewHolder(View itemView) {
            super(itemView);
            scoreText = itemView.findViewById(R.id.scoreText);
            riskText = itemView.findViewById(R.id.riskText);
            dateText = itemView.findViewById(R.id.dateText);
        }
    }
}
```

---

### **🧪 ETAPA 8 - CRIAR LAYOUTS DOS ITENS**

**item_questionnaire.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:background="#f8f9fa"
    android:padding="12dp"
    android:layout_marginBottom="8dp">

    <TextView
        android:id="@+id/scoreText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Pontuação: 15/50"
        android:textSize="16sp"
        android:textStyle="bold" />

    <TextView
        android:id="@+id/riskText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Risco: Baixo"
        android:textSize="14sp"
        android:textStyle="bold"
        android:layout_marginTop="4dp" />

    <TextView
        android:id="@+id/dateText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Data: 25/04/2026"
        android:textSize="12sp"
        android:textColor="#666666"
        android:layout_marginTop="4dp" />

</LinearLayout>
```

---

### **✅ ETAPA 9 - REGISTRAR ACTIVITY**

**Adicionar em AndroidManifest.xml:**
```xml
<activity
    android:name=".PatientHealthDetailsActivity"
    android:parentActivityName=".PatientsListActivity"
    android:theme="@style/Theme.AppCompat.Light.DarkActionBar" />
```

---

## 🧪 **ETAPA 10 - TESTE COMPLETO**

### **✅ Testar Backend:**
```bash
curl -X GET "http://localhost:8080/professional/pacientes/3/health-tools-test"
```

### **✅ Testar Frontend:**
1. **Compilar:** `./gradlew clean assembleDebug`
2. **Instalar:** `adb install app-debug.apk`
3. **Testar fluxo:**
   - Login profissional
   - "Meus Pacientes"
   - Clicar em paciente
   - Ver tela completa com dados

---

## ✅ **CRITÉRIOS DE SUCESSO:**

### **📊 Backend:**
- **Endpoint 200 OK**
- **Dados completos**

### **📱 Frontend:**
- **Clique abre nova tela**
- **Layout profissional e organizado**
- **Dados aparecem corretamente**
- **Sem crashes ou erros**

---

## 🎯 **RESUMO DA MISSÃO:**

1. **Verificar endpoint** do backend
2. **Encontrar lista de pacientes**
3. **Modificar clique** para abrir nova tela
4. **Criar Activity** profissional com cards
5. **Criar layout** bonito e organizado
6. **Criar adaptadores** para listas
7. **Testar** fluxo completo

---

## 🚀 **RESULTADO ESPERADO:**

**Profissional clica no paciente → Tela profissional com:**
- 🏥 **Informações do paciente**
- 📊 **Estatísticas visuais**
- 📋 **Histórico de questionários**
- 📈 **Histórico de IMCs**
- 🎨 **Design profissional e moderno**

**Missão: Criar uma tela completa e profissional para visualização dos dados de saúde do paciente! 🎯🏥📊**
