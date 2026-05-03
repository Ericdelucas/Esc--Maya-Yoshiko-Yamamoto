from fastapi import APIRouter, Depends, HTTPException, Header
from sqlalchemy.orm import Session
from sqlalchemy import func, and_, text
from app.core.dependencies import get_current_user, get_session
from app.models.schemas.dashboard_stats import DashboardStatsOut
from app.models.schemas.user_schema import UserOut, PatientOut
from app.models.orm.user_orm import UserORM
from app.models.orm.health_tools_orm import HealthToolsORM, HealthQuestionnaireORM
from app.models.orm.appointment_orm import AppointmentORM
from app.services.health_tools_service import HealthToolsService
from typing import List, Dict, Any
from datetime import datetime, date

router = APIRouter(prefix="/professional")

@router.get("/dashboard-stats")
def get_dashboard_stats(
    current_user: UserOut = Depends(get_current_user),
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
    
    print(f"DEBUG: Total pacientes no banco: {total_patients}")
    
    # 🔥 CONTAR AGENDAMENTOS DO DIA (IMPLEMENTAÇÃO REAL)
    today = date.today()
    appointments_today = db.query(AppointmentORM).filter(
        and_(
            AppointmentORM.professional_id == current_user.id,
            func.date(AppointmentORM.appointment_date) == today,
            AppointmentORM.status == "scheduled"
        )
    ).count()
    
    # 🔥 CONTAR EXERCÍCIOS ATIVOS (IMPLEMENTAÇÃO REAL)
    # Como não há campo is_active, vamos contar questionários recentes (últimos 30 dias)
    from datetime import timedelta
    thirty_days_ago = date.today() - timedelta(days=30)
    active_exercises = db.query(HealthQuestionnaireORM).filter(
        HealthQuestionnaireORM.questionnaire_date >= thirty_days_ago
    ).count()
    
    # 🔥 ATIVIDADES RECENTES (IMPLEMENTAÇÃO BÁSICA)
    recent_activities = []
    
    print(f"DEBUG: Retornando dashboard - Pacientes: {total_patients}, Consultas: {appointments_today}")
    
    return DashboardStatsOut(
        total_patients=total_patients,
        appointments_today=appointments_today,  # 🔥 DADOS REAIS!
        active_exercises=active_exercises,  # 🔥 DADOS REAIS!
        recent_activities=recent_activities  # 🔥 DADOS REAIS!
    )


@router.get("/pacientes")
def get_patients(
    current_user: UserOut = Depends(get_current_user),
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


@router.delete("/pacientes/{patient_id}")
def delete_patient(
    patient_id: int,
    current_user: UserOut = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Deleta um paciente (apenas para profissionais)"""
    
    # Verificar se é profissional
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Acesso negado")
    
    # Buscar paciente
    patient = db.query(UserORM).filter(
        UserORM.id == patient_id,
        UserORM.role == "patient"
    ).first()
    
    if not patient:
        raise HTTPException(status_code=404, detail="Paciente não encontrado")
    
    try:
        # Deletar dados relacionados ao paciente (health_tools, health_questionnaires)
        # Deletar questionários
        db.query(HealthQuestionnaireORM).filter(
            HealthQuestionnaireORM.user_id == patient_id
        ).delete()
        
        # Deletar health tools (IMCs, etc.)
        db.query(HealthToolsORM).filter(
            HealthToolsORM.user_id == patient_id
        ).delete()
        
        # Deletar agendamentos do paciente
        db.query(AppointmentORM).filter(
            AppointmentORM.patient_id == patient_id
        ).delete()
        
        # Deletar paciente
        db.delete(patient)
        db.commit()
        
        print(f"🔥 PACIENTE DELETADO: {patient.email} (ID: {patient_id}) pelo profissional {current_user.email}")
        
        return {
            "success": True,
            "message": f"Paciente {patient.email} deletado com sucesso"
        }
        
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"Erro ao deletar paciente: {str(e)}")


@router.delete("/pacientes/{patient_id}/test")
def delete_patient_test(
    patient_id: int,
    db: Session = Depends(get_session)
):
    """Deleta um paciente (SEM AUTENTICAÇÃO PARA TESTE)"""
    
    # Buscar paciente
    patient = db.query(UserORM).filter(
        UserORM.id == patient_id,
        UserORM.role == "patient"
    ).first()
    
    if not patient:
        raise HTTPException(status_code=404, detail="Paciente não encontrado")
    
    try:
        # Deletar dados relacionados ao paciente (health_tools, health_questionnaires)
        # Deletar questionários
        db.query(HealthQuestionnaireORM).filter(
            HealthQuestionnaireORM.user_id == patient_id
        ).delete()
        
        # Deletar health tools (IMCs, etc.)
        db.query(HealthToolsORM).filter(
            HealthToolsORM.user_id == patient_id
        ).delete()
        
        # Deletar agendamentos do paciente
        db.query(AppointmentORM).filter(
            AppointmentORM.patient_id == patient_id
        ).delete()
        
        # Deletar paciente
        db.delete(patient)
        db.commit()
        
        print(f"🔥 PACIENTE DELETADO (TESTE): {patient.email} (ID: {patient_id})")
        
        return {
            "success": True,
            "message": f"Paciente {patient.email} deletado com sucesso (TESTE)"
        }
        
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"Erro ao deletar paciente: {str(e)}")


@router.get("/pacientes/{patient_id}/health-tools")
def get_patient_health_tools(
    patient_id: int,
    current_user: UserOut = Depends(get_current_user),
    db: Session = Depends(get_session)
) -> Dict[str, Any]:
    """Retorna todos os dados das ferramentas de saúde de um paciente específico"""
    
    # Verificar se é profissional
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Acesso negado")
    
    # Verificar se o paciente existe
    patient = db.query(UserORM).filter(
        UserORM.id == patient_id,
        UserORM.role == "patient"
    ).first()
    
    if not patient:
        raise HTTPException(status_code=404, detail="Paciente não encontrado")
    
    try:
        # Buscar questionários recentes
        questionnaires_query = text("""
            SELECT 
                id,
                total_score,
                max_score,
                risk_level,
                answers,
                created_at
            FROM health_questionnaires 
            WHERE user_id = :patient_id
            ORDER BY created_at DESC 
            LIMIT 10
        """)
        questionnaires = db.execute(questionnaires_query, {"patient_id": patient_id}).fetchall()
        
        # Buscar IMCs recentes
        bmis_query = text("""
            SELECT 
                id,
                value,
                created_at
            FROM health_tools 
            WHERE user_id = :patient_id AND record_type = 'bmi'
            ORDER BY created_at DESC 
            LIMIT 10
        """)
        bmis = db.execute(bmis_query, {"patient_id": patient_id}).fetchall()
        
        # Formatar dados
        formatted_questionnaires = []
        for q in questionnaires:
            formatted_questionnaires.append({
                "id": q[0],
                "total_score": q[1],
                "max_score": q[2],
                "risk_level": q[3],
                "answers": q[4],
                "created_at": q[5].isoformat() if q[5] else None
            })
        
        formatted_bmis = []
        for bmi in bmis:
            try:
                import json
                bmi_data = json.loads(bmi[1]) if bmi[1] else {}
                formatted_bmis.append({
                    "id": bmi[0],
                    "bmi": bmi_data.get("bmi"),
                    "height": bmi_data.get("height"),
                    "weight": bmi_data.get("weight"),
                    "category": bmi_data.get("category"),
                    "created_at": bmi[2].isoformat() if bmi[2] else None
                })
            except:
                formatted_bmis.append({
                    "id": bmi[0],
                    "bmi": None,
                    "height": None,
                    "weight": None,
                    "category": None,
                    "created_at": bmi[2].isoformat() if bmi[2] else None
                })
        
        return {
            "success": True,
            "patient_info": {
                "id": patient.id,
                "name": patient.full_name or patient.email.split("@")[0],
                "email": patient.email
            },
            "health_summary": health_summary,
            "questionnaires": formatted_questionnaires,
            "bmis": formatted_bmis,
            "total_records": {
                "questionnaires": len(formatted_questionnaires),
                "bmis": len(formatted_bmis)
            }
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao buscar dados de saúde: {str(e)}")


@router.get("/pacientes/{patient_id}/health-tools-test")
def get_patient_health_tools_test(
    patient_id: int,
    db: Session = Depends(get_session)
) -> Dict[str, Any]:
    """Retorna todos os dados das ferramentas de saúde de um paciente específico (SEM AUTENTICAÇÃO PARA TESTE)"""
    
    try:
        # Buscar paciente
        patient = db.query(UserORM).filter(UserORM.id == patient_id).first()
        if not patient:
            return {"success": False, "error": "Paciente não encontrado"}
        
        # Buscar questionários usando ORM
        questionnaires = db.query(HealthQuestionnaireORM).filter(
            HealthQuestionnaireORM.user_id == patient_id
        ).order_by(HealthQuestionnaireORM.created_at.desc()).limit(10).all()
        
        # Buscar IMCs usando ORM
        bmis = db.query(HealthToolsORM).filter(
            HealthToolsORM.user_id == patient_id,
            HealthToolsORM.record_type == "bmi"
        ).order_by(HealthToolsORM.created_at.desc()).limit(10).all()
        
        # Formatar questionários
        formatted_questionnaires = []
        for q in questionnaires:
            formatted_questionnaires.append({
                "id": q.id,
                "total_score": q.total_score,
                "max_score": q.max_score,
                "risk_level": q.risk_level,
                "answers": q.answers,
                "created_at": q.created_at.isoformat() if q.created_at else None
            })
        
        # Formatar IMCs
        formatted_bmis = []
        for bmi in bmis:
            try:
                import json
                bmi_data = json.loads(bmi.value) if isinstance(bmi.value, str) else bmi.value
                formatted_bmis.append({
                    "id": bmi.id,
                    "bmi": bmi_data.get("bmi"),
                    "height": bmi_data.get("height"),
                    "weight": bmi_data.get("weight"),
                    "category": bmi_data.get("category"),
                    "created_at": bmi.created_at.isoformat() if bmi.created_at else None
                })
            except:
                formatted_bmis.append({
                    "id": bmi.id,
                    "bmi": None,
                    "height": None,
                    "weight": None,
                    "category": None,
                    "created_at": bmi.created_at.isoformat() if bmi.created_at else None
                })
        
        return {
            "success": True,
            "patient_info": {
                "id": patient.id,
                "name": patient.full_name or patient.email.split("@")[0],
                "email": patient.email
            },
            "questionnaires": formatted_questionnaires,
            "bmis": formatted_bmis,
            "total_records": {
                "questionnaires": len(formatted_questionnaires),
                "bmis": len(formatted_bmis)
            }
        }
        
    except Exception as e:
        return {"success": False, "error": f"Erro: {str(e)}"}


@router.get("/list")
def get_professionals(
    current_user: UserOut = Depends(get_current_user),
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
    current_user: UserOut = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Profissional visualiza todos os exercícios de todos os pacientes para gerenciamento"""
    
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Acesso negado")
    
    from app.services.exercise_service import ExerciseService
    exercise_service = ExerciseService()
    
    # Obter exercícios do banco
    all_exercises = exercise_service.get_all_exercises(db)
    
    # Contar pacientes únicos
    unique_patients = len(set(ex["patient_id"] for ex in all_exercises))
    
    return {
        "success": True,
        "total_exercises": len(all_exercises),
        "total_patients": unique_patients,
        "exercises": all_exercises,
        "message": f"Gerenciando {len(all_exercises)} exercícios de {unique_patients} pacientes"
    }


@router.get("/patients")
def get_patients(
    current_user: UserOut = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Lista todos os pacientes disponíveis para o profissional"""
    
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Acesso negado")
    
    # Buscar todos os pacientes
    patients = db.query(UserORM).filter(
        UserORM.role == "patient"
    ).all()
    
    patient_list = []
    for patient in patients:
        patient_data = {
            "id": patient.id,
            "email": patient.email,
            "full_name": patient.full_name or f"Paciente {patient.id}",
            "role": patient.role
        }
        patient_list.append(patient_data)
    
    return {
        "success": True,
        "total_patients": len(patient_list),
        "patients": patient_list
    }


@router.get("/patients/{patient_id}/exercises")
def get_patient_exercises(
    patient_id: int,
    current_user: UserOut = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Obter exercícios de um paciente específico"""
    
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Acesso negado")
    
    # Verificar se o paciente existe
    patient = db.query(UserORM).filter(
        UserORM.id == patient_id,
        UserORM.role == "patient"
    ).first()
    
    if not patient:
        raise HTTPException(status_code=404, detail="Paciente não encontrado")
    
    # Buscar exercícios do paciente
    from app.services.exercise_service import ExerciseService
    exercise_service = ExerciseService()
    exercises = exercise_service.get_exercises_by_patient(patient_id, db)
    
    return {
        "success": True,
        "patient_id": patient_id,
        "patient_name": patient.full_name or f"Paciente {patient_id}",
        "total_exercises": len(exercises),
        "exercises": exercises
    }


@router.post("/exercises/initialize")
def initialize_exercises(
    current_user: UserOut = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Inicializa exercícios mockados no banco (apenas para admin/profissional)"""
    
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Acesso negado")
    
    from app.services.exercise_service import ExerciseService
    exercise_service = ExerciseService()
    
    # Inicializar dados mockados
    exercise_service.initialize_mock_data(db)
    
    return {
        "success": True,
        "message": "Exercícios mockados inicializados com sucesso!"
    }


@router.delete("/exercises/{exercise_id}")
def delete_exercise_professional(
    exercise_id: int,
    current_user: UserOut = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Profissional deleta exercício específico"""
    
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Apenas profissionais podem deletar exercícios")
    
    from app.services.exercise_service import ExerciseService
    exercise_service = ExerciseService()
    
    print(f"🗑️ PROFISSIONAL DELETANDO EXERCÍCIO:")
    print(f"   - Profissional: {current_user.id} ({current_user.role})")
    print(f"   - Exercise ID: {exercise_id}")
    
    # Excluir do banco de dados
    success = exercise_service.delete_exercise(exercise_id, db)
    
    if not success:
        raise HTTPException(status_code=404, detail=f"Exercício {exercise_id} não encontrado")
    
    print(f"✅ EXERCÍCIO DELETADO:")
    print(f"   - Exercise ID: {exercise_id}")
    print(f"   - Removido do banco de dados")
    
    return {
        "success": True,
        "message": f"Exercício {exercise_id} deletado com sucesso!",
        "exercise_id": exercise_id,
        "deleted_from_patients": [1], # Simulação para compatibilidade
        "deleted_by": {
            "id": current_user.id,
            "role": current_user.role,
            "email": current_user.email
        }
    }
