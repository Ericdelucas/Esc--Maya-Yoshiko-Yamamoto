from fastapi import FastAPI, Request, HTTPException
from fastapi.responses import JSONResponse
import os
import traceback
from datetime import datetime

app = FastAPI(title="SmartSaúde Health Service", version="1.0.0")

# Global exception handler para debug
@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    print(f"HEALTH ERROR: {str(exc)}")
    print(f"HEALTH TRACEBACK: {traceback.format_exc()}")
    print(f"HEALTH REQUEST: {request.method} {request.url}")
    return JSONResponse(
        status_code=500,
        content={"detail": "Internal server error", "error": str(exc)}
    )

@app.get("/health")
def health():
    print("HEALTH: Health check requested")
    return {"status": "ok", "service": "health-service"}

@app.post("/metrics/imc")
def calculate_imc(weight: float, height: float, user_id: int):
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
        
        # TODO: Salvar no banco de dados
        
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
def calculate_body_fat(weight: float, height: float, age: int, gender: str, waist_circumference: float, user_id: int):
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
        
        # TODO: Salvar no banco de dados
        
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
def save_questionnaire(user_id: int, medical_history: dict = {}, medications: list = [], allergies: list = [], habits: dict = {}):
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
        
        # TODO: Salvar no banco de dados
        
        print(f"HEALTH: Questionnaire Saved - User: {user_id}")
        
        return {
            "id": 1,  # TODO: Retornar ID real do banco
            "user_id": user_id,
            "questionnaire_data": questionnaire_data,
            "version": "1.0",
            "completed_at": datetime.now().isoformat()
        }
    except Exception as e:
        print(f"HEALTH: Questionnaire Save Error - {str(e)}")
        raise HTTPException(status_code=500, detail=f"Questionnaire save failed: {str(e)}")

@app.post("/goals")
def create_goal(user_id: int, goal_type: str, target_value: float, notes: str = ""):
    print(f"HEALTH: Goal Create - User: {user_id}, Type: {goal_type}, Target: {target_value}")
    
    try:
        # TODO: Salvar no banco de dados
        
        print(f"HEALTH: Goal Created - User: {user_id}")
        
        return {
            "id": 1,  # TODO: Retornar ID real do banco
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
def get_health_history(user_id: int, metric_type: str = None, period: str = "all"):
    print(f"HEALTH: History Request - User: {user_id}, Type: {metric_type}, Period: {period}")
    
    try:
        # TODO: Buscar do banco de dados
        
        # Retornar dados de exemplo por enquanto
        return []
    except Exception as e:
        print(f"HEALTH: History Request Error - {str(e)}")
        raise HTTPException(status_code=500, detail=f"History request failed: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    print("HEALTH: Starting SmartSaúde Health Service on port 8070")
    uvicorn.run("main:app", host="0.0.0.0", port=8070, reload=True)
