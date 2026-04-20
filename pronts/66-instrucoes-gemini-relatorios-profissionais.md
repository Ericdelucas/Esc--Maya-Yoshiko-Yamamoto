# # **INSTRUÇÕES PARA GEMINI - IMPLEMENTAR RELATÓRIOS PROFISSIONAIS**

## # **OBJETIVO PRINCIPAL**
Criar interface profissional para relatórios de pacientes acessível através do ProfessionalMainActivity, com abas organizadas e design moderno.

## # **CONTEXTUALIZAÇÃO**

### # **Sistema Atual:**
- # **Backend:** 100% pronto com API de relatórios funcionando
- # **ProfessionalMainActivity:** Já existe com cardReports
- # **PatientsListActivity:** Já existe para lista de pacientes
- # **Necessidade:** Implementar frontend completo para relatórios

### # **Ponto de Partida:**
- # **CardReports** no ProfessionalMainActivity já aponta para PatientEvaluationActivity
- # **Precisa ser alterado** para apontar para nova PatientReportsActivity
- # **Backend API** está em `http://127.0.0.1:8080/reports/`

## # **BACKEND API - REFERÊNCIA**

### # **Endpoints Disponíveis:**
```bash
# # GET /reports/professional/{id} - Relatórios do profissional
# # POST /reports/ - Criar novo relatório
# # GET /reports/{id} - Detalhes do relatório
# # PUT /reports/{id} - Atualizar relatório
# # DELETE /reports/{id} - Excluir relatório
# # GET /reports/patient/{id} - Relatórios de um paciente
# # GET /reports/statistics - Estatísticas do profissional
```

### # **Estrutura de Dados:**
```json
{
  "id": 1,
  "patient_id": 1,
  "professional_id": 37,
  "report_date": "2026-04-13T15:00:00",
  "report_type": "EVOLUTION|ASSESSMENT|DISCHARGE|PROGRESS",
  "title": "Título do relatório",
  "content": "Conteúdo principal",
  "clinical_evolution": "Evolução clínica",
  "objective_data": "Dados objetivos",
  "subjective_data": "Dados subjetivos", 
  "treatment_plan": "Plano de tratamento",
  "recommendations": "Recomendações",
  "next_steps": "Próximos passos",
  "pain_scale": 4,
  "functional_status": "Bom|Regular|Ruim",
  "achievements": ["Conquista 1", "Conquista 2"],
  "limitations": ["Limitação 1", "Limitação 2"],
  "created_at": "2026-04-14T01:03:06",
  "created_by": "professional"
}
```

## # **IMPLEMENTAÇÃO OBRIGATÓRIA**

### # **1. Models Java (CRIAR):**
```java
// # PatientReport.java
public class PatientReport implements Serializable {
    private Integer id;
    private Integer patientId;
    private Integer professionalId;
    private Date reportDate;
    private String reportType;
    private String title;
    private String content;
    private String clinicalEvolution;
    private String objectiveData;
    private String subjectiveData;
    private String treatmentPlan;
    private String recommendations;
    private String nextSteps;
    private Integer painScale;
    private String functionalStatus;
    private List<String> achievements;
    private List<String> limitations;
    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
    
    // Getters e Setters completos com @SerializedName
}

// # ReportCreate.java (para POST)
public class ReportCreate {
    // Mesmos campos de PatientReport sem id, created_at, updated_at
}

// # ReportUpdate.java (para PUT)
public class ReportUpdate {
    // Campos opcionais para atualização
}

// # ReportList.java
public class ReportList {
    private List<PatientReport> reports;
    private Integer total;
    private Integer page;
    private Integer perPage;
}

// # ReportStatistics.java
public class ReportStatistics {
    private Map<String, Object> reportTypes;
    private Integer totalReports;
    private List<PatientReport> recentReports;
}
```

