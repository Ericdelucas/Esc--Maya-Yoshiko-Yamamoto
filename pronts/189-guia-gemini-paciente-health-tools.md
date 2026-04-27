# 🏥 **GUIA PARA GEMINI - PÁGINA DE DADOS DO PACIENTE**

## 🎯 **OBJETIVO:**

Criar uma página no app Android que mostra todos os dados das ferramentas de saúde de um paciente quando clicado na lista "Meus Pacientes".

---

## 📋 **SITUAÇÃO ATUAL:**

### **✅ Backend:** Em desenvolvimento
- **Endpoint criado:** `/professional/pacientes/{patient_id}/health-tools-test`
- **Dados disponíveis:** Questionários, IMCs, histórico completo
- **Status:** Precisa finalizar implementação

### **❌ Frontend:** Não implementado
- **Lista de pacientes:** existe mas não abre nada ao clicar
- **Página de detalhes:** precisa ser criada
- **Navegação:** precisa ser implementada

---

## 🛠️ **O QUE PRECISA SER FEITO:**

### **🔧 ETAPA 1 - Backend (Finalizar):**

**1. Verificar endpoint:**
```bash
curl -X GET "http://localhost:8080/professional/pacientes/3/health-tools-test"
```

**2. Se der 404, verificar:**
- Se o arquivo `professional_router.py` está correto
- Se os imports estão funcionando
- Se o container foi reiniciado

**3. Dados esperados do endpoint:**
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

### **📱 ETAPA 2 - Frontend (Criar do zero):**

#### **🔍 2.1 - Encontrar Activity/Fragment de pacientes:**

**Procurar por:**
- `PatientListActivity.java`
- `PatientsFragment.java`
- `MeusPacientesActivity.java`
- Arquivos que contêm "pacientes" ou "patients"

**Usar Ctrl+Shift+F no Android Studio:**
```
pacientes
patients
PatientList
RecyclerView pacientes
```

#### **🎯 2.2 - Modificar clique na lista:**

**Encontrar onde trata clique em item da lista:**
```java
// Provavelmente algo assim:
@Override
public void onItemClick(Patient patient) {
    // Código atual (provavelmente vazio ouToast)
    Toast.makeText(this, "Clicou em: " + patient.getName(), Toast.LENGTH_SHORT).show();
}
```

**Trocar para:**
```java
@Override
public void onItemClick(Patient patient) {
    Intent intent = new Intent(this, PatientHealthDetailsActivity.class);
    intent.putExtra("patient_id", patient.getId());
    intent.putExtra("patient_name", patient.getName());
    startActivity(intent);
}
```

#### **📄 2.3 - Criar nova Activity:**

**Criar arquivo:** `PatientHealthDetailsActivity.java`

```java
public class PatientHealthDetailsActivity extends AppCompatActivity {
    
    private int patientId;
    private String patientName;
    private TextView patientNameText;
    private RecyclerView questionnairesRecyclerView;
    private RecyclerView bmisRecyclerView;
    private ProgressBar progressBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_health_details);
        
        // Pegar dados da Intent
        patientId = getIntent().getIntExtra("patient_id", 0);
        patientName = getIntent().getStringExtra("patient_name");
        
        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Dados de Saúde: " + patientName);
            getSupportActionBar().setHomeAsUpEnabled(true);
        }
        
        // Inicializar views
        initViews();
        
        // Carregar dados
        loadPatientHealthData();
    }
    
    private void initViews() {
        patientNameText = findViewById(R.id.patientNameText);
        questionnairesRecyclerView = findViewById(R.id.questionnairesRecyclerView);
        bmisRecyclerView = findViewById(R.id.bmisRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        
        patientNameText.setText("Paciente: " + patientName);
        
        // Configurar RecyclerViews
        questionnairesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bmisRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    
    private void loadPatientHealthData() {
        progressBar.setVisibility(View.VISIBLE);
        
        // Usar Retrofit para buscar dados
        ApiClient.getAuthClient().create(ProfessionalApi.class)
            .getPatientHealthToolsTest(patientId)
            .enqueue(new Callback<PatientHealthResponse>() {
                @Override
                public void onResponse(Call<PatientHealthResponse> call, Response<PatientHealthResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        PatientHealthResponse data = response.body();
                        displayHealthData(data);
                    } else {
                        Toast.makeText(PatientHealthDetailsActivity.this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show();
                    }
                }
                
                @Override
                public void onFailure(Call<PatientHealthResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PatientHealthDetailsActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
                }
            });
    }
    
    private void displayHealthData(PatientHealthResponse data) {
        // Configurar adaptadores
        QuestionnaireAdapter questionnaireAdapter = new QuestionnaireAdapter(data.getQuestionnaires());
        questionnairesRecyclerView.setAdapter(questionnaireAdapter);
        
        BMIAdapter bmiAdapter = new BMIAdapter(data.getBmis());
        bmisRecyclerView.setAdapter(bmiAdapter);
        
        // Mostrar estatísticas
        Toast.makeText(this, 
            "Questionários: " + data.getTotalRecords().getQuestionnaires() + 
            " | IMCs: " + data.getTotalRecords().getBmis(), 
            Toast.LENGTH_LONG).show();
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

#### **🎨 2.4 - Criar layout XML:**

**Criar arquivo:** `res/layout/activity_patient_health_details.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <androidx.appcompat.widget.Toolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:background="?attr/colorPrimary"
        android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar" />

    <TextView
        android:id="@+id/patientNameText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Paciente: Nome"
        android:textSize="18sp"
        android:textStyle="bold"
        android:layout_marginBottom="16dp" />

    <ProgressBar
        android:id="@+id/progressBar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:visibility="gone" />

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="📋 Questionários Recentes"
        android:textSize="16sp"
        android:textStyle="bold"
        android:layout_marginTop="16dp"
        android:layout_marginBottom="8dp" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/questionnairesRecyclerView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:background="@android:color/white"
        android:padding="8dp" />

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="📊 Histórico de IMC"
        android:textSize="16sp"
        android:textStyle="bold"
        android:layout_marginTop="16dp"
        android:layout_marginBottom="8dp" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/bmisRecyclerView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:background="@android:color/white"
        android:padding="8dp" />

