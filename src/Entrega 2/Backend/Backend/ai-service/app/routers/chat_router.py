from fastapi import APIRouter, HTTPException, status
from app.schemas.chat import ChatRequest, ChatResponse
from app.services.chat_service import chat_service
from app.services.ollama_client import ollama_client

router = APIRouter()


@router.post("/chat", response_model=ChatResponse)
async def chat(request: ChatRequest) -> ChatResponse:
    """
    Endpoint principal do assistente SmartSaúde
    
    Processa mensagens do usuário com:
    - Memória de sessão contextual
    - Integração com Ollama/Llama 3
    - Análise de intenção e ações estruturadas
    - Navegação inteligente no app
    
    Args:
        request: Requisição com mensagem e contexto
        
    Returns:
        ChatResponse com resposta, intenção e ação opcional
    """
    try:
        return chat_service.process_message(request)
    except Exception as e:
        # Fallback simples quando Ollama não está disponível
        message = request.message.lower()
        
        # Respostas básicas baseadas em palavras-chave
        if "olá" in message or "oi" in message:
            reply = "Olá! Como posso ajudar você hoje? Sou o assistente SmartSaúde."
            intent = "greeting"
        elif "ajuda" in message or "help" in message:
            reply = "Posso ajudar com:\n• Navegação no app\n• Informações sobre exercícios\n• Dicas de saúde\n• Agendamento de consultas"
            intent = "help"
        elif "exercício" in message or "exercicio" in message:
            reply = "Para ver seus exercícios, vá em Início → Exercícios. Lá você encontrará seus treinos diários e pode acompanhar seu progresso."
            intent = "navigation"
            action = {"screen": "ExerciseListActivity", "params": {}}
        elif "saúde" in message or "medic" in message:
            reply = "Para informações de saúde, vá em Início → Saúde e Ferramentas. Lá você pode calcular IMC, registrar questionários e ver seu histórico."
            intent = "navigation"
            action = {"screen": "HealthHubActivity", "params": {}}
        else:
            reply = "Entendi sua mensagem. Para melhor assistência, você pode usar as seções do app como Exercícios, Saúde ou Progresso. Em que posso ajudar especificamente?"
            intent = "info"
        
        return ChatResponse(
            reply=reply,
            intent=intent,
            action=action if 'action' in locals() else None,
            confidence=0.8
        )


@router.get("/chat/status")
async def chat_status():
    """
    Verifica status do serviço de chat
    
    Returns:
        Status do serviço e fallback
    """
    try:
        ollama_status = ollama_client.check_connection()
        return {
            "chat_service": "online",
            "ollama": ollama_status,
            "mode": "ai_enabled"
        }
    except Exception as e:
        return {
            "chat_service": "online",
            "ollama": {"status": "offline", "error": str(e)},
            "mode": "fallback_enabled"
        }


@router.delete("/chat/session/{session_id}")
async def clear_session(session_id: str):
    """
    Limpa memória de uma sessão específica
    
    Args:
        session_id: ID da sessão para limpar
        
    Returns:
        Status da operação
    """
    try:
        success = chat_service.clear_session(session_id)
        return {
            "session_id": session_id,
            "cleared": success,
            "message": "Sessão limpa com sucesso" if success else "Sessão não encontrada"
        }
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erro ao limpar sessão: {str(e)}"
        )
