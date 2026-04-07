# 🗄️ SOLUÇÃO COMPLETA - PERSISTÊNCIA DE FICHAS DE AVALIAÇÃO

## 🎯 **PROBLEMA IDENTIFICADO**

### **O que acontece:**
- ✅ Frontend cria ficha de avaliação
- ✅ Dados são preenchidos corretamente
- ❌ **Não salva no banco de dados**
- ❌ **Ao sair e voltar, dados somem**
- ❌ **Cada paciente novo cria "quadradinho" separado**

### **O que precisa acontecer:**
- ✅ Salvar ficha no banco MySQL
- ✅ Recuperar fichas salvas
- ✅ Persistir dados após logout/login
- ✅ Listar todos os pacientes com fichas

## 🛠️ **SOLUÇÃO COMPLETA**

### **1. Criar ORM Model para Avaliação**
```java
// app/models/orm/PatientEvaluationORM.java
package com.example.testbackend.models.orm;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_evaluations")
public class PatientEvaluationORM {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "patient_id")
    private Integer patientId;
    
    @Column(name = "professional_id")
    private Integer professionalId;
    
    @Column(name = "evaluation_date")
    private LocalDateTime evaluationDate;
    
    // Dados de identificação
    @Column(name = "full_name")
    private String fullName;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "cpf")
    private String cpf;
    
    @Column(name = "birth_date")
    private LocalDateTime birthDate;
    
    @Column(name = "gender")
    private String gender;
    
    // Dados administrativos
    @Column(name = "first_contact_date")
    private LocalDateTime firstContactDate;
    
    @Column(name = "profession")
    private String profession;
    
    @Column(name = "health_plan")
    private String healthPlan;
    
    @Column(name = "patient_origin")
    private String patientOrigin;
    
    @Column(name = "session_fee")
    private Double sessionFee;
    
    @Column(name = "medications", columnDefinition = "JSON")
    private String medications;
    
    @Column(name = "appointment_time")
    private String appointmentTime;
    
    // Queixa principal
    @Column(name = "main_reason")
    private String mainReason;
    
    @Column(name = "complaint_description")
    private String complaintDescription;
    
    @Column(name = "pain_scale")
    private Integer painScale;
    
    // Histórico de dor
    @Column(name = "pain_location")
    private String painLocation;
    
    @Column(name = "duration")
    private String duration;
    
    @Column(name = "frequency_pattern")
    private String frequencyPattern;
    
    // Histórico clínico
    @Column(name = "clinical_history", columnDefinition = "JSON")
    private String clinicalHistory;
    
    // Exames
    @Column(name = "exams", columnDefinition = "JSON")
    private String exams;
    
    // Avaliação física
    @Column(name = "postural_assessment", columnDefinition = "JSON")
    private String posturalAssessment;
    
    // Plano de tratamento
    @Column(name = "treatment_plan", columnDefinition = "JSON")
    private String treatmentPlan;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Getters e Setters...
}
```

### **2. Criar Repository**
```java
// app/storage/database/PatientEvaluationRepository.java
package com.example.testbackend.storage.database;

import com.example.testbackend.models.orm.PatientEvaluationORM;
import java.util.List;
import java.util.Optional;

public class PatientEvaluationRepository {
    
    public PatientEvaluationORM save(PatientEvaluationORM evaluation) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                if (evaluation.getId() == null) {
                    evaluation.setCreatedAt(LocalDateTime.now());
                    session.persist(evaluation);
                } else {
                    evaluation.setUpdatedAt(LocalDateTime.now());
                    session.merge(evaluation);
                }
                transaction.commit();
                return evaluation;
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }
    }
    
    public Optional<PatientEvaluationORM> findById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(PatientEvaluationORM.class, id));
        }
    }
    
    public List<PatientEvaluationORM> findByPatientId(Integer patientId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM PatientEvaluationORM WHERE patientId = :patientId ORDER BY evaluationDate DESC",
                PatientEvaluationORM.class)
                .setParameter("patientId", patientId)
                .list();
        }
    }
    
    public List<PatientEvaluationORM> findByProfessionalId(Integer professionalId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "FROM PatientEvaluationORM WHERE professionalId = :professionalId ORDER BY evaluationDate DESC",
                PatientEvaluationORM.class)
                .setParameter("professionalId", professionalId)
                .list();
        }
    }
    
    public List<PatientEvaluationORM> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM PatientEvaluationORM ORDER BY evaluationDate DESC", 
                PatientEvaluationORM.class).list();
        }
    }
    
    public void deleteById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                PatientEvaluationORM evaluation = session.get(PatientEvaluationORM.class, id);
                if (evaluation != null) {
                    session.remove(evaluation);
                }
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }
    }
}
```

