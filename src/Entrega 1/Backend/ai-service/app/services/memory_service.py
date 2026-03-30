from typing import Dict, Optional
from datetime import datetime, timedelta
import threading
from app.schemas.chat import SessionMemory, ChatMessage


class MemoryService:
    def __init__(self):
        """Serviço de memória em memória local do processo"""
        self._sessions: Dict[str, SessionMemory] = {}
        self._lock = threading.Lock()
        self._cleanup_interval = 3600  # 1 hora
        self._last_cleanup = datetime.now()
    
    def get_session(self, session_id: str) -> SessionMemory:
        """Obtém ou cria sessão"""
        with self._lock:
            if session_id not in self._sessions:
                self._sessions[session_id] = SessionMemory(session_id=session_id)
            
            # Atualiza timestamp de acesso
            self._sessions[session_id].last_access = datetime.now()
            
            # Cleanup periódico
            self._cleanup_old_sessions()
            
            return self._sessions[session_id]
    
    def add_user_message(self, session_id: str, message: str) -> SessionMemory:
        """Adiciona mensagem do usuário à sessão"""
        session = self.get_session(session_id)
        session.add_message("user", message)
        return session
    
    def add_assistant_message(self, session_id: str, message: str, 
                           intent: Optional[str] = None, 
                           target: Optional[str] = None) -> SessionMemory:
        """Adiciona mensagem do assistente à sessão"""
        session = self.get_session(session_id)
        session.add_message("assistant", message)
        session.update_context(intent, target)
        return session
    
    def get_conversation_history(self, session_id: str, limit: int = 10) -> list:
        """Obtém histórico da conversa formatado para Ollama"""
        session = self.get_session(session_id)
        
        history = []
        for msg in session.messages[-limit:]:
            # Verifica se é ChatMessage ou dict
            if hasattr(msg, 'role') and hasattr(msg, 'content'):
                # É ChatMessage
                history.append({
                    "role": msg.role,
                    "content": msg.content
                })
            elif isinstance(msg, dict):
                # Já é dict
                history.append(msg)
            else:
                # Fallback para compatibilidade
                history.append({
                    "role": getattr(msg, 'role', 'unknown'),
                    "content": getattr(msg, 'content', str(msg))
                })
        
        return history
    
    def get_session_context(self, session_id: str) -> dict:
        """Obtém contexto atual da sessão"""
        session = self.get_session(session_id)
        
        return {
            "last_intent": session.last_intent,
            "last_target": session.last_target,
            "message_count": len(session.messages)
        }
    
    def _cleanup_old_sessions(self):
        """Remove sessões antigas (mais de 24 horas sem acesso)"""
        now = datetime.now()
        
        # Executa cleanup apenas se passou o intervalo
        if (now - self._last_cleanup).seconds < self._cleanup_interval:
            return
        
        to_remove = []
        for session_id, session in self._sessions.items():
            last_access = getattr(session, 'last_access', session.session_id)  # Fallback
            if isinstance(last_access, str):
                continue  # Sessões mais antigas não têm timestamp
            
            if (now - last_access) > timedelta(hours=24):
                to_remove.append(session_id)
        
        for session_id in to_remove:
            del self._sessions[session_id]
        
        self._last_cleanup = now
        
        if to_remove:
            print(f"🧹 Cleanup: removidas {len(to_remove)} sessões antigas")
    
    def clear_session(self, session_id: str) -> bool:
        """Limpa sessão específica"""
        with self._lock:
            if session_id in self._sessions:
                del self._sessions[session_id]
                return True
            return False
    
    def get_stats(self) -> dict:
        """Estatísticas do serviço de memória"""
        with self._lock:
            return {
                "active_sessions": len(self._sessions),
                "total_messages": sum(len(s.messages) for s in self._sessions.values()),
                "last_cleanup": self._last_cleanup.isoformat()
            }


# Instância global do serviço
memory_service = MemoryService()
