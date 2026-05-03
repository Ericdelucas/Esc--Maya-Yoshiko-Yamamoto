from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from app.core.dependencies import get_session
from app.models.orm.user_orm import UserORM
from app.models.orm.health_tools_orm import HealthToolsORM, HealthQuestionnaireORM
from typing import List, Dict, Any

router = APIRouter()

@router.get("/pacientes/{patient_id}/health-tools-simple")
def get_patient_health_tools_simple(
    patient_id: int,
    db: Session = Depends(get_session)
) -> Dict[str, Any]:
    """Retorna dados de saúde do paciente (VERSÃO SIMPLES)"""
    
    try:
        # Buscar paciente
        patient = db.query(UserORM).filter(UserORM.id == patient_id).first()
        if not patient:
            return {"success": False, "error": "Paciente não encontrado"}
        
        # Buscar questionários
        questionnaires = db.query(HealthQuestionnaireORM).filter(
            HealthQuestionnaireORM.user_id == patient_id
        ).order_by(HealthQuestionnaireORM.created_at.desc()).limit(5).all()
        
        # Buscar IMCs
        bmis = db.query(HealthToolsORM).filter(
            HealthToolsORM.user_id == patient_id,
            HealthToolsORM.record_type == "bmi"
        ).order_by(HealthToolsORM.created_at.desc()).limit(5).all()
        
        # Formatar dados
        formatted_questionnaires = []
        for q in questionnaires:
            formatted_questionnaires.append({
                "id": q.id,
                "total_score": q.total_score,
                "max_score": q.max_score,
                "risk_level": q.risk_level,
                "created_at": q.created_at.isoformat() if q.created_at else None
            })
        
        formatted_bmis = []
        for bmi in bmis:
            try:
                import json
                bmi_data = json.loads(bmi.value) if isinstance(bmi.value, str) else bmi.value
                formatted_bmis.append({
                    "id": bmi.id,
                    "bmi": bmi_data.get("bmi"),
                    "category": bmi_data.get("category"),
                    "created_at": bmi.created_at.isoformat() if bmi.created_at else None
                })
            except:
                formatted_bmis.append({
                    "id": bmi.id,
                    "bmi": None,
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
