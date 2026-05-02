from fastapi import APIRouter, HTTPException, Request, Response
import httpx
import os
import json

router = APIRouter(prefix="/ai", tags=["ai"])

AI_SERVICE_URL = os.getenv("AI_SERVICE_URL", "https://esc-maya-yoshiko-yamamoto.onrender.com")


@router.post("/chat")
async def proxy_ai_chat(request: Request):
    """Endpoint principal com fallback integrado"""
    try:
        payload = await request.json()
        message = str(payload.get("message", "")).lower()
        
        print(f"🔄 CHAT PRINCIPAL - Mensagem: {message}")
        
        # Respostas básicas diretas (sem depender de AI service)
        if "olá" in message or "oi" in message:
            return {"reply": "Olá! Como posso ajudar você hoje? Sou o assistente SmartSaúde.", "intent": "greeting", "action": None}
        elif "ajuda" in message or "help" in message:
            return {"reply": "Posso ajudar com navegação no app, informações sobre exercícios e dicas de saúde.", "intent": "help", "action": None}
        elif "exercício" in message or "exercicio" in message:
            return {"reply": "Para ver seus exercícios, vá em Início → Exercícios.", "intent": "navigation", "action": {"screen": "ExerciseListActivity"}}
        elif "saúde" in message or "medic" in message or "imc" in message:
            return {"reply": "Para informações de saúde, vá em Início → Saúde e Ferramentas.", "intent": "navigation", "action": {"screen": "HealthHubActivity"}}
        else:
            return {"reply": "Entendi. Use as seções Exercícios, Saúde ou Progresso do app.", "intent": "info", "action": None}
            
    except Exception as e:
        print(f"🚨 ERRO NO CHAT PRINCIPAL: {e}")
        return {"reply": "Olá! Como posso ajudar você hoje?", "intent": "greeting", "action": None}


@router.post("/chat/fallback")
async def chat_fallback_direct(request: dict):
    """Endpoint direto de fallback quando AI service falha"""
    message = str(request.get("message", "")).lower()
    
    print(f"🔄 FALLBACK DIRETO - Mensagem: {message}")
    
    # Respostas básicas imediatas
    if "olá" in message or "oi" in message:
        return {"reply": "Olá! Como posso ajudar você hoje? Sou o assistente SmartSaúde.", "intent": "greeting", "action": None}
    elif "ajuda" in message or "help" in message:
        return {"reply": "Posso ajudar com navegação no app, informações sobre exercícios e dicas de saúde.", "intent": "help", "action": None}
    elif "exercício" in message or "exercicio" in message:
        return {"reply": "Para ver seus exercícios, vá em Início → Exercícios.", "intent": "navigation", "action": {"screen": "ExerciseListActivity"}}
    elif "saúde" in message or "medic" in message or "imc" in message:
        return {"reply": "Para informações de saúde, vá em Início → Saúde e Ferramentas.", "intent": "navigation", "action": {"screen": "HealthHubActivity"}}
    else:
        return {"reply": "Entendi. Use as seções Exercícios, Saúde ou Progresso do app.", "intent": "info", "action": None}


def get_professionals_context():
    """Retorna informações sobre profissionais para contexto da IA"""
    # Mock de dados dos profissionais (em produção, viria do banco)
    professionals_mock = [
        {
            "id": 1,
            "name": "Dr. João Silva",
            "email": "joao.silva@saude.com",
            "role": "doctor",
            "specialty": "Médico",
            "experience": "10 anos",
            "focus_areas": ["Ortopedia", "Reabilitação Física"]
        },
        {
            "id": 2,
            "name": "Dra. Maria Santos",
            "email": "maria.santos@saude.com", 
            "role": "professional",
            "specialty": "Fisioterapeuta",
            "experience": "8 anos",
            "focus_areas": ["Fisioterapia Respiratória", "Reabilitação Motora"]
        },
        {
            "id": 3,
            "name": "Carlos Oliveira",
            "email": "carlos.oliveira@saude.com",
            "role": "professional", 
            "specialty": "Fisioterapeuta",
            "experience": "5 anos",
            "focus_areas": ["Fisioterapia Esportiva", "Prevenção de Lesões"]
        }
    ]
    
    return professionals_mock
