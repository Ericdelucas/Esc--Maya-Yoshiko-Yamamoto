from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routers.translate_router import router as translate_router
# from app.routers.pose_router import router as pose_router  # Temporarily disabled
# from app.routers.pose_ws_router import router as pose_ws_router  # Temporarily disabled
from app.routers.ws_probe_router import router as ws_probe_router
from app.routers.health_router import router as health_router
from app.routers.chat_router import router as chat_router


def create_app() -> FastAPI:
    app = FastAPI(title="smartsaude-ai", version="1.0.0")
    
    # Adicionar endpoint de teste WebSocket direto no app
    @app.websocket("/test-ws")
    async def test_websocket(websocket):
        await websocket.accept()
        await websocket.send_json({"hello": "direct_test"})
    
    # Endpoint direto para chat (fallback rápido)
    @app.post("/ai/chat")
    async def chat_fallback(request: dict):
        """Fallback rápido quando o serviço principal falha"""
        message = str(request.get("message", "")).lower()
        
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
    
    app.include_router(health_router)
    app.include_router(translate_router, prefix="/ai", tags=["ai"])
    # app.include_router(pose_router)  # Temporarily disabled
    # app.include_router(pose_ws_router, tags=["websocket"])  # Temporarily disabled
    app.include_router(ws_probe_router, tags=["websocket"])
    app.include_router(chat_router, prefix="/ai", tags=["chat"])
    return app


app = create_app()