</LinearLayout>
```

#### **🔌 2.5 - Criar API接口:**

**Adicionar em ProfessionalApi.java:**
```java
@GET("professional/pacientes/{patient_id}/health-tools-test")
Call<PatientHealthResponse> getPatientHealthToolsTest(@Path("patient_id") int patientId);
```

#### **📝 2.6 - Criar classes de modelo:**

```java
// PatientHealthResponse.java
public class PatientHealthResponse {
    private boolean success;
    private PatientInfo patient_info;
    private List<QuestionnaireData> questionnaires;
    private List<BmiData> bmis;
    private TotalRecords total_records;
    
    // Getters e Setters
}

// QuestionnaireData.java
public class QuestionnaireData {
    private int id;
    private int total_score;
    private int max_score;
    private String risk_level;
    private Map<String, String> answers;
    private String created_at;
    
    // Getters e Setters
}

// BmiData.java
public class BmiData {
    private int id;
    private double bmi;
    private double height;
    private double weight;
    private String category;
    private String created_at;
    
    // Getters e Setters
}
```

#### **📋 2.7 - Criar adaptadores:**

```java
// QuestionnaireAdapter.java
public class QuestionnaireAdapter extends RecyclerView.Adapter<QuestionnaireAdapter.ViewHolder> {
    
    private List<QuestionnaireData> questionnaires;
    
    public QuestionnaireAdapter(List<QuestionnaireData> questionnaires) {
        this.questionnaires = questionnaires;
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_questionnaire, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        QuestionnaireData questionnaire = questionnaires.get(position);
        
        holder.scoreText.setText("Pontuação: " + questionnaire.getTotal_score() + "/" + questionnaire.getMax_score());
        holder.riskText.setText("Risco: " + questionnaire.getRisk_level());
        holder.dateText.setText("Data: " + questionnaire.getCreated_at());
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

## 🧪 **ETAPA 3 - Teste:**

### **✅ Backend:**
```bash
curl -X GET "http://localhost:8080/professional/pacientes/3/health-tools-test"
```

### **✅ Frontend:**
1. **Compilar:** `./gradlew clean assembleDebug`
2. **Instalar:** `adb install app-debug.apk`
3. **Testar:**
   - Abrir app
   - Fazer login como profissional
   - Ir em "Meus Pacientes"
   - Clicar em um paciente
   - Verificar se abre página com dados

---

## ✅ **CRITÉRIOS DE SUCESSO:**

### **📊 Backend:**
- **Endpoint retorna 200 OK**
- **Dados completos do paciente**
- **Questionários e IMCs formatados**

### **📱 Frontend:**
- **Clique no paciente abre nova página**
- **Nome do paciente aparece**
- **Lista de questionários aparece**
- **Histórico de IMC aparece**
- **Sem crashes ou erros**

---

## 🚨 **PONTOS DE ATENÇÃO:**

### **⚠️ Importante:**
- **Verificar** se o paciente existe antes de buscar dados
- **Tratar** casos de paciente sem dados
- **Mostrar** loading enquanto carrega
- **Tratar** erros de rede

### **🔍 Debug:**
- **Logs** para verificar se endpoint está sendo chamado
- **Logs** para verificar se dados estão chegando
- **Toast** para mostrar erros

---

## 🎯 **RESUMO DA TAREFA:**

1. **Finalizar endpoint** no backend
2. **Encontrar lista de pacientes** no app
3. **Modificar clique** para abrir nova activity
4. **Criar Activity** de detalhes do paciente
5. **Criar layout** com RecyclerViews
6. **Criar adaptadores** para questionários e IMCs
7. **Testar** fluxo completo

---

## 🚀 **MISSÃO:**

**Criar uma página completa que mostra todos os dados de saúde do paciente quando clicado na lista!**

**O profissional vai poder ver todo o histórico de questionários e IMCs do paciente de forma organizada! 🎯🏥📊**
