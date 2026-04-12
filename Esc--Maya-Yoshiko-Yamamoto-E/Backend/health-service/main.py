"""
Serviço de saúde do SmartSaúde.
Este módulo fornece endpoints para cálculos de métricas de saúde,
questionários médicos e gerenciamento de metas de saúde.
"""

from fastapi import FastAPI, Request, HTTPException, Depends
from fastapi.responses import JSONResponse
import os
import traceback
from datetime import datetime
from sqlalchemy.orm import Session
from app.storage.database import get_db, init_db
from app.services.health_service import HealthService

app = FastAPI(title="SmartSaúde Health Service", version="1.0.0")

# Initialize database tables
@app.on_event("startup")
async def startup_event():
    """Inicializa as tabelas do banco de dados no startup da aplicação."""
    print("HEALTH: Initializing database...")
    init_db()
    print("HEALTH: Database initialized successfully")

# Global exception handler para debug
@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    """Manipulador global de exceções para logging de erros."""
    print(f"HEALTH ERROR: {str(exc)}")
    print(f"HEALTH TRACEBACK: {traceback.format_exc()}")
    print(f"HEALTH REQUEST: {request.method} {request.url}")
    return JSONResponse(
        status_code=500,
        content={"detail": "Internal server error", "error": str(exc)}
    )

@app.get("/health")
def health():
    """Endpoint de verificação de saúde do serviço."""
    print("HEALTH: Health check requested")
    return {"status": "ok", "service": "health-service"}

@app.post("/metrics/imc")
def calculate_imc(weight: float, height: float, user_id: int, db: Session = Depends(get_db)):
    """Calcula o Índice de Massa Corporal (IMC) e classifica o resultado."""
    print(f"HEALTH: IMC Request - User: {user_id}, Weight: {weight}, Height: {height}")
    
    try:
        # Calcular IMC
        imc = weight / (height ** 2)
        
        # Classificar
        if imc < 18.5:
            classification = "Abaixo do peso"
        elif imc < 25:
            classification = "Normal"
        elif imc < 30:
            classification = "Sobrepeso"
        else:
            classification = "Obesidade"
        
        print(f"HEALTH: IMC Result - User: {user_id}, IMC: {imc:.2f}, Classification: {classification}")
        
        # Salvar no banco de dados
        try:
            health_service = HealthService(db)
            metric = health_service.create_health_metric(
                user_id=user_id,
                metric_type="imc",
                value=round(imc, 2),
                unit="kg/m²",
                classification=classification
            )
            print(f"HEALTH: IMC saved to database with ID: {metric.id}")
        except Exception as db_error:
            print(f"HEALTH: Database error saving IMC: {str(db_error)}")
            # Continue even if DB fails
        
        return {
            "metric_type": "imc",
            "value": round(imc, 2),
            "unit": "kg/m²",
            "classification": classification,
            "measured_at": datetime.now().isoformat(),
            "user_id": user_id
        }
    except Exception as e:
        print(f"HEALTH: IMC Calculation Error - {str(e)}")
        raise HTTPException(status_code=500, detail=f"IMC calculation failed: {str(e)}")

@app.post("/metrics/body-fat")
def calculate_body_fat(weight: float, height: float, age: int, gender: str, waist_circumference: float, user_id: int, db: Session = Depends(get_db)):
    """Calcula a porcentagem de gordura corporal usando fórmula simplificada."""
    print(f"HEALTH: Body Fat Request - User: {user_id}, Weight: {weight}, Height: {height}, Age: {age}, Gender: {gender}")
    
    try:
        # Fórmula simplificada de gordura corporal
        if gender.lower() == "male":
            body_fat = (1.20 * weight) + (0.23 * height) - (10.8 * age) - 5.4
        else:
            body_fat = (1.20 * weight) + (0.23 * height) - (10.8 * age) + 5.4
        
        body_fat = max(0, min(100, body_fat))  # Limitar entre 0-100%
        
        # Classificar
        if gender.lower() == "male":
            if body_fat < 6:
                classification = "Essencial"
            elif body_fat < 14:
                classification = "Atleta"
            elif body_fat < 18:
                classification = "Fitness"
            elif body_fat < 25:
                classification = "Médio"
            else:
                classification = "Acima da média"
        else:
            if body_fat < 14:
                classification = "Essencial"
            elif body_fat < 21:
                classification = "Atleta"
            elif body_fat < 25:
                classification = "Fitness"
            elif body_fat < 32:
                classification = "Médio"
            else:
                classification = "Acima da média"
        
        print(f"HEALTH: Body Fat Result - User: {user_id}, Body Fat: {body_fat:.2f}%, Classification: {classification}")
        
        # Salvar no banco de dados
        try:
            health_service = HealthService(db)
            metric = health_service.create_health_metric(
                user_id=user_id,
                metric_type="body_fat",
                value=round(body_fat, 2),
                unit="%",
                classification=classification
            )
            print(f"HEALTH: Body Fat saved to database with ID: {metric.id}")
        except Exception as db_error:
            print(f"HEALTH: Database error saving Body Fat: {str(db_error)}")
            # Continue even if DB fails
        
        return {
            "metric_type": "body_fat",
            "value": round(body_fat, 2),
            "unit": "%",
            "classification": classification,
            "measured_at": datetime.now().isoformat(),
            "user_id": user_id
        }
    except Exception as e:
        print(f"HEALTH: Body Fat Calculation Error - {str(e)}")
        raise HTTPException(status_code=500, detail=f"Body fat calculation failed: {str(e)}")