### # **2. API Interface (CRIAR):**
```java
// # PatientReportApi.java
public interface PatientReportApi {
    
    @GET("reports/professional/{professionalId}")
    Call<List<PatientReport>> getProfessionalReports(@Path("professionalId") int professionalId);
    
    @POST("reports/")
    Call<PatientReport> createReport(@Body ReportCreate report);
    
    @GET("reports/{reportId}")
    Call<PatientReport> getReport(@Path("reportId") int reportId);
    
    @PUT("reports/{reportId}")
    Call<PatientReport> updateReport(@Path("reportId") int reportId, @Body ReportUpdate report);
    
    @DELETE("reports/{reportId}")
    Call<Void> deleteReport(@Path("reportId") int reportId);
    
    @GET("reports/patient/{patientId}")
    Call<List<PatientReport>> getPatientReports(@Path("patientId") int patientId);
    
    @GET("reports/statistics")
    Call<ReportStatistics> getStatistics(@Query("professional_id") int professionalId);
    
    @GET("reports/search/patient")
    Call<List<PatientReport>> searchReports(@Query("name") String name);
}
```

### # **3. Activity Principal com Abas (CRIAR):**
```java
// # PatientReportsActivity.java
public class PatientReportsActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ReportsPagerAdapter adapter;
    private TokenManager tokenManager;
    private int professionalId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_reports);
        
        // Obter ID do profissional
        professionalId = getCurrentProfessionalId();
        
        // Setup ViewPager e TabLayout
        setupViewPager();
        
        // Toolbar com título "Relatórios de Pacientes"
        setupToolbar();
    }
    
    private void setupViewPager() {
        adapter = new ReportsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ReportListFragment(), "Relatórios");
        adapter.addFragment(new ReportStatisticsFragment(), "Estatísticas");
        adapter.addFragment(new ReportSearchFragment(), "Buscar");
        adapter.addFragment(new CreateReportFragment(), "Novo");
        
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
    
    private int getCurrentProfessionalId() {
        // Obter ID do profissional do TokenManager ou hardcoded (37)
        return 37; // Temporário
    }
}
```

### # **4. Fragment - Lista de Relatórios (CRIAR):**
```java
// # ReportListFragment.java
public class ReportListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ReportAdapter adapter;
    private List<PatientReport> reports = new ArrayList<>();
    private ProgressBar progressBar;
    private PatientReportApi api;
    private TokenManager tokenManager;
    private int professionalId;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_list, container, false);
        
        setupViews(view);
        loadReports();
        
        return view;
    }
    
    private void setupViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewReports);
        progressBar = view.findViewById(R.id.progressBar);
        
        adapter = new ReportAdapter(reports, this::onReportClick, this::onReportLongClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        
        // Swipe to refresh
        SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this::loadReports);
    }
    
    private void loadReports() {
        progressBar.setVisibility(View.VISIBLE);
        
        api.getProfessionalReports(professionalId).enqueue(new Callback<List<PatientReport>>() {
            @Override
            public void onResponse(Call<List<PatientReport>> call, Response<List<PatientReport>> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    reports.clear();
                    reports.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Erro ao carregar relatórios", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<List<PatientReport>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void onReportClick(PatientReport report) {
        // Abrir detalhes do relatório
        Intent intent = new Intent(getContext(), ReportDetailActivity.class);
        intent.putExtra("report", report);
        startActivity(intent);
    }
    
    private void onReportLongClick(PatientReport report) {
        // Menu com opções: Editar, Excluir, Compartilhar
        showOptionsMenu(report);
    }
}
```

