# 🗓️ INTEGRAÇÃO CALENDÁRIO COM BANCO DE DADOS

## 🎯 **OBJETIVO**

Fazer o calendário salvar/buscar agendamentos do banco de dados, persistindo mesmo após logout.

## 🔧 **MODIFICAÇÕES NO BACKEND**

### **1. Criar Model Appointment**

**Arquivo:** `Backend/auth-service/app/models/orm/appointment_orm.py`

```python
from sqlalchemy import String, Text, DateTime, Integer, ForeignKey
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column
from datetime import datetime

class Base(DeclarativeBase):
    pass

class AppointmentORM(Base):
    __tablename__ = "appointments"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    title: Mapped[str] = mapped_column(String(255), nullable=False)
    description: Mapped[str] = mapped_column(Text, nullable=True)
    appointment_date: Mapped[datetime] = mapped_column(DateTime, nullable=False)
    time: Mapped[str] = mapped_column(String(10), nullable=True)  # HH:MM format
    professional_id: Mapped[int] = mapped_column(Integer, ForeignKey("users.id"), nullable=False)
    patient_id: Mapped[int] = mapped_column(Integer, ForeignKey("users.id"), nullable=True)
    status: Mapped[str] = mapped_column(String(20), default="scheduled")  # scheduled, completed, cancelled
    created_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow)
    updated_at: Mapped[datetime] = mapped_column(DateTime, nullable=True, onupdate=datetime.utcnow)
```

### **2. Criar AppointmentRepository**

**Arquivo:** `Backend/auth-service/app/storage/database/appointment_repository.py`

```python
from sqlalchemy.orm import Session
from sqlalchemy import and_, or_
from datetime import datetime, timedelta
from app.models.orm.appointment_orm import AppointmentORM
from app.storage.database.base_repository import SessionLocal

class AppointmentRepository:
    
    def create(self, title: str, description: str, appointment_date: datetime, 
                 time: str, professional_id: int, patient_id: int = None, 
                 status: str = "scheduled") -> dict:
        """Cria novo agendamento"""
        with SessionLocal() as session:
            appointment = AppointmentORM(
                title=title,
                description=description,
                appointment_date=appointment_date,
                time=time,
                professional_id=professional_id,
                patient_id=patient_id,
                status=status
            )
            session.add(appointment)
            session.commit()
            session.refresh(appointment)
            
            return {
                "id": appointment.id,
                "title": appointment.title,
                "description": appointment.description,
                "appointment_date": appointment.appointment_date,
                "time": appointment.time,
                "professional_id": appointment.professional_id,
                "patient_id": appointment.patient_id,
                "status": appointment.status
            }
    
    def get_by_professional_and_month(self, professional_id: int, year: int, month: int) -> list:
        """Busca agendamentos de um profissional por mês"""
        with SessionLocal() as session:
            start_date = datetime(year, month, 1)
            if month == 12:
                end_date = datetime(year + 1, 1, 1) - timedelta(days=1)
            else:
                end_date = datetime(year, month + 1, 1)
            
            appointments = session.query(AppointmentORM).filter(
                and_(
                    AppointmentORM.professional_id == professional_id,
                    AppointmentORM.appointment_date >= start_date,
                    AppointmentORM.appointment_date < end_date,
                    AppointmentORM.status.in_(["scheduled", "completed"])
                )
            ).order_by(AppointmentORM.appointment_date, AppointmentORM.time).all()
            
            return [
                {
                    "id": apt.id,
                    "title": apt.title,
                    "description": apt.description,
                    "appointment_date": apt.appointment_date,
                    "time": apt.time,
                    "status": apt.status
                }
                for apt in appointments
            ]
    
    def get_by_date(self, professional_id: int, date: datetime) -> list:
        """Busca agendamentos de um profissional em uma data específica"""
        with SessionLocal() as session:
            start_date = date.replace(hour=0, minute=0, second=0, microsecond=0)
            end_date = date.replace(hour=23, minute=59, second=59, microsecond=999999)
            
            appointments = session.query(AppointmentORM).filter(
                and_(
                    AppointmentORM.professional_id == professional_id,
                    AppointmentORM.appointment_date >= start_date,
                    AppointmentORM.appointment_date <= end_date,
                    AppointmentORM.status.in_(["scheduled", "completed"])
                )
            ).order_by(AppointmentORM.time).all()
            
            return [
                {
                    "id": apt.id,
                    "title": apt.title,
                    "description": apt.description,
                    "appointment_date": apt.appointment_date,
                    "time": apt.time,
                    "status": apt.status
                }
                for apt in appointments
            ]
    
    def update_status(self, appointment_id: int, status: str) -> bool:
        """Atualiza status do agendamento"""
        with SessionLocal() as session:
            appointment = session.query(AppointmentORM).filter(
                AppointmentORM.id == appointment_id
            ).first()
            
            if not appointment:
                return False
            
            appointment.status = status
            appointment.updated_at = datetime.utcnow()
            session.commit()
            return True
    
    def delete(self, appointment_id: int) -> bool:
        """Exclui agendamento"""
        with SessionLocal() as session:
            appointment = session.query(AppointmentORM).filter(
                AppointmentORM.id == appointment_id
            ).first()
            
            if not appointment:
                return False
            
            session.delete(appointment)
            session.commit()
            return True
```

