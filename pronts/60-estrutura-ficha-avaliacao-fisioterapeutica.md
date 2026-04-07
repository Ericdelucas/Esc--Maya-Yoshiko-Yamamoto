# 📋 ESTRUTURA COMPLETA - FICHA DE AVALIAÇÃO FISIOTERAPÊUTICA

## 🎯 **OBJETIVO**
Criar um sistema completo para fichas de avaliação fisioterapêutica com 4 blocos principais de dados.

## 📊 **ESTRUTURA DE DADOS**

### **BLOCO 1: IDENTIFICAÇÃO DO PACIENTE**
```java
public class PatientIdentification {
    private String fullName;           // Nome completo
    private String address;            // Endereço completo
    private String phone;              // Telefone (com DDD)
    private String email;              // E-mail
    private String cpf;                // CPF
    private Date birthDate;            // Data de nascimento
    private String gender;             // Gênero
    private String maritalStatus;        // Estado civil
}
```

### **BLOCO 2: DADOS ADMINISTRATIVOS DO ATENDIMENTO**
```java
public class AdministrativeData {
    private Date firstContactDate;      // Data do primeiro contato
    private String profession;           // Profissão
    private String healthPlan;          // Convênio / plano de saúde
    private String patientOrigin;        // Como chegou ao consultório
    private Double sessionFee;          // Valor cobrado por sessão
    private List<String> medications;   // Lista de medicamentos em uso
    private String appointmentTime;      // Horário de atendimento
    private String frequency;            // Frequência das sessões
}
```

### **BLOCO 3: QUEIXA PRINCIPAL**
```java
public class ChiefComplaint {
    private String mainReason;           // Motivo principal da consulta
    private String complaintDescription;  // Descrição detalhada
    private String painExample;         // Exemplo: dor, desconforto, limitação
    private String patientObjective;    // Objetivo do paciente
    private Integer painScale;          // Escala de dor (0-10)
    private Date symptomStartDate;      // Início dos sintomas
}
```

### **BLOCO 4: HISTÓRICO DE DOR**
```java
public class PainHistory {
    private String painLocation;        // Local da dor
    private String duration;           // Tempo de duração
    private String frequencyPattern;    // Frequência ou padrão temporal
    private Date symptomStartDate;     // Datas associadas ao início
    private List<String> triggers;     // Gatilhos da dor
    private List<String> relievers;    // O que alivia a dor
    private String painType;           // Tipo de dor (aguda, crônica)
    private String evolutionPattern;    // Padrão de evolução
}
```

### **BLOCO 5: HISTÓRICO CLÍNICO RELEVANTE**
```java
public class ClinicalHistory {
    private List<String> accidents;      // Acidentes
    private List<String> surgeries;      // Cirurgias
    private List<String> traumas;        // Traumas
    private List<String> immobilizations; // Imobilizações
    private List<String> hospitalizations; // Internações
    private List<String> previousDiseases; // Doenças prévias
    private List<String> allergies;       // Alergias
    private String familyHistory;         // Histórico familiar
}
```

### **BLOCO 6: EXAMES COMPLEMENTARES**
```java
public class ComplementaryExams {
    private List<ImagingExam> imagingExams;    // Exames de imagem
    private List<LaboratoryExam> labExams;      // Exames laboratoriais
    private List<String> previousDiagnoses;     // Diagnósticos prévios
    private String relevantResults;              // Resultados relevantes
}

public class ImagingExam {
    private String examType;        // RM, RX, TC, USG
    private Date examDate;         // Data do exame
    private String result;          // Resultado
    private String conclusions;     // Conclusões
}
```

### **BLOCO 7: AVALIAÇÃO FÍSICA / POSTURAL**
```java
public class PhysicalAssessment {
    private PosturalAssessment posturalAssessment; // Avaliação postural
    private List<String> tensionAreas;          // Regiões de tensão
    private List<String> asymmetries;           // Assimetrias
    private List<String> adhesionsScars;       // Cicatrizes aderidas
    private List<String> referredPainAreas;     // Áreas de dor referida
    private String postureProfile;               // Perfil postural geral
}

public class PosturalAssessment {
    private String anteriorView;      // Marcações vista anterior
    private String posteriorView;      // Marcações vista posterior
    private String lateralView;        // Marcações perfil
    private String observations;        // Observações gerais
}
```

### **BLOCO 8: RELAÇÃO CAUSA → CONSEQUÊNCIA**
```java
public class CauseConsequenceAnalysis {
    private String probableOrigin;      // Origem provável do problema
    private List<String> connections;  // Conexões entre trauma e sintoma
    private String biomechanicalPattern; // Padrões biomecânicos compensatórios
    private String temporalEvolution;  // Evolução em linha do tempo
    private List<String> contributingFactors; // Fatores contribuintes
}
```

### **BLOCO 9: PLANO DE TRATAMENTO**
```java
public class TreatmentPlan {
    private String therapeuticStrategy;  // Estratégia terapêutica
    private List<String> posturalCorrection; // Correções posturais
    private List<String> muscleChains;     // Foco nas cadeias musculares
    private String individualizedApproach;   // Abordagem individualizada
    private Integer estimatedSessions;       // Sessões estimadas
    private List<String> treatmentGoals;     // Metas do tratamento
    private String objectiveSummary;         // Resumo objetivo
}
```

## 🗄️ **ESTRUTURA DO BANCO DE DADOS**

