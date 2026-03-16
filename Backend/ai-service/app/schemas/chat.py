from pydantic import BaseModel, Field
from typing import Optional, Dict, Any, List


class ChatAction(BaseModel):
    type: str = Field(..., description="Tipo da ação: 'open_screen', 'navigate', 'execute'")
    target: str = Field(..., description="Alvo da ação: nome da tela ou rota")
    label: str = Field(..., description="Texto para exibição no botão/link")


class ChatRequest(BaseModel):
    session_id: str = Field(..., description="ID único da sessão do usuário")
    user_id: int = Field(..., description="ID do usuário")
    message: str = Field(..., description="Mensagem do usuário")
    screen_context: Optional[str] = Field(None, description="Tela atual do app")
    locale: Optional[str] = Field("pt", description="Idioma do usuário (pt/en)")


class ChatResponse(BaseModel):
    reply: str = Field(..., description="Resposta do assistente")
    intent: str = Field(..., description="Intenção detectada: 'navigation', 'info', 'help', 'other'")
    action: Optional[ChatAction] = Field(None, description="Ação estruturada opcional")
    memory_updated: bool = Field(True, description="Se a memória da sessão foi atualizada")


class ChatMessage(BaseModel):
    role: str = Field(..., description="user ou assistant")
    content: str = Field(..., description="Conteúdo da mensagem")


class SessionMemory(BaseModel):
    session_id: str
    messages: List[ChatMessage] = Field(default_factory=list)
    last_intent: Optional[str] = None
    last_target: Optional[str] = None
    last_access: Optional[str] = None
    
    def add_message(self, role: str, content: str):
        """Adiciona mensagem e mantém apenas as últimas 10"""
        self.messages.append(ChatMessage(role=role, content=content))
        if len(self.messages) > 10:
            self.messages = self.messages[-10:]
    
    def update_context(self, intent: Optional[str] = None, target: Optional[str] = None):
        """Atualiza último contexto"""
        if intent:
            self.last_intent = intent
        if target:
            self.last_target = target
