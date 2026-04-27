# 🎯 **GUIA GEMINI - IMPLEMENTAR FERRAMENTAS DE SAÚDE**

## 🚨 **BACKEND JÁ ESTÁ PRONTO!**

### **✅ O que já foi implementado:**
- **Tabelas criadas:** `health_tools` e `health_questionnaires`
- **API endpoints:** 8 endpoints completos
- **Cálculo IMC:** automático com categorização
- **Cálculo Gordura Corporal:** baseado em idade e gênero
- **Questionário Saúde:** com pontuação e nível de risco
- **Histórico completo:** todos os registros salvos

---

## 🎯 **ENDPOINTS DISPONÍVEIS**

### **📋 IMC - Índice de Massa Corporal:**
```
POST /health-tools/calculate-bmi
{
  "height": 1.75,
  "weight": 70
}

GET /health-tools/bmi-history?limit=10
```

### **📋 Gordura Corporal:**
```
POST /health-tools/calculate-body-fat
{
  "height": 1.75,
  "weight": 70,
  "age": 25,
  "gender": "M"
}

GET /health-tools/body-fat-history?limit=10
```

### **📋 Questionário de Saúde:**
```
GET /health-tools/questionnaire-template  (obter perguntas)

POST /health-tools/save-questionnaire
{
  "answers": [
    {"question_id": "smoking", "answer": "no"},
    {"question_id": "alcohol", "answer": "weekly"}
  ]
}

GET /health-tools/questionnaire-history?limit=10
```

### **📋 Resumo e Histórico:**
```
GET /health-tools/summary  (resumo completo)
GET /health-tools/history?limit=50  (histórico completo)
```

---

## 🎯 **O QUE GEMINI PRECISA FAZER NO FRONTEND**

### **📋 Passo 1 - Criar Activity Ferramentas de Saúde**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/HealthToolsActivity.java`

```java
public class HealthToolsActivity extends AppCompatActivity {
    
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private HealthToolsPagerAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_tools);
        
        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Ferramentas de Saúde");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // Configurar Tabs
        setupTabs();
    }
    
    private void setupTabs() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        
        adapter = new HealthToolsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        
        new TabLayoutMediator(tabLayout, viewPager).attach();
        
        TabLayout.Tab tabIMC = tabLayout.newTab().setText("IMC");
        TabLayout.Tab tabGordura = tabLayout.newTab().setText("Gordura");
        TabLayout.Tab tabQuestionario = tabLayout.newTab().setText("Questionário");
        TabLayout.Tab tabHistorico = tabLayout.newTab().setText("Histórico");
        
        tabLayout.addTab(tabIMC);
        tabLayout.addTab(tabGordura);
        tabLayout.addTab(tabQuestionario);
        tabLayout.addTab(tabHistorico);
    }
}
```

### **📋 Passo 2 - Layout da Activity**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/layout/activity_health_tools.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <com.google.android.material.appbar.MaterialToolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize" />

    </com.google.android.material.appbar.AppBarLayout>

    <com.google.android.material.tabs.TabLayout
        android:id="@+id/tabLayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="?attr/colorPrimary"
        android:tabTextColor="@android:color/white"
        android:tabSelectedTextColor="@android:color/white" />

    <androidx.viewpager.widget.ViewPager
        android:id="@+id/viewPager"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />

</LinearLayout>
```

### **📋 Passo 3 - Fragment de IMC**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/fragments/BMICalculatorFragment.java`

```java
public class BMICalculatorFragment extends Fragment {
    
    private EditText editHeight, editWeight;
    private Button btnCalculate;
    private TextView txtResult, txtCategory;
    private ProgressBar progressBar;
    private BMIApi api;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bmi_calculator, container, false);
        
        editHeight = view.findViewById(R.id.editHeight);
        editWeight = view.findViewById(R.id.editWeight);
        btnCalculate = view.findViewById(R.id.btnCalculate);
        txtResult = view.findViewById(R.id.txtResult);
        txtCategory = view.findViewById(R.id.txtCategory);
        progressBar = view.findViewById(R.id.progressBar);
        
        api = ApiClient.getAuthClient().create(BMIApi.class);
        
        setupClickListeners();
        loadHistory();
        
        return view;
    }
    
    private void setupClickListeners() {
        btnCalculate.setOnClickListener(v -> calculateBMI());
    }
    
    private void calculateBMI() {
        String heightStr = editHeight.getText().toString().trim();
        String weightStr = editWeight.getText().toString().trim();
        
        if (heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(getContext(), "Preencha altura e peso", Toast.LENGTH_SHORT).show();
            return;
        }
        
        float height = Float.parseFloat(heightStr);
        float weight = Float.parseFloat(weightStr);
        
        progressBar.setVisibility(View.VISIBLE);
        
        BMICalculationRequest request = new BMICalculationRequest(height, weight);
        
        api.calculateBMI(request).enqueue(new Callback<BMIResponse>() {
            @Override
            public void onResponse(Call<BMIResponse> call, Response<BMIResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    BMIResponse bmiResponse = response.body();
                    if (bmiResponse.success) {
                        txtResult.setText("IMC: " + bmiResponse.data.bmi);
                        txtCategory.setText("Categoria: " + bmiResponse.data.category);
                    } else {
                        Toast.makeText(getContext(), "Erro ao calcular IMC", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Erro na resposta", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<BMIResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```