### **3. Criar Router/Controller**
```java
// app/routers/patient_evaluation_router.py
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List

from app.storage.database.patient_evaluation_repository import PatientEvaluationRepository
from app.storage.database.db import get_session
from app.models.schemas.patient_evaluation_schema import (
    PatientEvaluationCreate, PatientEvaluationResponse, PatientEvaluationUpdate
)

router = APIRouter(prefix="/evaluations", tags=["patient_evaluations"])

@router.post("/", response_model=PatientEvaluationResponse)
def create_evaluation(
    evaluation: PatientEvaluationCreate,
    session: Session = Depends(get_session)
):
    repository = PatientEvaluationRepository()
    try:
        new_evaluation = repository.save(evaluation)
        return new_evaluation
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.get("/patient/{patient_id}", response_model=List[PatientEvaluationResponse])
def get_evaluations_by_patient(
    patient_id: int,
    session: Session = Depends(get_session)
):
    repository = PatientEvaluationRepository()
    evaluations = repository.findByPatientId(patient_id)
    return evaluations

@router.get("/professional/{professional_id}", response_model=List[PatientEvaluationResponse])
def get_evaluations_by_professional(
    professional_id: int,
    session: Session = Depends(get_session)
):
    repository = PatientEvaluationRepository()
    evaluations = repository.findByProfessionalId(professional_id)
    return evaluations

@router.get("/{evaluation_id}", response_model=PatientEvaluationResponse)
def get_evaluation(
    evaluation_id: int,
    session: Session = Depends(get_session)
):
    repository = PatientEvaluationRepository()
    evaluation = repository.findById(evaluation_id)
    if not evaluation:
        raise HTTPException(status_code=404, detail="Evaluation not found")
    return evaluation

@router.put("/{evaluation_id}", response_model=PatientEvaluationResponse)
def update_evaluation(
    evaluation_id: int,
    evaluation_update: PatientEvaluationUpdate,
    session: Session = Depends(get_session)
):
    repository = PatientEvaluationRepository()
    existing_evaluation = repository.findById(evaluation_id)
    if not existing_evaluation:
        raise HTTPException(status_code=404, detail="Evaluation not found")
    
    # Atualizar campos
    for field, value in evaluation_update.dict(exclude_unset=True).items():
        setattr(existing_evaluation, field, value)
    
    updated_evaluation = repository.save(existing_evaluation)
    return updated_evaluation

@router.delete("/{evaluation_id}")
def delete_evaluation(
    evaluation_id: int,
    session: Session = Depends(get_session)
):
    repository = PatientEvaluationRepository()
    evaluation = repository.findById(evaluation_id)
    if not evaluation:
        raise HTTPException(status_code=404, detail="Evaluation not found")
    
    repository.deleteById(evaluation_id)
    return {"message": "Evaluation deleted successfully"}
```

