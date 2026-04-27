from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from app.core.dependencies import get_current_user, get_session
from app.models.schemas.user_schema import UserOut
from app.storage.database.base_repository import SessionLocal
from app.models.orm.appointment_orm import AppointmentORM

router = APIRouter(prefix="/patient")

@router.get("/my-appointments")
def get_my_appointments(
    current_user: UserOut = Depends(get_current_user)
):
    """Paciente vê seus próprios agendamentos"""
    
    # Verificar se é paciente
    if current_user.role != "patient":
        raise HTTPException(status_code=403, detail="Acesso negado")
    
    # Buscar todos os agendamentos do paciente
    with SessionLocal() as session:
        appointments = session.query(AppointmentORM).filter(
            AppointmentORM.patient_id == current_user.id,
            AppointmentORM.status.in_(["scheduled", "completed"])
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