### **📋 Passo 4 - Layout do IMC**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/layout/fragment_bmi_calculator.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <com.google.android.material.card.MaterialCardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="16dp">

                <TextView
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="Calculadora de IMC"
                    android:textSize="18sp"
                    android:textStyle="bold"
                    android:gravity="center"
                    android:layout_marginBottom="16dp" />

                <com.google.android.material.textfield.TextInputLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginBottom="16dp"
                    android:hint="Altura (metros)">

                    <com.google.android.material.textfield.TextInputEditText
                        android:id="@+id/editHeight"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:inputType="numberDecimal" />

                </com.google.android.material.textfield.TextInputLayout>

                <com.google.android.material.textfield.TextInputLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginBottom="16dp"
                    android:hint="Peso (kg)">

                    <com.google.android.material.textfield.TextInputEditText
                        android:id="@+id/editWeight"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:inputType="numberDecimal" />

                </com.google.android.material.textfield.TextInputLayout>

                <Button
                    android:id="@+id/btnCalculate"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="Calcular IMC"
                    android:layout_marginBottom="16dp" />

                <ProgressBar
                    android:id="@+id/progressBar"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_gravity="center"
                    android:visibility="gone" />

            </LinearLayout>

        </com.google.android.material.card.MaterialCardView>

        <TextView
            android:id="@+id/txtResult"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Resultado aparecerá aqui"
            android:textSize="16sp"
            android:gravity="center"
            android:layout_marginBottom="8dp" />

        <TextView
            android:id="@+id/txtCategory"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Categoria aparecerá aqui"
            android:textSize="16sp"
            android:gravity="center"
            android:textStyle="bold" />

    </LinearLayout>

</ScrollView>
```

### **📋 Passo 5 - API Classes**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/network/HealthToolsApi.java`

```java
package com.example.testbackend.network;

import retrofit2.Call;
import retrofit2.http.*;

public interface HealthToolsApi {
    
    @POST("health-tools/calculate-bmi")
    Call<BMIResponse> calculateBMI(@Body BMICalculationRequest request);
    
    @POST("health-tools/calculate-body-fat")
    Call<BodyFatResponse> calculateBodyFat(@Body BodyFatCalculationRequest request);
    
    @POST("health-tools/save-questionnaire")
    Call<QuestionnaireResponse> saveQuestionnaire(@Body QuestionnaireRequest request);
    
    @GET("health-tools/summary")
    Call<HealthSummaryResponse> getHealthSummary();
    
    @GET("health-tools/bmi-history")
    Call<HistoryResponse> getBMIHistory(@Query("limit") int limit);
    
    @GET("health-tools/body-fat-history")
    Call<HistoryResponse> getBodyFatHistory(@Query("limit") int limit);
    
    @GET("health-tools/questionnaire-template")
    Call<QuestionnaireTemplateResponse> getQuestionnaireTemplate();
    
    @GET("health-tools/questionnaire-history")
    Call<QuestionnaireHistoryResponse> getQuestionnaireHistory(@Query("limit") int limit);
    
    // Response classes
    class BMIResponse {
        public boolean success;
        public String message;
        public BMIData data;
    }
    
    class BMIData {
        public float bmi;
        public String category;
        public String created_at;
    }
    
    class BodyFatResponse {
        public boolean success;
        public String message;
        public BodyFatData data;
    }
    
    class BodyFatData {
        public float body_fat_percentage;
        public String category;
        public String created_at;
    }
}
```

---

## 🎮 **COMO TESTAR**

### **✅ Testar Backend:**
```bash
# Testar template do questionário
curl -X GET "http://localhost:8080/health-tools/questionnaire-template"

# Testar cálculo de IMC
curl -X POST "http://localhost:8080/health-tools/calculate-bmi" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -d '{"height": 1.75, "weight": 70}'
```

### **✅ Testar Frontend:**
1. **Implementar HealthToolsActivity**
2. **Implementar BMICalculatorFragment**
3. **Implementar outros fragments**
4. **Testar cálculo de IMC**
5. **Testar histórico**

---

## 🚨 **IMPORTANTE**

### **🎯 Features completas:**
- ✅ **IMC:** cálculo automático + categorização
- ✅ **Gordura Corporal:** baseado em idade/gênero
- ✅ **Questionário Saúde:** pontuação + nível de risco
- ✅ **Histórico:** todos os registros salvos
- ✅ **Resumo:** visão geral completa

### **🎯 O que falta:**
- ❌ **Frontend Android:** implementar activities e fragments
- ❌ **Navegação:** adicionar no menu principal
- ❌ **UI/UX:** telas amigáveis e responsivas

**Backend está 100% pronto! Só falta implementar o frontend Android! 🎯**
