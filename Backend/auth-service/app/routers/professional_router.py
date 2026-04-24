from fastapi import APIRouter, Depends, HTTPException, Header
from sqlalchemy.orm import Session
from app.core.dependencies import get_current_user, get_session
from app.models.schemas.dashboard_stats import DashboardStatsOut
from app.models.schemas.user_schema import UserOut, PatientOut
from app.models.orm.user_orm import UserORM
from typing import List

router = APIRouter(prefix="/professional")

@router.get("/dashboard-stats")
def get_dashboard_stats(
    current_user: dict = Depends(get_current_user),
    db: Session = Depends(get_session)
) -> DashboardStatsOut:
    """Retorna estatísticas do dashboard para profissionais"""
    
    # Verificar se é profissional
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Acesso negado")
    
    # Contar pacientes
    total_patients = db.query(UserORM).filter(
        UserORM.role == "patient"
    ).count()
    
    # Para agora, retornar valores simulados para outras estatísticas
    # (podem ser implementadas depois)
    return DashboardStatsOut(
        total_patients=total_patients,
        appointments_today=0,  # Implementar depois
        active_exercises=0,  # Implementar depois
        recent_activities=[]  # Implementar depois
    )


@router.get("/pacientes")
def get_patients(
    current_user: dict = Depends(get_current_user),
    db: Session = Depends(get_session)
) -> List[PatientOut]:
    """Lista todos os pacientes (apenas para profissionais)"""
    
    # Verificar se é profissional
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Acesso negado")
    
    # Buscar todos os pacientes
    patients = db.query(UserORM).filter(
        UserORM.role == "patient"
    ).all()
    
    # Converter para PatientOut manualmente
    patient_list = []
    for patient in patients:
        # Se full_name for null, usar email como fallback
        display_name = patient.full_name if patient.full_name else patient.email.split("@")[0]
        
        patient_data = PatientOut(
            id=patient.id,
            full_name=display_name,
            email=patient.email,
            cpf=None,  # Campo não existe na tabela
            phone=None,  # Campo não existe na tabela
            birth_date=None,  # Campo não existe na tabela
            role=patient.role
        )
        patient_list.append(patient_data)
    
    return patient_list