### # **5. Fragment - Estatísticas (CRIAR):**
```java
// # ReportStatisticsFragment.java
public class ReportStatisticsFragment extends Fragment {
    private TextView tvTotalReports;
    private TextView tvEvolutionReports;
    private TextView tvAssessmentReports;
    private TextView tvAvgPainScale;
    private RecyclerView recyclerViewRecent;
    private RecentReportsAdapter recentAdapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_statistics, container, false);
        
        setupViews(view);
        loadStatistics();
        
        return view;
    }
    
    private void loadStatistics() {
        PatientReportApi api = ApiClient.getAuthClient().create(PatientReportApi.class);
        
        api.getStatistics(professionalId).enqueue(new Callback<ReportStatistics>() {
            @Override
            public void onResponse(Call<ReportStatistics> call, Response<ReportStatistics> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReportStatistics stats = response.body();
                    
                    tvTotalReports.setText(String.valueOf(stats.getTotalReports()));
                    
                    // Atualizar cards por tipo
                    Map<String, Object> reportTypes = stats.getReportTypes();
                    if (reportTypes.containsKey("EVOLUTION")) {
                        Map<String, Object> evolution = (Map<String, Object>) reportTypes.get("EVOLUTION");
                        tvEvolutionReports.setText(String.valueOf(evolution.get("count")));
                    }
                    
                    // Média de dor
                    double avgPain = calculateAveragePain(reportTypes);
                    tvAvgPainScale.setText(String.format("%.1f", avgPain));
                    
                    // Relatórios recentes
                    recentAdapter.updateReports(stats.getRecentReports());
                }
            }
            
            @Override
            public void onFailure(Call<ReportStatistics> call, Throwable t) {
                Toast.makeText(getContext(), "Erro ao carregar estatísticas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```

### # **6. Activity - Criar Relatório (CRIAR):**
```java
// # CreateReportActivity.java
public class CreateReportActivity extends AppCompatActivity {
    private Spinner spinnerReportType;
    private EditText editTitle;
    private EditText editContent;
    private EditText editClinicalEvolution;
    private EditText editObjectiveData;
    private EditText editTreatmentPlan;
    private SeekBar seekBarPainScale;
    private TextView tvPainScaleValue;
    private Spinner spinnerFunctionalStatus;
    private Button btnSave;
    private Button btnCancel;
    
    private PatientReportApi api;
    private int professionalId;
    private int patientId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);
        
        // Obter dados da Intent
        professionalId = getIntent().getIntExtra("professional_id", 37);
        patientId = getIntent().getIntExtra("patient_id", -1);
        
        setupViews();
        setupSpinners();
        setupPainScale();
    }
    
    private void setupViews() {
        spinnerReportType = findViewById(R.id.spinnerReportType);
        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        editClinicalEvolution = findViewById(R.id.editClinicalEvolution);
        editObjectiveData = findViewById(R.id.editObjectiveData);
        editTreatmentPlan = findViewById(R.id.editTreatmentPlan);
        seekBarPainScale = findViewById(R.id.seekBarPainScale);
        tvPainScaleValue = findViewById(R.id.tvPainScaleValue);
        spinnerFunctionalStatus = findViewById(R.id.spinnerFunctionalStatus);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        
        btnSave.setOnClickListener(v -> saveReport());
        btnCancel.setOnClickListener(v -> finish());
    }
    
    private void saveReport() {
        // Validação de campos
        if (!validateFields()) {
            return;
        }
        
        ReportCreate report = new ReportCreate();
        report.setPatientId(patientId);
        report.setProfessionalId(professionalId);
        report.setReportDate(new Date());
        report.setReportType((String) spinnerReportType.getSelectedItem());
        report.setTitle(editTitle.getText().toString());
        report.setContent(editContent.getText().toString());
        report.setClinicalEvolution(editClinicalEvolution.getText().toString());
        report.setObjectiveData(editObjectiveData.getText().toString());
        report.setTreatmentPlan(editTreatmentPlan.getText().toString());
        report.setPainScale(seekBarPainScale.getProgress());
        report.setFunctionalStatus((String) spinnerFunctionalStatus.getSelectedItem());
        
        api.createReport(report).enqueue(new Callback<PatientReport>() {
            @Override
            public void onResponse(Call<PatientReport> call, Response<PatientReport> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateReportActivity.this, "Relatório criado com sucesso", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CreateReportActivity.this, "Erro ao criar relatório", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<PatientReport> call, Throwable t) {
                Toast.makeText(CreateReportActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private boolean validateFields() {
        if (editTitle.getText().toString().trim().isEmpty()) {
            editTitle.setError("Título obrigatório");
            return false;
        }
        if (patientId == -1) {
            Toast.makeText(this, "Selecione um paciente", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
```