### **3. Criar Appointment Router**

**Arquivo:** `Backend/auth-service/app/routers/appointment_router.py`

```python
from fastapi import APIRouter, Depends, HTTPException, Header
from sqlalchemy.orm import Session
from datetime import datetime
from pydantic import BaseModel

from app.core.dependencies import get_current_user, get_session
from app.storage.database.appointment_repository import AppointmentRepository

router = APIRouter(prefix="/appointments")

# Pydantic models
class AppointmentCreate(BaseModel):
    title: str
    description: str = ""
    appointment_date: str  # ISO format
    time: str  # HH:MM format
    patient_id: int = None

class AppointmentResponse(BaseModel):
    id: int
    title: str
    description: str
    appointment_date: str
    time: str
    status: str

@router.post("/")
def create_appointment(
    appointment: AppointmentCreate,
    current_user: dict = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Cria novo agendamento"""
    
    # Verificar se é profissional
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Apenas profissionais podem criar agendamentos")
    
    try:
        appointment_date = datetime.fromisoformat(appointment.appointment_date)
    except ValueError:
        raise HTTPException(status_code=400, detail="Data inválida")
    
    repo = AppointmentRepository()
    result = repo.create(
        title=appointment.title,
        description=appointment.description,
        appointment_date=appointment_date,
        time=appointment.time,
        professional_id=current_user.get("id"),
        patient_id=appointment.patient_id
    )
    
    return {"message": "Agendamento criado com sucesso", "appointment": result}

@router.get("/month/{year}/{month}")
def get_appointments_by_month(
    year: int,
    month: int,
    current_user: dict = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Busca agendamentos por mês"""
    
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Acesso negado")
    
    repo = AppointmentRepository()
    appointments = repo.get_by_professional_and_month(
        professional_id=current_user.get("id"),
        year=year,
        month=month
    )
    
    return {"appointments": appointments}

@router.get("/day/{year}/{month}/{day}")
def get_appointments_by_date(
    year: int,
    month: int,
    day: int,
    current_user: dict = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Busca agendamentos por data específica"""
    
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Acesso negado")
    
    try:
        date = datetime(year, month, day)
    except ValueError:
        raise HTTPException(status_code=400, detail="Data inválida")
    
    repo = AppointmentRepository()
    appointments = repo.get_by_date(
        professional_id=current_user.get("id"),
        date=date
    )
    
    return {"appointments": appointments}

@router.put("/{appointment_id}/status")
def update_appointment_status(
    appointment_id: int,
    status: str,
    current_user: dict = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Atualiza status do agendamento"""
    
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Acesso negado")
    
    if status not in ["scheduled", "completed", "cancelled"]:
        raise HTTPException(status_code=400, detail="Status inválido")
    
    repo = AppointmentRepository()
    success = repo.update_status(appointment_id, status)
    
    if success:
        return {"message": "Status atualizado com sucesso"}
    else:
        raise HTTPException(status_code=404, detail="Agendamento não encontrado")

@router.delete("/{appointment_id}")
def delete_appointment(
    appointment_id: int,
    current_user: dict = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Exclui agendamento"""
    
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Acesso negado")
    
    repo = AppointmentRepository()
    success = repo.delete(appointment_id)
    
    if success:
        return {"message": "Agendamento excluído com sucesso"}
    else:
        raise HTTPException(status_code=404, detail="Agendamento não encontrado")
```

