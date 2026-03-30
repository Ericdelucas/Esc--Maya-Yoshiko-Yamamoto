from fastapi import APIRouter, HTTPException, status
from app.schemas.chat import ChatRequest, ChatResponse
from app.services.chat_service import chat_service
from app.services.ollama_client import ollama_client

router = APIRouter()


@router.post("/ai/chat", response_model=ChatResponse)
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
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erro ao processar mensagem: {str(e)}"
        )


@router.get("/ai/chat/status")
async def chat_status():
    """
    Verifica status do serviço de chat
    
    Returns:
        Status do Ollama e estatísticas do serviço
    """
    ollama_status = ollama_client.check_connection()
    
    return {
        "chat_service": "online",
        "ollama": ollama_status,
        "memory_stats": chat_service.get_session_stats("sample")  # Apenas para testar
    }


@router.delete("/ai/chat/session/{session_id}")
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
