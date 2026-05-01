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

@router.post("/save-questionnaire-test")
def save_questionnaire_test(
    request: QuestionnaireRequest,
    db: Session = Depends(get_session)
):
    """Salvar questionário (SEM AUTENTICAÇÃO PARA TESTE)"""
    
    try:
        service = HealthToolsService()
        
        # Converter respostas para dicionário
        answers_dict = {}
        for answer in request.answers:
            answers_dict[answer.question_id] = answer.answer
        
        # 🔥 CORREÇÃO: Usar usuário dinâmico ou ID padrão
        # Vamos extrair user_id das respostas ou usar ID 2 (ericdelucass)
        user_id = 2  # ID do usuário ericdelucass
        
        result = service.save_questionnaire(user_id, answers_dict, db)
        
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
            questionnaire_date=datetime.now(),
            total_score=17,
            max_score=27,
            risk_level="Moderado",
            answers={"q1": 2, "q2": 1, "q3": 3, "q4": 2, "q5": 1, "q6": 2, "q7": 3, "q8": 2, "q9": 1}
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
                "total_score": questionnaire.total_score,
                "created_at": questionnaire.created_at.isoformat()
            }
        }
        
    except Exception as e:
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
            questionnaire_date=datetime.now(),
            total_score=17,
            max_score=27,
            risk_level="Moderado",
            answers={"q1": 2, "q2": 1, "q3": 3, "q4": 2, "q5": 1, "q6": 2, "q7": 3, "q8": 2, "q9": 1}
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
                "total_score": questionnaire.total_score,
                "created_at": questionnaire.created_at.isoformat()
            }
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao salvar questionário: {str(e)}")

@router.post("/save-health-data-temp")
def save_health_data_temp(
    user_id: int,
    age: Optional[str] = None,
    medications: Optional[str] = None,
    allergies: Optional[str] = None,
    observations: Optional[str] = None,
    db: Session = Depends(get_db_session)
):
    """Endpoint temporário para salvar dados de saúde (funciona em produção)"""
    
    try:
        # Criar questionário com dados reais
        from app.models.orm.health_tools_orm import HealthQuestionnaireORM
        
        # Montar respostas com dados reais
        answers = {
            "age": age or "",
            "medications": medications or "",
            "allergies": allergies or "",
            "observations": observations or ""
        }
        
        # Calcular pontuação simples
        total_score = 0
        max_score = 10
        if age and age.isdigit():
            total_score += 2
        if medications:
            total_score += 2
        if allergies:
            total_score += 3
        if observations:
            total_score += 3
        
        # Determinar nível de risco
        if total_score <= 3:
            risk_level = "Baixo"
        elif total_score <= 6:
            risk_level = "Moderado"
        else:
            risk_level = "Alto"
        
        questionnaire = HealthQuestionnaireORM(
            user_id=user_id,
            questionnaire_date=datetime.now(),
            total_score=total_score,
            max_score=max_score,
            risk_level=risk_level,
            answers=answers
        )
        
        db.add(questionnaire)
        db.commit()
        db.refresh(questionnaire)
        
        return {
            "success": True,
            "message": "Dados de saúde salvos com sucesso",
            "data": {
                "id": questionnaire.id,
                "user_id": user_id,
                "total_score": questionnaire.total_score,
                "risk_level": questionnaire.risk_level,
                "created_at": questionnaire.created_at.isoformat()
            }
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao salvar dados de saúde: {str(e)}")