### **4. Atualizar Main.py**

**Adicionar em `Backend/auth-service/main.py`:**

```python
# Adicionar import
from app.routers.appointment_router import router as appointment_router

# Adicionar no create_app()
app.include_router(appointment_router, tags=["appointments"])
```

### **5. Criar Tabela no Banco**

**Arquivo:** `Backend/database/migrations/create_appointments_table.sql`

```sql
CREATE TABLE IF NOT EXISTS appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    appointment_date DATETIME NOT NULL,
    time VARCHAR(10),  -- HH:MM format
    professional_id INT NOT NULL,
    patient_id INT,
    status VARCHAR(20) DEFAULT 'scheduled',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (professional_id) REFERENCES users(id),
    FOREIGN KEY (patient_id) REFERENCES users(id),
    INDEX idx_professional_date (professional_id, appointment_date),
    INDEX idx_date (appointment_date)
);
```

## 📱 **INSTRUÇÕES FRONTEND**

### **Arquivo:** `pronts/31-integracao-calendario-banco-frontend.md`

```markdown
# 📱 INTEGRAÇÃO CALENDÁRIO COM API REAL

## 🎯 **OBJETIVO**

Fazer o CalendarActivity buscar/salvar agendamentos da API real, não usar dados mock.

## 🔧 **MODIFICAÇÕES NECESSÁRIAS**

### **1. Criar AppointmentApi**

**Arquivo:** `front/.../network/AppointmentApi.java`

```java
public interface AppointmentApi {
    @POST("appointments/")
    Call<Map<String, Object>> createAppointment(
        @Header("Authorization") String token,
        @Body AppointmentCreateRequest request
    );
    
    @GET("appointments/month/{year}/{month}")
    Call<AppointmentListResponse> getAppointmentsByMonth(
        @Header("Authorization") String token,
        @Path("year") int year,
        @Path("month") int month
    );
    
    @GET("appointments/day/{year}/{month}/{day}")
    Call<AppointmentListResponse> getAppointmentsByDate(
        @Header("Authorization") String token,
        @Path("year") int year,
        @Path("month") int month,
        @Path("day") int day
    );
    
    @PUT("appointments/{appointment_id}/status")
    Call<Map<String, String>> updateAppointmentStatus(
        @Header("Authorization") String token,
        @Path("appointment_id") int appointmentId,
        @Body StatusUpdateRequest request
    );
    
    @DELETE("appointments/{appointment_id}")
    Call<Map<String, String>> deleteAppointment(
        @Header("Authorization") String token,
        @Path("appointment_id") int appointmentId
    );
}
```

### **2. Model Classes**

**AppointmentCreateRequest.java:**
```java
public class AppointmentCreateRequest {
    private String title;
    private String description;
    private String appointmentDate;  // ISO format
    private String time;  // HH:MM format
    private Integer patientId;
    
    // Getters e Setters...
}
```

**StatusUpdateRequest.java:**
```java
public class StatusUpdateRequest {
    private String status;
    
    public StatusUpdateRequest(String status) {
        this.status = status;
    }
    
