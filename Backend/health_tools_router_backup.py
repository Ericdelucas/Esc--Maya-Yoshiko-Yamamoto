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
            questionnaire_date=datetime.now(),
            answers={"q1": 2, "q2": 1, "q3": 3, "q4": 2, "q5": 1, "q6": 2, "q7": 3, "q8": 2, "q9": 1},
            total_score=17, max_score=27, risk_level="Moderado",
            
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
            answers={"q1": 2, "q2": 1, "q3": 3, "q4": 2, "q5": 1, "q6": 2, "q7": 3, "q8": 2, "q9": 1},
            total_score=17, max_score=27, risk_level="Moderado",
            
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

@router.post("/save-questionnaire-test")