### # **7. Adapters (CRIAR):**
```java
// # ReportAdapter.java
public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
    private List<PatientReport> reports;
    private OnReportClickListener clickListener;
    private OnReportLongClickListener longClickListener;
    
    public interface OnReportClickListener {
        void onClick(PatientReport report);
    }
    
    public interface OnReportLongClickListener {
        void onLongClick(PatientReport report);
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_report, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PatientReport report = reports.get(position);
        
        holder.tvTitle.setText(report.getTitle());
        holder.tvDate.setText(formatDate(report.getReportDate()));
        holder.tvType.setText(report.getReportType());
        holder.tvPatient.setText("Paciente ID: " + report.getPatientId());
        
        // Escala de dor
        if (report.getPainScale() != null) {
            holder.tvPainScale.setText("Dor: " + report.getPainScale() + "/10");
        }
        
        holder.itemView.setOnClickListener(v -> clickListener.onClick(report));
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onLongClick(report);
            return true;
        });
    }
    
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }
}

// # ReportsPagerAdapter.java
public class ReportsPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    
    public void addFragment(Fragment fragment, String title) {
        fragments.add(fragment);
        titles.add(title);
    }
    
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
    
    @Override
    public int getCount() {
        return fragments.size();
    }
    
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
```

## # **LAYOUTS XML (CRIAR)**

### # **1. Activity Principal:**
```xml
<!-- # activity_patient_reports.xml -->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
        
        <androidx.appcompat.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="?attr/colorPrimary"
            android:title="Relatórios de Pacientes"
            android:titleTextColor="@color/white"/>
            
        <com.google.android.material.tabs.TabLayout
            android:id="@+id/tabLayout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="?attr/colorPrimary"
            app:tabMode="fixed"
            app:tabGravity="fill"
            app:tabTextColor="@color/white"
            app:tabSelectedTextColor="@color/white"/>
            
    </com.google.android.material.appbar.AppBarLayout>

    <androidx.viewpager.widget.ViewPager
        android:id="@+id/viewPager"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"/>

</LinearLayout>
```

### # **2. Fragment Lista:**
```xml
<!-- # fragment_report_list.xml -->
<androidx.swiperefreshlayout.widget.SwipeRefreshLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/swipeRefresh"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/recyclerViewReports"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:padding="8dp"/>

        <ProgressBar
            android:id="@+id/progressBar"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"/>

    </FrameLayout>

</androidx.swiperefreshlayout.widget.SwipeRefreshLayout>
```

### # **3. Item da Lista:**
```xml
<!-- # item_report.xml -->
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="8dp"
    app:cardCornerRadius="8dp"
    app:cardElevation="4dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal">

            <TextView
                android:id="@+id/tvTitle"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="Título do Relatório"
                android:textSize="16sp"
                android:textStyle="bold"
                android:textColor="@color/black"/>

            <TextView
                android:id="@+id/tvType"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:background="@drawable/rounded_background"
                android:padding="4dp"
                android:text="EVOLUTION"
                android:textColor="@color/white"
                android:textSize="12sp"/>

        </LinearLayout>

        <TextView
            android:id="@+id/tvDate"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="4dp"
            android:text="13/04/2026 15:00"
            android:textSize="14sp"
            android:textColor="@color/gray"/>

        <TextView
            android:id="@+id/tvPatient"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="2dp"
            android:text="Paciente: Maria Silva"
            android:textSize="14sp"
            android:textColor="@color/gray"/>

        <TextView
            android:id="@+id/tvPainScale"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="2dp"
            android:text="Dor: 4/10"
            android:textSize="14sp"
            android:textColor="@color/red"/>

    </LinearLayout>

</com.google.android.material.card.MaterialCardView>
```