    // Getters e Setters...
}
```

### **3. Modificar CalendarActivity**

**Remover dados mock e usar API real:**

```java
private void loadAppointments() {
    String token = tokenManager.getAuthToken();
    if (token != null) {
        AppointmentApi api = ApiClient.getAppointmentClient().create(AppointmentApi.class);
        
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        
        api.getAppointmentsByMonth(token, year, month).enqueue(new Callback<AppointmentListResponse>() {
            @Override
            public void onResponse(Call<AppointmentListResponse> call, Response<AppointmentListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Appointment> apiAppointments = response.body().getAppointments();
                    appointments.clear();
                    appointments.addAll(apiAppointments);
                    calendarAdapter.updateAppointments(appointments);
                } else {
                    Log.e(TAG, "Erro ao carregar agendamentos: " + response.code());
                    Toast.makeText(CalendarActivity.this, "Erro ao carregar agendamentos", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<AppointmentListResponse> call, Throwable t) {
                Log.e(TAG, "Falha na conexão", t);
                Toast.makeText(CalendarActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

private void saveAppointment(Appointment appointment) {
    String token = tokenManager.getAuthToken();
    if (token != null) {
        AppointmentApi api = ApiClient.getAppointmentClient().create(AppointmentApi.class);
        
        AppointmentCreateRequest request = new AppointmentCreateRequest();
        request.setTitle(appointment.getTitle());
        request.setDescription(appointment.getDescription());
        request.setAppointmentDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(appointment.getDate()));
        request.setTime("14:30");  // Default time
        
        api.createAppointment(token, request).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CalendarActivity.this, "Agendamento salvo", Toast.LENGTH_SHORT).show();
                    // Recarregar dados do mês
                    loadAppointments();
                } else {
                    Toast.makeText(CalendarActivity.this, "Erro ao salvar agendamento", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "Falha ao salvar", t);
                Toast.makeText(CalendarActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```

### **4. Atualizar ApiClient**

**Adicionar em `ApiClient.java`:**
```java
public static Retrofit getAppointmentClient() {
    return new Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)  // ou Constants.APPOINTMENTS_BASE_URL
        .addConverterFactory(GsonConverterFactory.create())
        .client(getUnsafeOkHttpClient())
        .build();
}
```

## 🔄 **COMO TESTAR**

### **1. Criar tabela no banco:**
```bash
docker exec smartsaude-mysql mysql -u smartuser -psmartpass smartsaude < database/migrations/create_appointments_table.sql
```

### **2. Reiniciar backend:**
```bash
cd Backend
docker-compose restart auth-service
```

### **3. Testar endpoints:**
```bash
# Criar agendamento
curl -X POST http://localhost:8080/appointments/ \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Consulta Teste",
    "description": "Descrição da consulta",
    "appointment_date": "2024-01-15",
    "time": "14:30"
  }'

# Buscar agendamentos do mês
curl -X GET http://localhost:8080/appointments/month/2024/1 \
  -H "Authorization: Bearer TOKEN"
```

### **4. Testar no app:**
- Criar agendamento → Salva no banco
- Navegar meses → Mostra agendamentos corretos
- Logout/Login → Dados persistem

---

**Status:** 🔄 **AGUARDANDO IMPLEMENTAÇÃO DA INTEGRAÇÃO COM BANCO**
```

## 🎯 **RESUMO DAS MUDANÇAS**

### **Backend:**
✅ **Model AppointmentORM** - Tabela de agendamentos  
✅ **AppointmentRepository** - CRUD completo  
✅ **AppointmentRouter** - Endpoints REST  
✅ **Integração com autenticação** - Apenas profissionais  
✅ **Criação de tabela SQL** - Para executar no banco  

### **Frontend:**
✅ **AppointmentApi** - Interface Retrofit  
✅ **Models** - Request/Response  
✅ **CalendarActivity** - Integração com API real  
✅ **Remoção de dados mock** - Usa banco persistente  

---

**Status:** ✅ **INTEGRAÇÃO COMPLETA CRIADA - PRONTO PARA IMPLEMENTAR**
