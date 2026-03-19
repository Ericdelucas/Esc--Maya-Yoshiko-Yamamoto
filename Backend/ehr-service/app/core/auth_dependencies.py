import httpx
from fastapi import Depends, HTTPException, status, Header
from typing import Dict


async def get_current_user(authorization: str = Header(default=None)) -> Dict:
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
    """Decorator para verificar permissões"""
    def dependency(current_user: Dict = Depends(get_current_user)):
        role = current_user.get("role")
        
        # Bloquear pacientes de acessar endpoints de profissionais
        if permission == "professional" and role == "Patient":
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Patients cannot access professional endpoints"
            )
        
        # Apenas Professional e Admin podem acessar endpoints profissionais
        if permission == "professional" and role not in ["Professional", "Admin"]:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Not authorized"
            )
        
        return current_user
    return dependency


def require_professional_or_admin():
    """Apenas Professional e Admin podem acessar"""
    def dependency(current_user: Dict = Depends(get_current_user)):
        role = current_user.get("role")
        if role not in ["Professional", "Admin"]:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Only Professional and Admin can access this endpoint"
            )
        return current_user
    return dependency