### # **4. Activity Criar Relatório:**
```xml
<!-- # activity_create_report.xml -->
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <!-- Tipo de Relatório -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Tipo de Relatório"
            android:textStyle="bold"
            android:layout_marginBottom="4dp"/>

        <Spinner
            android:id="@+id/spinnerReportType"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp"/>

        <!-- Título -->
        <com.google.android.material.textfield.TextInputLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/editTitle"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="Título do Relatório"
                android:inputType="textCapWords"/>

        </com.google.android.material.textfield.TextInputLayout>

        <!-- Conteúdo Principal -->
        <com.google.android.material.textfield.TextInputLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/editContent"
                android:layout_width="match_parent"
                android:layout_height="120dp"
                android:hint="Conteúdo Principal"
                android:gravity="top"
                android:inputType="textMultiLine"/>

        </com.google.android.material.textfield.TextInputLayout>

        <!-- Evolução Clínica -->
        <com.google.android.material.textfield.TextInputLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/editClinicalEvolution"
                android:layout_width="match_parent"
                android:layout_height="100dp"
                android:hint="Evolução Clínica"
                android:gravity="top"
                android:inputType="textMultiLine"/>

        </com.google.android.material.textfield.TextInputLayout>

        <!-- Dados Objetivos -->
        <com.google.android.material.textfield.TextInputLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/editObjectiveData"
                android:layout_width="match_parent"
                android:layout_height="100dp"
                android:hint="Dados Objetivos"
                android:gravity="top"
                android:inputType="textMultiLine"/>

        </com.google.android.material.textfield.TextInputLayout>

        <!-- Plano de Tratamento -->
        <com.google.android.material.textfield.TextInputLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/editTreatmentPlan"
                android:layout_width="match_parent"
                android:layout_height="100dp"
                android:hint="Plano de Tratamento"
                android:gravity="top"
                android:inputType="textMultiLine"/>

        </com.google.android.material.textfield.TextInputLayout>

        <!-- Escala de Dor -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Escala de Dor"
            android:textStyle="bold"
            android:layout_marginBottom="4dp"/>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:layout_marginBottom="16dp">

            <SeekBar
                android:id="@+id/seekBarPainScale"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:max="10"/>

            <TextView
                android:id="@+id/tvPainScaleValue"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="0/10"
                android:textSize="16sp"
                android:textStyle="bold"
                android:layout_marginStart="16dp"/>

        </LinearLayout>

        <!-- Status Funcional -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Status Funcional"
            android:textStyle="bold"
            android:layout_marginBottom="4dp"/>

        <Spinner
            android:id="@+id/spinnerFunctionalStatus"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="32dp"/>

        <!-- Botões -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal">

            <Button
                android:id="@+id/btnCancel"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_marginEnd="8dp"
                android:text="Cancelar"
                style="@style/Widget.AppCompat.Button.ButtonBarAlertDialog.Negative"/>

            <Button
                android:id="@+id/btnSave"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_marginStart="8dp"
                android:text="Salvar Relatório"
                style="@style/Widget.AppCompat.Button.ButtonBarAlertDialog.Positive"/>

        </LinearLayout>

    </LinearLayout>

</ScrollView>
```

## # **INTEGRAÇÕES NECESSÁRIAS**

### # **1. ProfessionalMainActivity (ALTERAR):**
```java
// # Encontrar o método setupClickListeners() e alterar:
if (cardReports != null) {
    cardReports.setOnClickListener(v -> {
        Log.d(TAG, "Abrindo Relatórios de Pacientes...");
        startActivity(new Intent(this, PatientReportsActivity.class));
    });
}
```

