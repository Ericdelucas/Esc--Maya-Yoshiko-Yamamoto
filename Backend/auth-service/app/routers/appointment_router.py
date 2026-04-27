from fastapi import APIRouter, Depends, HTTPException, Header
from sqlalchemy.orm import Session
from datetime import datetime
from pydantic import BaseModel

from app.core.dependencies import get_current_user, get_session
from app.storage.database.appointment_repository import AppointmentRepository
from app.models.schemas.user_schema import UserOut
from app.services.appointment_notification_service import AppointmentNotificationService
from app.storage.database.base_repository import SessionLocal
from app.models.orm.appointment_orm import AppointmentORM

router = APIRouter(prefix="/appointments")

# 🔥 SOLUÇÃO: Endpoint simples para pacientes
@router.get("/patient-appointments")
def get_patient_appointments_simple(
    current_user: UserOut = Depends(get_current_user)
):
    """Endpoint simples para pacientes verem seus agendamentos"""
    
    # Verificar se é paciente
    if current_user.role != "patient":
        raise HTTPException(status_code=403, detail="Acesso negado")
    
    # Buscar agendamentos do paciente
    with SessionLocal() as session:
        appointments = session.query(AppointmentORM).filter(
            AppointmentORM.patient_id == current_user.id,
            AppointmentORM.status.in_(["scheduled", "completed"])
        ).order_by(AppointmentORM.appointment_date).all()
        
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

class AppointmentListResponse(BaseModel):
    appointments: list[dict]

@router.post("/")
def create_appointment(
    appointment: AppointmentCreate,
    current_user: UserOut = Depends(get_current_user),
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
        professional_id=current_user.id,
        patient_id=appointment.patient_id
    )
    
    # 🔥 AGENDAR NOTIFICAÇÕES DA CONSULTA
    notification_service = AppointmentNotificationService()
    notification_success = notification_service.schedule_appointment_reminders(
        appointment_id=result["id"],
        professional_id=current_user.id,
        patient_id=appointment.patient_id,
        title=appointment.title,
        appointment_date=appointment_date
    )
    
    if notification_success:
        print(f"✅ Notificações agendadas para consulta {result['id']}")
    else:
        print(f"❌ Erro ao agendar notificações para consulta {result['id']}")
    
    return {"message": "Agendamento criado com sucesso", "appointment": result}

@router.get("/month/{year}/{month}")
def get_appointments_by_month(
    year: int,
    month: int,
    current_user: UserOut = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Busca agendamentos por mês"""
    
    repo = AppointmentRepository()
    
    # 🔥 CORREÇÃO: Permitir pacientes e profissionais
    if current_user.role in ["professional", "doctor", "admin"]:
        # Profissional vê seus próprios agendamentos
        appointments = repo.get_by_professional_and_month(
            professional_id=current_user.id,
            year=year,
            month=month
        )
    elif current_user.role == "patient":
        # Paciente vê agendamentos onde ele é o paciente
        appointments = repo.get_by_patient_and_month(
            patient_id=current_user.id,
            year=year,
            month=month
        )
    else:
        # Outros roles não têm acesso
        raise HTTPException(status_code=403, detail="Acesso negado")
    
    return {"appointments": appointments}

# 🔥 NOVO: Endpoint específico para pacientes
@router.get("/patient/month/{year}/{month}")
def get_patient_appointments_by_month(
    year: int,
    month: int,
    current_user: UserOut = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Busca agendamentos de um PACIENTE por mês"""
    
    # 🔥 DEBUG: Log do usuário
    print(f"DEBUG PATIENT: Usuário {current_user.id}, role: {current_user.role}")
    
    # 🔥 VERIFICAÇÃO: Permitir apenas pacientes
    if current_user.role not in ["patient"]:
        raise HTTPException(status_code=403, detail="Acesso negado")
    
    repo = AppointmentRepository()
    print(f"DEBUG PATIENT: Buscando agendamentos do paciente {current_user.id}")
    
    appointments = repo.get_by_patient_and_month(
        patient_id=current_user.id,
        year=year,
        month=month
    )
    
    print(f"DEBUG PATIENT: Encontrados {len(appointments)} agendamentos")
    return {"appointments": appointments}

@router.get("/day/{year}/{month}/{day}")
def get_appointments_by_date(
    year: int,
    month: int,
    day: int,
    current_user: UserOut = Depends(get_current_user),
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
        professional_id=current_user.id,
        date=date
    )
    
    return {"appointments": appointments}

@router.put("/{appointment_id}/status")
def update_appointment_status(
    appointment_id: int,
    status: str,
    current_user: UserOut = Depends(get_current_user),
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

@router.post("/send-daily-notifications")
def send_daily_notifications(
    current_user: UserOut = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Enviar resumo diário de consultas (admin only)"""
    
    if current_user.role != "admin":
        raise HTTPException(status_code=403, detail="Apenas administradores podem enviar notificações diárias")
    
    notification_service = AppointmentNotificationService()
    sent_count = notification_service.send_daily_appointment_summary(db)
    
    return {
        "message": f"Resumo diário enviado com sucesso",
        "notifications_sent": sent_count,
        "date": datetime.now().date().isoformat()
    }

@router.get("/notifications/pending")
def get_pending_notifications(
    current_user: UserOut = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Buscar notificações pendentes para o usuário atual"""
    
    try:
        # Buscar notificações do notification-service
        import requests
        
        response = requests.get(f"http://notification-service:8070/notifications/user/{current_user.id}", timeout=5)
        
        if response.status_code == 200:
            notifications = response.json()
            return {"notifications": notifications}
        else:
            return {"notifications": []}
            
    except Exception as e:
        print(f"❌ Erro ao buscar notificações: {e}")
        return {"notifications": []}

@router.delete("/{appointment_id}")
def delete_appointment(
    appointment_id: int,
    current_user: UserOut = Depends(get_current_user),
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
