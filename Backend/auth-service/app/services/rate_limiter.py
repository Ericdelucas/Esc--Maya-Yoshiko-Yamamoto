from datetime import datetime, timedelta
from typing import Dict
from threading import Lock


class SimpleRateLimiter:
    """
    Versão simplificada e robusta de rate limiting para login
    """
    
    def __init__(self):
        # Armazena dados por email: {email: {"count": int, "last_attempt": datetime, "blocked_until": datetime}}
        self.attempts: Dict[str, Dict] = {}
        self.lock = Lock()
        
        # Configurações
        self.max_attempts = 10  # Máximo de tentativas permitidas
        self.block_duration_minutes = 5  # Duração do bloqueio em minutos
    
    def record_attempt(self, email: str, success: bool = False) -> Dict:
        """
        Registra uma tentativa de login
        Retorna: dict com status e informações relevantes
        """
        email_lower = email.lower().strip()
        
        with self.lock:
            now = datetime.now()
            
            # Inicializar se não existir
            if email_lower not in self.attempts:
                self.attempts[email_lower] = {
                    "count": 0,
                    "last_attempt": now,
                    "blocked_until": None
                }
            
            user_data = self.attempts[email_lower]
            
            # Verificar se está bloqueado
            if user_data["blocked_until"] and now < user_data["blocked_until"]:
                remaining = int((user_data["blocked_until"] - now).total_seconds())
                return {
                    "allowed": False,
                    "blocked": True,
                    "retry_after": remaining,
                    "message": f"Muitas tentativas. Tente novamente em {remaining // 60} minutos."
                }
            
            # Se sucesso, resetar contador
            if success:
                self.attempts[email_lower] = {
                    "count": 0,
                    "last_attempt": now,
                    "blocked_until": None
                }
                return {"allowed": True, "success": True}
            
            # Se falha, incrementar contador
            user_data["count"] += 1
            user_data["last_attempt"] = now
            
            # Verificar se atingiu limite
            if user_data["count"] >= self.max_attempts:
                user_data["blocked_until"] = now + timedelta(minutes=self.block_duration_minutes)
                remaining = self.block_duration_minutes * 60
                return {
                    "allowed": False,
                    "blocked": True,
                    "retry_after": remaining,
                    "message": f"Muitas tentativas. Tente novamente em {self.block_duration_minutes} minutos."
                }
            
            # Retornar status com tentativas restantes
            remaining = self.max_attempts - user_data["count"]
            return {
                "allowed": True,
                "blocked": False,
                "success": False,
                "attempts_remaining": remaining,
                "attempts_used": user_data["count"],
                "max_attempts": self.max_attempts,
                "message": f"Credenciais inválidas. Restam {remaining} tentativas."
            }
    
    def get_status(self, email: str) -> Dict:
        """
        Retorna status atual do rate limiting para um email
        """
        email_lower = email.lower().strip()
        
        with self.lock:
            if email_lower not in self.attempts:
                return {
                    "identifier": email_lower,
                    "blocked": False,
                    "attempts_used": 0,
                    "attempts_remaining": self.max_attempts,
                    "max_attempts": self.max_attempts,
                    "block_until": None
                }
            
            user_data = self.attempts[email_lower]
            now = datetime.now()
            is_blocked = user_data["blocked_until"] and now < user_data["blocked_until"]
            
            return {
                "identifier": email_lower,
                "blocked": is_blocked,
                "attempts_used": user_data["count"],
                "attempts_remaining": max(0, self.max_attempts - user_data["count"]),
                "max_attempts": self.max_attempts,
                "block_until": user_data["blocked_until"].isoformat() if user_data["blocked_until"] else None
            }


# Instância global do rate limiter
rate_limiter = SimpleRateLimiter()
