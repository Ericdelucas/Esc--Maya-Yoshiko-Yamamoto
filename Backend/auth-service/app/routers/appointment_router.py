from fastapi import APIRouter, Depends, HTTPException, Header
from sqlalchemy.orm import Session
from datetime import datetime
from pydantic import BaseModel

from app.core.dependencies import get_current_user, get_session
from app.storage.database.appointment_repository import AppointmentRepository
from app.models.schemas.user_schema import UserOut

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
    
    return {"message": "Agendamento criado com sucesso", "appointment": result}

@router.get("/month/{year}/{month}")
def get_appointments_by_month(
    year: int,
    month: int,
    current_user: UserOut = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Busca agendamentos por mês"""
    
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Acesso negado")
    
    repo = AppointmentRepository()
    appointments = repo.get_by_professional_and_month(
        professional_id=current_user.id,
        year=year,
        month=month
    )
    
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
