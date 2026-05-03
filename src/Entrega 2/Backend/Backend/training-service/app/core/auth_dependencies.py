import httpx
from fastapi import Depends, HTTPException, status
from typing import Dict


async def get_current_user(authorization: str = None) -> Dict:
    """Extrai usuário do token JWT"""
    if not authorization:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Missing authorization header"
        )
    
    if not authorization.startswith("Bearer "):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid authorization format"
        )
    
    token = authorization.split(" ")[1]
    
    # Validação simplificada - chamar auth-service
    try:
        async with httpx.AsyncClient() as client:
            response = await client.get(
                "http://auth-service:8080/auth/verify",
                headers={"Authorization": f"Bearer {token}"}
            )
            
            if response.status_code != 200:
                raise HTTPException(
                    status_code=status.HTTP_401_UNAUTHORIZED,
                    detail="Invalid token"
                )
            
            return response.json()
            
    except Exception:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Token validation failed"
        )


def require_permission(permission: str = None):
    """Decorator para verificar permissões (simplificado)"""
    def dependency(current_user: Dict = Depends(get_current_user)):
        # Simplificado - apenas verifica se está autenticado
        if current_user.get("role") not in ["Patient", "Professional", "Admin"]:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Not authorized"
            )
        return current_user
    return dependency
