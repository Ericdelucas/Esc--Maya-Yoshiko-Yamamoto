from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from pydantic import BaseModel, Field
from datetime import datetime
from typing import Optional, List, Dict, Any

from app.core.dependencies import get_current_user, get_session
from app.storage.database.db import get_session as get_db_session
from app.models.schemas.user_schema import UserOut
from app.services.health_tools_service import HealthToolsService
from app.storage.database.health_tools_repository import HealthToolsRepository

router = APIRouter(tags=["health-tools"])

# Pydantic models para requests
class BMICalculationRequest(BaseModel):
    height: float = Field(gt=0, description="Altura em metros")
    weight: float = Field(gt=0, description="Peso em kg")

class BodyFatCalculationRequest(BaseModel):
    height: float = Field(gt=0, description="Altura em metros")
    weight: float = Field(gt=0, description="Peso em kg")
    age: int = Field(gt=0, le=120, description="Idade")
    gender: str = Field(pattern="^(M|F)$", description="Gênero (M ou F)")

class QuestionnaireAnswer(BaseModel):
    question_id: str
    answer: str

class QuestionnaireRequest(BaseModel):
    answers: List[QuestionnaireAnswer]

@router.post("/calculate-bmi")
def calculate_bmi(
    user_id: int,
    height: float,
    weight: float,
    db: Session = Depends(get_db_session)
):
    """Calcular IMC sem autenticação"""
    
    try:
        service = HealthToolsService()
        result = service.calculate_bmi(user_id, height, weight, db)
        
        return {
            "success": True,
            "message": "IMC calculado com sucesso",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao calcular IMC: {str(e)}")

@router.post("/calculate-bmi-public")
def calculate_bmi_public(
    user_id: int,
    height: float,
    weight: float
):
    """Calcular IMC sem autenticação e sem DB"""
    
    try:
        # Calcular IMC diretamente
        bmi = weight / (height * height)
        
        # Determinar categoria
        if bmi < 18.5:
            category = "Abaixo do peso"
        elif bmi < 25:
            category = "Peso normal"
        elif bmi < 30:
            category = "Sobrepeso"
        else:
            category = "Obesidade"
        
        return {
            "success": True,
            "message": "IMC calculado com sucesso",
            "data": {
                "bmi": round(bmi, 2),
                "category": category,
                "height": height,
                "weight": weight,
                "user_id": user_id
            }
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao calcular IMC: {str(e)}")

@router.post("/calculate-bmi-no-auth")
def calculate_bmi_no_auth(
    user_id: int,
    height: float,
    weight: float,
    db: Session = Depends(get_session)
):
    """Calcular IMC sem autenticação (para teste)"""
    
    try:
        service = HealthToolsService()
        result = service.calculate_bmi(user_id, height, weight, db)
        
        return {
            "success": True,
            "message": "IMC calculado com sucesso",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao calcular IMC: {str(e)}")

@router.post("/calculate-body-fat")
def calculate_body_fat(
    request: BodyFatCalculationRequest,
    current_user: UserOut = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Calcular gordura corporal"""
    
    try:
        service = HealthToolsService()
        result = service.calculate_body_fat(
            current_user.id, 
            request.height, 
            request.weight, 
            request.age, 
            request.gender, 
            db
        )
        
        return {
            "success": True,
            "message": "Gordura corporal calculada com sucesso",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao calcular gordura corporal: {str(e)}")

@router.post("/save-questionnaire")
def save_questionnaire(
    request: QuestionnaireRequest,
    current_user: UserOut = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Salvar questionário de saúde"""
    
    try:
        service = HealthToolsService()
        
        # Converter respostas para dicionário
        answers_dict = {}
        for answer in request.answers:
            answers_dict[answer.question_id] = answer.answer
        
        result = service.save_questionnaire(current_user.id, answers_dict, db)
        
        return {
            "success": True,
            "message": "Questionário salvo com sucesso",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao salvar questionário: {str(e)}")

@router.post("/save-questionnaire-test")
def save_questionnaire_test(
    request: QuestionnaireRequest,
    db: Session = Depends(get_session)
):
    """Salvar questionário (SEM AUTENTICAÇÃO PARA TESTE)"""
    
    try:
        service = HealthToolsService()
        # Usar usuário fixo para teste (ID 3)
        result = service.save_questionnaire(3, request.answers, db)
        
        return {
            "success": True,
            "message": "Questionário salvo com sucesso (TESTE)",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao salvar questionário: {str(e)}")

@router.post("/save-questionnaire-simple")
def save_questionnaire_simple(
    user_id: int,
    db: Session = Depends(get_db_session)
):
    """Salvar questionário (SEM AUTENTICAÇÃO E SEM BODY)"""
    
    try:
        # Criar questionário de exemplo
        from app.models.orm.health_tools_orm import HealthQuestionnaireORM
        
        questionnaire = HealthQuestionnaireORM(
            user_id=user_id,
            questionnaire_type="phq9",
            responses={"q1": 2, "q2": 1, "q3": 3, "q4": 2, "q5": 1, "q6": 2, "q7": 3, "q8": 2, "q9": 1},
            score=17,
            created_at=datetime.now()
        )
        
        db.add(questionnaire)
        db.commit()
        db.refresh(questionnaire)
        
        return {
            "success": True,
            "message": "Questionário salvo com sucesso (SIMPLES)",
            "data": {
                "id": questionnaire.id,
                "user_id": user_id,
                "score": questionnaire.score,
                "created_at": questionnaire.created_at.isoformat()
            }
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao salvar questionário: {str(e)}")

        raise HTTPException(status_code=500, detail=f"Erro ao salvar questionário: {str(e)}")

@router.post("/save-questionnaire-test-query")
def save_questionnaire_test_query(
    user_id: int,
    db: Session = Depends(get_db_session)
):
    """Salvar questionário (SEM AUTENTICAÇÃO PARA TESTE) com query params simples"""
    
    try:
        # Criar questionário de exemplo
        from app.models.orm.health_tools_orm import HealthQuestionnaireORM
        
        questionnaire = HealthQuestionnaireORM(
            user_id=user_id,
            questionnaire_type="phq9",
            responses={"q1": 2, "q2": 1, "q3": 3, "q4": 2, "q5": 1, "q6": 2, "q7": 3, "q8": 2, "q9": 1},
            score=17,
            created_at=datetime.now()
        )
        
        db.add(questionnaire)
        db.commit()
        db.refresh(questionnaire)
        
        return {
            "success": True,
            "message": "Questionário salvo com sucesso (TESTE)",
            "data": {
                "id": questionnaire.id,
                "user_id": user_id,
                "score": questionnaire.score,
                "created_at": questionnaire.created_at.isoformat()
            }
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao salvar questionário: {str(e)}")

@router.post("/save-questionnaire-test")
def save_questionnaire_test(
    request: QuestionnaireRequest,
    db: Session = Depends(get_session)
):
    """Salvar questionário de saúde (SEM AUTENTICAÇÃO PARA TESTE)"""
    
    try:
        service = HealthToolsService()
        
        # Converter respostas para dicionário
        answers_dict = {}
        for answer in request.answers:
            answers_dict[answer.question_id] = answer.answer
        
        # Usar usuário fixo para teste (ID 3)
        result = service.save_questionnaire(3, answers_dict, db)
        
        return {
            "success": True,
            "message": "Questionário salvo com sucesso (TESTE)",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao salvar questionário: {str(e)}")

@router.post("/calculate-bmi-test")
def calculate_bmi_test(
    request: BMICalculationRequest,
    db: Session = Depends(get_session)
):
    """Calcular IMC (SEM AUTENTICAÇÃO PARA TESTE)"""
    
    try:
        service = HealthToolsService()
        # Usar usuário fixo para teste (ID 3)
        result = service.calculate_bmi(3, request.height, request.weight, db)
        
        return {
            "success": True,
            "message": "IMC calculado com sucesso (TESTE)",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao calcular IMC: {str(e)}")

        raise HTTPException(status_code=500, detail=f"Erro ao calcular IMC: {str(e)}")

@router.post("/calculate-bmi-test-query")
def calculate_bmi_test_query(
    user_id: int,
    height: float,
    weight: float
):
    """Calcular IMC (SEM AUTENTICAÇÃO PARA TESTE) com query params"""
    
    try:
        # Calcular IMC diretamente
        bmi = weight / (height * height)
        
        # Determinar categoria
        if bmi < 18.5:
            category = "Abaixo do peso"
        elif bmi < 25:
            category = "Peso normal"
        elif bmi < 30:
            category = "Sobrepeso"
        else:
            category = "Obesidade"
        
        return {
            "success": True,
            "message": "IMC calculado com sucesso (TESTE)",
            "data": {
                "bmi": round(bmi, 2),
                "category": category,
                "height": height,
                "weight": weight,
                "user_id": user_id
            }
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao calcular IMC: {str(e)}")

@router.post("/calculate-body-fat-test")
def calculate_body_fat_test(
    request: BodyFatCalculationRequest,
    db: Session = Depends(get_session)
):
    """Calcular gordura corporal (SEM AUTENTICAÇÃO PARA TESTE)"""
    
    try:
        service = HealthToolsService()
        # Usar usuário fixo para teste (ID 3)
        result = service.calculate_body_fat(3, request.height, request.weight, request.age, request.gender, db)
        
        return {
            "success": True,
            "message": "Gordura corporal calculada com sucesso (TESTE)",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao calcular gordura corporal: {str(e)}")

@router.get("/summary")
def get_health_summary(
    current_user: UserOut = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Buscar resumo completo das ferramentas de saúde"""
    
    try:
        service = HealthToolsService()
        summary = service.get_health_summary(current_user.id, db)
        
        return {
            "success": True,
            "message": "Resumo de saúde obtido com sucesso",
            "data": summary
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao buscar resumo: {str(e)}")

        return {
            "success": True,
            "message": "Resumo de saúde obtido com sucesso (PÚBLICO)",
            "data": summary
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao buscar resumo: {str(e)}")

@router.get("/summary-test")
def get_health_summary_test(
    user_id: int,
    db: Session = Depends(get_db_session)
):
    """Buscar resumo completo das ferramentas de saúde (SEM AUTENTICAÇÃO)"""
    
    try:
        from app.models.orm.health_tools_orm import HealthQuestionnaireORM, HealthToolsORM
        
        # Buscar questionários do usuário
        questionnaires = db.query(HealthQuestionnaireORM).filter(
            HealthQuestionnaireORM.user_id == user_id
        ).order_by(HealthQuestionnaireORM.created_at.desc()).limit(10).all()
        
        # Buscar IMCs do usuário
        bmis = db.query(HealthToolsORM).filter(
            HealthToolsORM.user_id == user_id,
            HealthToolsORM.metric_type == "bmi"
        ).order_by(HealthToolsORM.created_at.desc()).limit(10).all()
        
        # Formatar dados
        formatted_questionnaires = []
        for q in questionnaires:
            formatted_questionnaires.append({
                "id": q.id,
                "score": q.score,
                "created_at": q.created_at.isoformat(),
                "responses": q.responses
            })
        
        formatted_bmis = []
        for bmi in bmis:
            formatted_bmis.append({
                "id": bmi.id,
                "value": bmi.value,
                "created_at": bmi.created_at.isoformat(),
                "metadata": bmi.metadata
            })
        
        return {
            "success": True,
            "message": "Resumo de saúde obtido com sucesso (TESTE)",
            "data": {
                "questionnaires": formatted_questionnaires,
                "bmis": formatted_bmis
            }
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao buscar resumo: {str(e)}")

@router.get("/get-patient-data-simple")
def get_patient_data_simple(
    user_id: int,
    db: Session = Depends(get_db_session)
):
    """Buscar dados do paciente de forma simples (SEM AUTENTICAÇÃO)"""
    
    try:
        from app.models.orm.health_tools_orm import HealthQuestionnaireORM, HealthToolsORM
        
        # Buscar questionários do usuário
        questionnaires = db.query(HealthQuestionnaireORM).filter(
            HealthQuestionnaireORM.user_id == user_id
        ).order_by(HealthQuestionnaireORM.created_at.desc()).all()
        
        # Buscar IMCs do usuário
        bmis = db.query(HealthToolsORM).filter(
            HealthToolsORM.user_id == user_id,
            HealthToolsORM.metric_type == "bmi"
        ).order_by(HealthToolsORM.created_at.desc()).all()
        
        # Retornar dados simples
        return {
            "success": True,
            "questionnaires_count": len(questionnaires),
            "bmis_count": len(bmis),
            "questionnaires": [
                {
                    "id": q.id,
                    "score": q.score,
                    "created_at": q.created_at.isoformat()
                } for q in questionnaires
            ],
            "bmis": [
                {
                    "id": bmi.id,
                    "value": bmi.value,
                    "created_at": bmi.created_at.isoformat(),
                    "metadata": bmi.metadata
                } for bmi in bmis
            ]
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao buscar dados: {str(e)}")

@router.get("/get-latest-data")
def get_latest_data(
    user_id: int,
    db: Session = Depends(get_db_session)
):
    """Buscar os dados mais recentes do usuário (SEM AUTENTICAÇÃO)"""
    
    try:
        from app.models.orm.health_tools_orm import HealthQuestionnaireORM, HealthToolsORM
        
        # Buscar último questionário
        last_questionnaire = db.query(HealthQuestionnaireORM).filter(
            HealthQuestionnaireORM.user_id == user_id
        ).order_by(HealthQuestionnaireORM.created_at.desc()).first()
        
        # Buscar último IMC
        last_bmi = db.query(HealthToolsORM).filter(
            HealthToolsORM.user_id == user_id,
            HealthToolsORM.metric_type == "bmi"
        ).order_by(HealthToolsORM.created_at.desc()).first()
        
        # Buscar contadores totais
        q_count = db.query(HealthQuestionnaireORM).filter(
            HealthQuestionnaireORM.user_id == user_id
        ).count()
        
        bmi_count = db.query(HealthToolsORM).filter(
            HealthToolsORM.user_id == user_id,
            HealthToolsORM.metric_type == "bmi"
        ).count()
        
        return {
            "success": True,
            "questionnaires_count": q_count,
            "bmis_count": bmi_count,
            "latest_questionnaire": {
                "id": last_questionnaire.id if last_questionnaire else None,
                "score": last_questionnaire.score if last_questionnaire else None,
                "created_at": last_questionnaire.created_at.isoformat() if last_questionnaire else None
            },
            "latest_bmi": {
                "id": last_bmi.id if last_bmi else None,
                "value": last_bmi.value if last_bmi else None,
                "metadata": last_bmi.metadata if last_bmi else None,
                "created_at": last_bmi.created_at.isoformat() if last_bmi else None
            }
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao buscar dados: {str(e)}")

@router.get("/history")
def get_health_history(
    limit: int = 50,
    current_user: UserOut = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Buscar histórico completo de saúde"""
    
    try:
        service = HealthToolsService()
        history = service.get_health_history(current_user.id, limit, db)
        
        return {
            "success": True,
            "message": "Histórico obtido com sucesso",
            "data": history,
            "total": len(history)
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao buscar histórico: {str(e)}")

@router.get("/bmi-history")
def get_bmi_history(
    limit: int = 10,
    user_id: int = None,
    current_user: UserOut = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Buscar histórico específico de IMC"""
    
    try:
        service = HealthToolsService()
        # Usar user_id se fornecido, senão usar do usuário autenticado
        target_user_id = user_id if user_id is not None else current_user.id
        history = service.get_bmi_history(target_user_id, limit, db)
        
        return {
            "success": True,
            "message": "Histórico de IMC obtido com sucesso",
            "data": history,
            "total": len(history)
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao buscar histórico de IMC: {str(e)}")

@router.get("/body-fat-history")
def get_body_fat_history(
    limit: int = 10,
    current_user: UserOut = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Buscar histórico específico de gordura corporal"""
    
    try:
        service = HealthToolsService()
        history = service.get_body_fat_history(current_user.id, limit, db)
        
        return {
            "success": True,
            "message": "Histórico de gordura corporal obtido com sucesso",
            "data": history,
            "total": len(history)
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao buscar histórico de gordura corporal: {str(e)}")

@router.get("/questionnaire-history")
def get_questionnaire_history(
    limit: int = 10,
    user_id: int = None,
    current_user: UserOut = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Buscar histórico específico de questionários"""
    
    try:
        service = HealthToolsService()
        # Usar user_id se fornecido, senão usar do usuário autenticado
        target_user_id = user_id if user_id is not None else current_user.id
        history = service.get_questionnaire_history(target_user_id, limit, db)
        
        return {
            "success": True,
            "message": "Histórico de questionários obtido com sucesso",
            "data": history,
            "total": len(history)
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao buscar histórico de questionários: {str(e)}")

@router.get("/bmi-history-test")
def get_bmi_history_test(
    user_id: int,
    limit: int = 10,
    db: Session = Depends(get_db_session)
):
    """Buscar histórico específico de IMC (SEM AUTENTICAÇÃO)"""
    
    try:
        service = HealthToolsService()
        history = service.get_bmi_history(user_id, limit, db)
        
        return {
            "success": True,
            "message": "Histórico de IMC obtido com sucesso (TESTE)",
            "data": history,
            "total": len(history)
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao buscar histórico de IMC: {str(e)}")
@router.get("/questionnaire-history-test")
def get_questionnaire_history_test(
    user_id: int,
    limit: int = 10,
    db: Session = Depends(get_db_session)
):
    """Buscar histórico específico de questionários (SEM AUTENTICAÇÃO)"""
    
    try:
        service = HealthToolsService()
        history = service.get_questionnaire_history(user_id, limit, db)
        
        return {
            "success": True,
            "message": "Histórico de questionários obtido com sucesso (TESTE)",
            "data": history,
            "total": len(history)
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao buscar histórico de questionários: {str(e)}")



@router.get("/patient/{patient_id}")
def get_patient_data(
    patient_id: int,
    db: Session = Depends(get_session)
):
    """Retorna dados simples do paciente"""
    try:
        from app.models.orm.user_orm import UserORM
        
        patient = db.query(UserORM).filter(UserORM.id == patient_id).first()
        if not patient:
            return {"success": False, "error": "Paciente não encontrado"}
        
        return {
            "success": True,
            "patient_id": patient_id,
            "name": patient.full_name or patient.email.split("@")[0],
            "email": patient.email,
            "role": patient.role,
            "message": "Paciente encontrado!"
        }
    except Exception as e:
        return {"success": False, "error": str(e)}


@router.get("/questionnaire-template")
def get_questionnaire_template():
    """Obter template do questionário de saúde"""
    
    questionnaire = {
        "title": "Avaliação de Saúde Geral",
        "description": "Responda às perguntas abaixo para avaliar seu estado de saúde atual",
        "questions": [
            {
                "id": "smoking",
                "text": "Você fuma?",
                "type": "multiple_choice",
                "options": ["yes", "no"],
                "required": True
            },
            {
                "id": "alcohol",
                "text": "Com que frequência você consome bebidas alcoólicas?",
                "type": "multiple_choice",
                "options": ["daily", "weekly", "rarely", "never"],
                "required": True
            },
            {
                "id": "exercise",
                "text": "Com que frequência você pratica atividades físicas?",
                "type": "multiple_choice",
                "options": ["daily", "weekly", "rarely", "never"],
                "required": True
            },
            {
                "id": "sleep",
                "text": "Quantas horas de sono você tem por noite?",
                "type": "multiple_choice",
                "options": ["7-8h", "5-6h", "<5h", ">9h"],
                "required": True
            },
            {
                "id": "stress",
                "text": "Como você classificaria seu nível de estresse atual?",
                "type": "multiple_choice",
                "options": ["low", "medium", "high", "very_high"],
                "required": True
            },
            {
                "id": "chronic_diseases",
                "text": "Você tem alguma doença crônica? Quais?",
                "type": "text",
                "required": False
            },
            {
                "id": "medications",
                "text": "Você usa algum medicamento contínuo? Quais?",
                "type": "text",
                "required": False
            }
        ]
    }
    
    return {
        "success": True,
        "message": "Template do questionário obtido com sucesso",
        "data": questionnaire
    }


@router.get("/pacientes/{patient_id}/health-data")
def get_patient_health_data(
    patient_id: int,
    db: Session = Depends(get_session)
) -> Dict[str, Any]:
    """Retorna dados de saúde do paciente (versão simples)"""
    
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
