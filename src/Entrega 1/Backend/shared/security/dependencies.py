from typing import Callable
from fastapi import Depends, HTTPException, status, Header
from .rbac import has_permission


def get_current_user(authorization: str = Header(default=None)) -> dict:
    """
    Get current user from token for RBAC permission checking
    """
    if not authorization:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Authentication required"
        )
    
    if not authorization.lower().startswith("bearer "):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid authorization header format"
        )
    
    token = authorization.split(" ", 1)[1].strip()
    
    # Here you should validate the token with auth service
    # For now, we'll simulate the token validation
    try:
        import requests
        from app.core.config import settings
        resp = requests.get(
            f"{settings.auth_base_url}/auth/verify",
            headers={"Authorization": f"Bearer {token}"},
            timeout=5,
        )
        if resp.status_code != 200:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Invalid token"
            )

        payload = resp.json()
        # Handle the actual response format from auth-service
        if "claims" in payload:
            claims = payload["claims"]
        else:
            claims = payload
            
        return claims
    except Exception:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="Auth service unavailable"
        )


def require_permission(permission: str):
    """
    FastAPI dependency to require specific permission
    
    Args:
        permission: Permission string required to access the endpoint
        
    Returns:
        Dependency function that checks permission and returns user data
        
    Usage:
        @router.post("/exercises")
        def create_exercise(
            payload: ExerciseCreate,
            current_user=Depends(require_permission("exercise:create"))
        ):
            # current_user contains the verified user data
            pass
    """
    def checker(current_user: dict = Depends(get_current_user)):
        if not current_user:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Authentication required"
            )
            
        role = current_user.get("role", "").strip()
        
        if not has_permission(role, permission):
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=f"Insufficient permissions. Required: {permission}"
            )
            
        return current_user
    
    return checker


def require_any_permission(permissions: list[str]):
    """
    FastAPI dependency to require any of the specified permissions
    
    Args:
        permissions: List of permission strings (user needs at least one)
        
    Returns:
        Dependency function that checks permissions
    """
    def checker(current_user: dict = Depends(get_current_user)):
        if not current_user:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Authentication required"
            )
            
        role = current_user.get("role", "").strip()
        
        has_any = any(has_permission(role, perm) for perm in permissions)
        
        if not has_any:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=f"Insufficient permissions. Required any of: {permissions}"
            )
            
        return current_user
    
    return checker


def require_all_permissions(permissions: list[str]):
    """
    FastAPI dependency to require all specified permissions
    
    Args:
        permissions: List of permission strings (user needs all of them)
        
    Returns:
        Dependency function that checks permissions
    """
    def checker(current_user: dict = Depends(get_current_user)):
        if not current_user:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Authentication required"
            )
            
        role = current_user.get("role", "").strip()
        
        has_all = all(has_permission(role, perm) for perm in permissions)
        
        if not has_all:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=f"Insufficient permissions. Required all of: {permissions}"
            )
            
        return current_user
    
    return checker