### **4. Criar Pydantic Schemas**
```python
# app/models/schemas/patient_evaluation_schema.py
from pydantic import BaseModel
from datetime import datetime
from typing import Optional, List

class PatientEvaluationBase(BaseModel):
    patient_id: int
    professional_id: int
    full_name: str
    address: Optional[str] = None
    phone: str
    email: str
    cpf: str
    birth_date: Optional[datetime] = None
    gender: Optional[str] = None
    first_contact_date: Optional[datetime] = None
    profession: Optional[str] = None
    health_plan: Optional[str] = None
    patient_origin: Optional[str] = None
    session_fee: Optional[float] = None
    medications: Optional[str] = None
    appointment_time: Optional[str] = None
    main_reason: str
    complaint_description: str
    pain_scale: Optional[int] = None
    pain_location: Optional[str] = None
    duration: Optional[str] = None
    frequency_pattern: Optional[str] = None
    clinical_history: Optional[str] = None
    exams: Optional[str] = None
    postural_assessment: Optional[str] = None
    treatment_plan: Optional[str] = None

class PatientEvaluationCreate(PatientEvaluationBase):
    pass

class PatientEvaluationUpdate(BaseModel):
    full_name: Optional[str] = None
    address: Optional[str] = None
    phone: Optional[str] = None
    email: Optional[str] = None
    main_reason: Optional[str] = None
    complaint_description: Optional[str] = None
    pain_scale: Optional[int] = None
    pain_location: Optional[str] = None
    duration: Optional[str] = None
    frequency_pattern: Optional[str] = None
    clinical_history: Optional[str] = None
    exams: Optional[str] = None
    postural_assessment: Optional[str] = None
    treatment_plan: Optional[str] = None

class PatientEvaluationResponse(PatientEvaluationBase):
    id: int
    created_at: datetime
    updated_at: Optional[datetime] = None
    
    class Config:
        from_attributes = True
```

### **5. Criar Tabela SQL**
```sql
-- migrations/create_patient_evaluations_table.sql
CREATE TABLE IF NOT EXISTS patient_evaluations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    professional_id INT NOT NULL,
    evaluation_date DATETIME NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    address TEXT,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    birth_date DATETIME,
    gender VARCHAR(10),
    first_contact_date DATETIME,
    profession VARCHAR(100),
    health_plan VARCHAR(100),
    patient_origin VARCHAR(100),
    session_fee DECIMAL(10,2),
    medications JSON,
    appointment_time VARCHAR(10),
    main_reason TEXT NOT NULL,
    complaint_description TEXT NOT NULL,
    pain_scale INT,
    pain_location TEXT,
    duration VARCHAR(100),
    frequency_pattern TEXT,
    clinical_history JSON,
    exams JSON,
    postural_assessment JSON,
    treatment_plan JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_patient_id (patient_id),
    INDEX idx_professional_id (professional_id),
    INDEX idx_evaluation_date (evaluation_date)
);
```

### **6. Frontend - Models**
```java
// app/models/PatientEvaluation.java
package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class PatientEvaluation {
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("patient_id")
    private Integer patientId;
    
    @SerializedName("professional_id")
    private Integer professionalId;
    
    @SerializedName("full_name")
    private String fullName;
    
    @SerializedName("address")
    private String address;
    
    @SerializedName("phone")
    private String phone;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("cpf")
    private String cpf;
    
    @SerializedName("main_reason")
    private String mainReason;
    
    @SerializedName("complaint_description")
    private String complaintDescription;
    
    @SerializedName("pain_scale")
    private Integer painScale;
    
    @SerializedName("pain_location")
    private String painLocation;
    
    @SerializedName("duration")
    private String duration;
    
    @SerializedName("clinical_history")
    private String clinicalHistory;
    
    @SerializedName("exams")
    private String exams;
    
    @SerializedName("postural_assessment")
    private String posturalAssessment;
    
    @SerializedName("treatment_plan")
    private String treatmentPlan;
    
    @SerializedName("created_at")
    private Date createdAt;
    
    // Getters e Setters...
}
```

### **7. Frontend - API Interface**
```java
// app/network/PatientEvaluationApi.java
package com.example.testbackend.network;

import com.example.testbackend.models.PatientEvaluation;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface PatientEvaluationApi {
    
    @GET("evaluations/patient/{patientId}")
    Call<List<PatientEvaluation>> getEvaluationsByPatient(@Path("patientId") int patientId);
    
    @GET("evaluations/professional/{professionalId}")
    Call<List<PatientEvaluation>> getEvaluationsByProfessional(@Path("professionalId") int professionalId);
    
    @GET("evaluations/{evaluationId}")
    Call<PatientEvaluation> getEvaluationById(@Path("evaluationId") int evaluationId);
    
    @POST("evaluations")
    Call<PatientEvaluation> createEvaluation(@Body PatientEvaluation evaluation);
    
    @PUT("evaluations/{evaluationId}")
    Call<PatientEvaluation> updateEvaluation(@Path("evaluationId") int evaluationId, @Body PatientEvaluation evaluation);
    
    @DELETE("evaluations/{evaluationId}")
    Call<Void> deleteEvaluation(@Path("evaluationId") int evaluationId);
}
```

