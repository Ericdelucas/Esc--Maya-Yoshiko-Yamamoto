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


@router.get("/list")
def get_professionals(
    current_user: dict = Depends(get_current_user),
    db: Session = Depends(get_session)
) -> List[dict]:
    """Lista todos os profissionais (para IA ter contexto)"""
    
    # Buscar todos os profissionais
    professionals = db.query(UserORM).filter(
        UserORM.role.in_(["professional", "doctor", "admin"])
    ).all()
    
    # Converter para formato simples
    professional_list = []
    for professional in professionals:
        # Se full_name for null, usar email como fallback
        display_name = professional.full_name if professional.full_name else professional.email.split("@")[0]
        
        professional_data = {
            "id": professional.id,
            "name": display_name,
            "email": professional.email,
            "role": professional.role,
            "specialty": get_specialty_by_role(professional.role)
        }
        professional_list.append(professional_data)
    
    return professional_list


def get_specialty_by_role(role: str) -> str:
    """Retorna especialidade baseada no papel do usuário"""
    specialties = {
        "professional": "Fisioterapeuta",
        "doctor": "Médico",
        "admin": "Administrador"
    }
    return specialties.get(role, "Profissional de Saúde")


@router.get("/exercises/manage")
def get_all_patients_exercises(
    current_user: dict = Depends(get_current_user)
):
    """Profissional visualiza todos os exercícios de todos os pacientes para gerenciamento"""
    
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Acesso negado")
    
    from app.routers.task_router import patient_exercises_db
    
    all_exercises = []
    
    for patient_id, exercises in patient_exercises_db.items():
        for exercise in exercises:
            managed_exercise = {
                "id": exercise["id"],
                "title": exercise["title"],
                "description": exercise["description"],
                "points_value": exercise["points_value"],
                "frequency_per_week": exercise["frequency_per_week"],
                "is_active": exercise["is_active"],
                "created_at": exercise["created_at"],
                "patient_id": patient_id,
                "can_delete": True,
                "assigned_by": exercise.get("assigned_by", "Sistema"),
                "assigned_at": exercise.get("assigned_at", "Desconhecido")
            }
            all_exercises.append(managed_exercise)
    
    print(f"🔍 PROFISSIONAL {current_user.id} VISUALIZANDO TODOS OS EXERCÍCIOS:")
    print(f"   - Total de exercícios: {len(all_exercises)}")
    print(f"   - Pacientes afetados: {len(patient_exercises_db)}")
    
    return {
        "success": True,
        "total_exercises": len(all_exercises),
        "total_patients": len(patient_exercises_db),
        "exercises": all_exercises,
        "message": f"Gerenciando {len(all_exercises)} exercícios de {len(patient_exercises_db)} pacientes"
    }


@router.delete("/exercises/{exercise_id}")
def delete_exercise_professional(
    exercise_id: int,
    current_user: dict = Depends(get_current_user)
):
    """Profissional deleta exercício específico"""
    
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Apenas profissionais podem deletar exercícios")
    
    from app.routers.task_router import patient_exercises_db
    
    print(f"🗑️ PROFISSIONAL DELETANDO EXERCÍCIO:")
    print(f"   - Profissional: {current_user.id} ({current_user.role})")
    print(f"   - Exercise ID: {exercise_id}")
    
    exercise_found = False
    deleted_from_patients = []
    
    for patient_id, exercises in patient_exercises_db.items():
        for i, exercise in enumerate(exercises):
            if exercise["id"] == exercise_id:
                patient_exercises_db[patient_id].pop(i)
                exercise_found = True
                deleted_from_patients.append(patient_id)
                print(f"   - Removido do paciente {patient_id}")
                break
    
    if not exercise_found:
        raise HTTPException(status_code=404, detail=f"Exercício {exercise_id} não encontrado")
    
    print(f"✅ EXERCÍCIO DELETADO:")
    print(f"   - Exercise ID: {exercise_id}")
    print(f"   - Removido de: {len(deleted_from_patients)} paciente(s)")
    
    return {
        "success": True,
        "message": f"Exercício {exercise_id} deletado com sucesso!",
        "exercise_id": exercise_id,
        "deleted_from_patients": deleted_from_patients,
        "deleted_by": {
            "id": current_user.id,
            "role": current_user.role,
            "email": current_user.email
        }
    }
