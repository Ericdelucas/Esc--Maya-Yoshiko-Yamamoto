from fastapi import APIRouter, Depends, HTTPException, Header
from sqlalchemy.orm import Session
from app.core.dependencies import get_current_user, get_session
from app.models.schemas.dashboard_stats import DashboardStatsOut
from app.models.orm.user_orm import UserORM

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