### **8. Frontend - Activity Principal**
```java
// app/PatientListActivity.java
package com.example.testbackend;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testbackend.models.PatientEvaluation;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.PatientEvaluationApi;
import com.example.testbackend.utils.TokenManager;

import java.util.List;

public class PatientListActivity extends AppCompatActivity {
    
    private static final String TAG = "PATIENT_LIST_DEBUG";
    private ListView listViewPatients;
    private ArrayAdapter<PatientEvaluation> adapter;
    private PatientEvaluationApi api;
    private TokenManager tokenManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);
        
        listViewPatients = findViewById(R.id.listViewPatients);
        tokenManager = new TokenManager(this);
        api = ApiClient.getAuthClient().create(PatientEvaluationApi.class);
        
        loadEvaluations();
        setupListeners();
    }
    
    private void loadEvaluations() {
        String token = tokenManager.getAuthToken();
        if (token == null) {
            Toast.makeText(this, "Faça login primeiro", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Log.d(TAG, "🔍 Carregando avaliações do profissional...");
        
        // Obter ID do profissional do token (implementar)
        int professionalId = getCurrentProfessionalId();
        
        api.getEvaluationsByProfessional(professionalId).enqueue(new Callback<List<PatientEvaluation>>() {
            @Override
            public void onResponse(Call<List<PatientEvaluation>> call, retrofit2.Response<List<PatientEvaluation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PatientEvaluation> evaluations = response.body();
                    Log.d(TAG, "✅ " + evaluations.size() + " avaliações carregadas do banco");
                    
                    adapter = new ArrayAdapter<>(PatientListActivity.this, 
                        android.R.layout.simple_list_item_1, evaluations);
                    listViewPatients.setAdapter(adapter);
                    
                } else {
                    Log.e(TAG, "❌ Erro ao carregar avaliações: " + response.code());
                    Toast.makeText(PatientListActivity.this, "Erro ao carregar avaliações", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<List<PatientEvaluation>> call, Throwable t) {
                Log.e(TAG, "❌ Falha na conexão", t);
                Toast.makeText(PatientListActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupListeners() {
        listViewPatients.setOnItemClickListener((parent, view, position, id) -> {
            PatientEvaluation evaluation = (PatientEvaluation) adapter.getItem(position);
            // Abrir activity de detalhes/edição
            openEvaluationActivity(evaluation);
        });
    }
    
    private void openEvaluationActivity(PatientEvaluation evaluation) {
        // Implementar navegação para activity de avaliação
        Intent intent = new Intent(this, PatientEvaluationActivity.class);
        intent.putExtra("evaluation_id", evaluation.getId());
        startActivity(intent);
    }
    
    private int getCurrentProfessionalId() {
        // Implementar extração do ID do JWT token
        // Por enquanto, retornar hardcoded para teste
        return 37; // ID do profissional@novo.com
    }
}
```

### **9. Registrar Router no Main**
```python
# main.py
from app.routers.patient_evaluation_router import router as patient_evaluation_router

app.include_router(patient_evaluation_router)
```

## 🎯 **FUNCIONALIDADE COMPLETA**

### **O que será implementado:**
1. ✅ **Salvar ficha no banco** MySQL
2. ✅ **Listar todos os pacientes** com fichas
3. ✅ **Editar ficha existente**
4. ✅ **Persistir dados** após logout/login
5. ✅ **Criar "quadradinho"** para cada paciente
6. ✅ **Integrar com sistema de login**

### **Fluxo completo:**
```
Login → Lista de Pacientes → Criar/Editar Ficha → Salvar no Banco → Listar novamente
```

---

**Status:** 🗄️ **SOLUÇÃO COMPLETA DE PERSISTÊNCIA DEFINIDA**