@app.post("/questionnaire")
def save_questionnaire(user_id: int, medical_history: dict = {}, medications: list = [], allergies: list = [], habits: dict = {}, db: Session = Depends(get_db)):
    """Salva o questionário médico do usuário no banco de dados."""
    print(f"HEALTH: Questionnaire Save - User: {user_id}")
    print(f"HEALTH: Medical History: {medical_history}")
    print(f"HEALTH: Medications: {medications}")
    print(f"HEALTH: Allergies: {allergies}")
    print(f"HEALTH: Habits: {habits}")
    
    try:
        questionnaire_data = {
            "medical_history": medical_history,
            "medications": medications,
            "allergies": allergies,
            "habits": habits,
            "completed_at": datetime.now().isoformat()
        }
        
        # Salvar no banco de dados
        try:
            health_service = HealthService(db)
            questionnaire = health_service.create_questionnaire(
                user_id=user_id,
                questionnaire_data=questionnaire_data,
                version="1.0"
            )
            print(f"HEALTH: Questionnaire saved to database with ID: {questionnaire.id}")
            
            return {
                "id": questionnaire.id,
                "user_id": user_id,
                "questionnaire_data": questionnaire_data,
                "version": "1.0",
                "completed_at": datetime.now().isoformat()
            }
        except Exception as db_error:
            print(f"HEALTH: Database error saving questionnaire: {str(db_error)}")
            raise HTTPException(status_code=500, detail=f"Database error: {str(db_error)}")
        
    except Exception as e:
        print(f"HEALTH: Questionnaire Save Error - {str(e)}")
        raise HTTPException(status_code=500, detail=f"Questionnaire save failed: {str(e)}")

@app.post("/goals")
def create_goal(user_id: int, goal_type: str, target_value: float, notes: str = "", db: Session = Depends(get_db)):
    """Cria uma nova meta de saúde para o usuário."""
    print(f"HEALTH: Goal Create - User: {user_id}, Type: {goal_type}, Target: {target_value}")
    
    try:
        # Salvar no banco de dados
        health_service = HealthService(db)
        goal = health_service.create_goal(
            user_id=user_id,
            goal_type=goal_type,
            target_value=target_value,
            notes=notes
        )
        print(f"HEALTH: Goal saved to database with ID: {goal.id}")
        
        return {
            "id": goal.id,
            "user_id": user_id,
            "goal_type": goal_type,
            "target_value": target_value,
            "current_value": 0.0,
            "target_date": None,
            "is_active": True,
            "achieved": False,
            "notes": notes,
            "progress_percentage": 0.0
        }
    except Exception as e:
        print(f"HEALTH: Goal Create Error - {str(e)}")
        raise HTTPException(status_code=500, detail=f"Goal creation failed: {str(e)}")

@app.get("/metrics/history/{user_id}")
def get_health_history(user_id: int, metric_type: str = None, period: str = "all", db: Session = Depends(get_db)):
    """Retorna o histórico de métricas de saúde do usuário."""
    print(f"HEALTH: History Request - User: {user_id}, Type: {metric_type}, Period: {period}")
    
    try:
        # Buscar do banco de dados
        health_service = HealthService(db)
        metrics = health_service.get_health_metrics(user_id, metric_type)
        
        # Converter para formato de resposta
        result = []
        for metric in metrics:
            result.append({
                "id": metric.id,
                "metric_type": metric.metric_type,
                "value": float(metric.value),
                "unit": metric.unit,
                "classification": metric.classification,
                "measured_at": metric.measured_at.isoformat(),
                "user_id": metric.user_id
            })
        
        return result
    except Exception as e:
        print(f"HEALTH: History Request Error - {str(e)}")
        raise HTTPException(status_code=500, detail=f"History request failed: {str(e)}")

if __name__ == "__main__":
    # Executa o servidor se o arquivo for executado diretamente
    import uvicorn
    print("HEALTH: Starting SmartSaúde Health Service on port 8070")
    uvicorn.run("main:app", host="0.0.0.0", port=8070, reload=True)
