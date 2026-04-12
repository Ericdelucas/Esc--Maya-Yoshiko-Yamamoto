from fastapi import FastAPI, Request, HTTPException, Depends
from fastapi.responses import JSONResponse
import os
import traceback
from datetime import datetime
import pymysql

app = FastAPI(title="SmartSaúde Health Service", version="1.0.0")

# Database connection direta
def get_db_connection():
    try:
        conn = pymysql.connect(
            host='mysql',
            user='smartuser',
            password='smartpass',
            database='smartsaude',
            charset='utf8mb4',
            cursorclass=pymysql.cursors.DictCursor
        )
        return conn
    except Exception as e:
        print(f"HEALTH: Database connection error: {e}")
        return None

def save_health_metric(user_id, metric_type, value, unit, classification):
    """Salvar métrica diretamente no banco"""
    conn = get_db_connection()
    if not conn:
        print("HEALTH: Cannot connect to database")
        return False
    
    try:
        with conn.cursor() as cursor:
            sql = """
            INSERT INTO health_metrics 
            (user_id, metric_type, value, unit, classification, measured_at) 
            VALUES (%s, %s, %s, %s, %s, NOW())
            """
            cursor.execute(sql, (user_id, metric_type, value, unit, classification))
            conn.commit()
            
            print(f"HEALTH: Metric saved directly - User: {user_id}, Type: {metric_type}, Value: {value}")
            return True
            
    except Exception as e:
        print(f"HEALTH: Error saving metric: {e}")
        conn.rollback()
        return False
    finally:
        conn.close()

def save_questionnaire_db(user_id, questionnaire_data):
    """Salvar questionário diretamente no banco"""
    conn = get_db_connection()
    if not conn:
        print("HEALTH: Cannot connect to database")
        return False
    
    try:
        with conn.cursor() as cursor:
            sql = """
            INSERT INTO health_questionnaires 
            (user_id, questionnaire_data, version, completed_at) 
            VALUES (%s, %s, %s, NOW())
            """
            cursor.execute(sql, (user_id, questionnaire_data, "1.0"))
            conn.commit()
            
            print(f"HEALTH: Questionnaire saved directly - User: {user_id}")
            return True
            
    except Exception as e:
        print(f"HEALTH: Error saving questionnaire: {e}")
        conn.rollback()
        return False
    finally:
        conn.close()

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
        
        # Salvar no banco de dados
        success = save_health_metric(user_id, "imc", round(imc, 2), "kg/m²", classification)
        
        if success:
            print(f"HEALTH: IMC saved to database successfully!")
        else:
            print(f"HEALTH: Failed to save IMC to database")
        
        return {
            "metric_type": "imc",
            "value": round(imc, 2),
            "unit": "kg/m²",
            "classification": classification,
            "measured_at": datetime.now().isoformat(),
            "user_id": user_id,
            "db_saved": success
        }
    except Exception as e:
        print(f"HEALTH: IMC Calculation Error - {str(e)}")
        raise HTTPException(status_code=500, detail=f"IMC calculation failed: {str(e)}")

@app.post("/questionnaire")
def save_questionnaire(user_id: int, medical_history: dict = {}, medications: list = [], allergies: list = [], habits: dict = {}):
    print(f"HEALTH: Questionnaire Save - User: {user_id}")
    
    try:
        questionnaire_data = {
            "medical_history": medical_history,
            "medications": medications,
            "allergies": allergies,
            "habits": habits,
            "completed_at": datetime.now().isoformat()
        }
        
        # Salvar no banco de dados
        success = save_questionnaire_db(user_id, questionnaire_data)
        
        if success:
            print(f"HEALTH: Questionnaire saved to database successfully!")
        else:
            print(f"HEALTH: Failed to save questionnaire to database")
        
        return {
            "id": 1 if success else 0,
            "user_id": user_id,
            "questionnaire_data": questionnaire_data,
            "version": "1.0",
            "completed_at": datetime.now().isoformat(),
            "db_saved": success
        }
    except Exception as e:
        print(f"HEALTH: Questionnaire Save Error - {str(e)}")
        raise HTTPException(status_code=500, detail=f"Questionnaire save failed: {str(e)}")

@app.get("/health")
def health():
    print("HEALTH: Health check requested")
    
    # Testar conexão com banco
    conn = get_db_connection()
    db_status = "connected" if conn else "disconnected"
    if conn:
        conn.close()
    
    return {
        "status": "ok", 
        "service": "health-service",
        "database": db_status
    }

if __name__ == "__main__":
    import uvicorn
    print("HEALTH: Starting SmartSaúde Health Service on port 8070")
    uvicorn.run("main_simple:app", host="0.0.0.0", port=8070, reload=True)