### **Tabela Principal - patient_evaluations**
```sql
CREATE TABLE patient_evaluations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    professional_id INT NOT NULL,
    evaluation_date DATETIME NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(255),
    cpf VARCHAR(14),
    birth_date DATE,
    gender VARCHAR(10),
    marital_status VARCHAR(50),
    
    -- Dados administrativos
    first_contact_date DATETIME,
    profession VARCHAR(100),
    health_plan VARCHAR(100),
    patient_origin VARCHAR(100),
    session_fee DECIMAL(10,2),
    medications JSON,
    appointment_time VARCHAR(10),
    frequency VARCHAR(50),
    
    -- Queixa principal
    main_reason TEXT,
    complaint_description TEXT,
    pain_example VARCHAR(255),
    patient_objective TEXT,
    pain_scale INT,
    symptom_start_date DATE,
    
    -- Histórico de dor
    pain_location TEXT,
    duration VARCHAR(100),
    frequency_pattern TEXT,
    triggers JSON,
    relievers JSON,
    pain_type VARCHAR(50),
    evolution_pattern TEXT,
    
    -- Histórico clínico
    accidents JSON,
    surgeries JSON,
    traumas JSON,
    immobilizations JSON,
    hospitalizations JSON,
    previous_diseases JSON,
    allergies JSON,
    family_history TEXT,
    
    -- Exames complementares
    imaging_exams JSON,
    lab_exams JSON,
    previous_diagnoses JSON,
    relevant_results TEXT,
    
    -- Avaliação física
    postural_assessment JSON,
    tension_areas JSON,
    asymmetries JSON,
    adhesions_scars JSON,
    referred_pain_areas JSON,
    posture_profile VARCHAR(100),
    
    -- Análise causa-consequência
    probable_origin TEXT,
    connections JSON,
    biomechanical_pattern TEXT,
    temporal_evolution TEXT,
    contributing_factors JSON,
    
    -- Plano de tratamento
    therapeutic_strategy TEXT,
    postural_correction JSON,
    muscle_chains JSON,
    individualized_approach TEXT,
    estimated_sessions INT,
    treatment_goals JSON,
    objective_summary TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (professional_id) REFERENCES users(id)
);
```

## 📱 **ESTRUTURA DAS ACTIVITIES**

### **1. PatientEvaluationActivity**
```java
public class PatientEvaluationActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private EvaluationPagerAdapter adapter;
    private PatientEvaluation currentEvaluation;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_evaluation);
        
        setupViewPager();
        setupFab();
    }
    
    private void setupViewPager() {
        viewPager = findViewById(R.id.viewPager);
        adapter = new EvaluationPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }
    
    private void setupFab() {
        FloatingActionButton fabSave = findViewById(R.id.fabSave);
        fabSave.setOnClickListener(v -> saveEvaluation());
    }
}
```

### **2. Fragments para cada bloco**
```java
// Fragment 1: IdentificationFragment
// Fragment 2: AdministrativeFragment  
// Fragment 3: ChiefComplaintFragment
// Fragment 4: PainHistoryFragment
// Fragment 5: ClinicalHistoryFragment
// Fragment 6: ExamsFragment
// Fragment 7: PhysicalAssessmentFragment
// Fragment 8: CauseConsequenceFragment
// Fragment 9: TreatmentPlanFragment
```

## 🔧 **API ENDPOINTS**

### **Endpoints para fichas de avaliação**
```java
public interface EvaluationApi {
    
    @GET("evaluations/patient/{patientId}")
    Call<List<PatientEvaluation>> getEvaluationsByPatient(@Path("patientId") int patientId);
    
    @GET("evaluations/{evaluationId}")
    Call<PatientEvaluation> getEvaluationById(@Path("evaluationId") int evaluationId);
    
    @POST("evaluations")
    Call<PatientEvaluation> createEvaluation(@Body PatientEvaluation evaluation);
    
    @PUT("evaluations/{evaluationId}")
    Call<PatientEvaluation> updateEvaluation(@Path("evaluationId") int evaluationId, @Body PatientEvaluation evaluation);
    
    @DELETE("evaluations/{evaluationId}")
    Call<Void> deleteEvaluation(@Path("evaluationId") int evaluationId);
    
    @GET("evaluations/professional/{professionalId}")
    Call<List<PatientEvaluation>> getEvaluationsByProfessional(@Path("professionalId") int professionalId);
}
```

## 📋 **FORMULÁRIO DETALHADO**

### **Campos obrigatórios vs opcionais:**
```java
public class ValidationRules {
    // OBRIGATÓRIOS
    private static final String[] REQUIRED_FIELDS = {
        "fullName", "phone", "email", "cpf",
        "mainReason", "complaintDescription"
    };
    
    // OPCIONAIS
    private static final String[] OPTIONAL_FIELDS = {
        "address", "profession", "healthPlan",
        "medications", "previousDiagnoses"
    };
}
```

## 🎯 **PRÓXIMOS PASSOS**

### **1. Criar models Java**
### **2. Implementar fragments**
### **3. Criar API endpoints**
### **4. Desenvolver UI**
### **5. Implementar validações**
### **6. Adicionar exportação (PDF)**
### **7. Integrar com sistema de agendamentos**

---

**Status:** 📋 **ESTRUTURA COMPLETA DEFINIDA - PRONTO PARA IMPLEMENTAÇÃO**