### # **2. PatientsListActivity (ALTERAR):**
```java
// # Adicionar botão de relatórios em cada item da lista
// # No adapter de pacientes, adicionar:
ImageButton btnReports = view.findViewById(R.id.btnReports);
btnReports.setOnClickListener(v -> {
    Intent intent = new Intent(context, PatientReportsActivity.class);
    intent.putExtra("patient_id", patient.getId());
    intent.putExtra("patient_name", patient.getFullName());
    context.startActivity(intent);
});
```

### # **3. AndroidManifest.xml (ADICIONAR):**
```xml
<activity
    android:name=".PatientReportsActivity"
    android:theme="@style/AppTheme.NoActionBar"
    android:parentActivityName=".ProfessionalMainActivity"/>

<activity
    android:name=".CreateReportActivity"
    android:theme="@style/AppTheme.NoActionBar"
    android:parentActivityName=".PatientReportsActivity"/>
```

## # **CORES E ESTILOS (ADICIONAR):**
```xml
<!-- # colors.xml -->
<color name="primary_blue">#1976D2</color>
<color name="primary_blue_dark">#1565C0</color>
<color name="accent_green">#4CAF50</color>
<color name="text_primary">#212121</color>
<color name="text_secondary">#757575</color>
<color name="background_light">#F5F5F5</color>
<color name="gray">#9E9E9E</color>
<color name="red">#F44336</color>

<!-- # styles.xml -->
<style name="AppTheme.NoActionBar" parent="Theme.AppCompat.Light.NoActionBar">
    <item name="colorPrimary">@color/primary_blue</item>
    <item name="colorPrimaryDark">@color/primary_blue_dark</item>
    <item name="colorAccent">@color/accent_green</item>
</style>

<!-- # drawable/rounded_background.xml -->
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="@color/primary_blue"/>
    <corners android:radius="12dp"/>
</shape>
```

## # **VALIDAÇÃO E TESTES**

### # **1. Testes de API:**
- # **GET /reports/professional/37** deve retornar lista
- # **POST /reports/** deve criar novo relatório
- # **PUT /reports/{id}** deve atualizar
- # **DELETE /reports/{id}** deve excluir

### # **2. Testes de UI:**
- # **Abas** devem alternar corretamente
- # **RecyclerView** deve carregar dados
- # **Formulário** deve validar campos obrigatórios
- # **Navigation** deve funcionar entre telas

### # **3. Testes de Integração:**
- # **ProfessionalMainActivity** -> PatientReportsActivity
- # **PatientsListActivity** -> PatientReportsActivity
- # **Token** deve ser incluído nos headers da API

## # **PRIORIDADES DE IMPLEMENTAÇÃO**

### # **1. MÁXIMA PRIORIDADE:**
1. # **Models Java** (PatientReport, ReportCreate, etc.)
2. # **API Interface** (PatientReportApi)
3. # **PatientReportsActivity** com TabLayout
4. # **ReportListFragment** com RecyclerView
5. # **ReportAdapter** básico

### # **2. PRIORIDADE MÉDIA:**
1. # **CreateReportActivity** com formulário
2. # **ReportStatisticsFragment** básico
3. # **Integração** com ProfessionalMainActivity
4. # **Layouts** profissionais

### # **3. PRIORIDADE BAIXA:**
1. # **ReportSearchFragment**
2. # **ReportDetailActivity**
3. # **Menu de opções** (long click)
4. # **Animações** e transições

---

## # **RESUMO FINAL**

**O que precisa ser feito:**
1. # **Criar Models Java** completos
2. # **Criar API Interface** com todos endpoints  
3. # **Implementar Activity principal** com abas
4. # **Implementar Fragment da lista** com RecyclerView
5. # **Implementar Activity de criação** com formulário
6. # **Integrar com sistema atual**
7. # **Testar tudo**

**Backend está 100% pronto!** Agora é só implementar o frontend profissional para relatórios de pacientes.
