from fastapi import APIRouter, Depends, Header, HTTPException, status, Request
from fastapi import UploadFile, File

from app.core.dependencies import get_auth_service
from app.core.exceptions import Unauthorized
from app.models.schemas.user_schema import TokenOut, UserCreateIn, UserLoginIn
from app.models.schemas.user_me_response import UserMeResponse
from app.models.schemas.change_password_request import ChangePasswordRequest
from app.models.schemas.profile_photo_response import ProfilePhotoResponse
from app.services.auth_service import AuthService

# Forçar ativação do rate limiting
from app.services.rate_limiter import rate_limiter
RATE_LIMITING_ENABLED = True
print("Rate limiting FORÇADO habilitado!")

router = APIRouter(prefix="/auth")


@router.get("/health")
def health() -> dict:
    return {"status": "ok", "service": "auth-service"}


@router.post("/register")
def register(payload: UserCreateIn, svc: AuthService = Depends(get_auth_service)) -> dict:
    user_id = svc.register(email=payload.email, password=payload.password, role=payload.role)
    return {"user_id": user_id}


@router.post("/login")
def login(payload: UserLoginIn, request: Request, svc: AuthService = Depends(get_auth_service)):
    """
    Endpoint de login com rate limiting
    - Máximo de 10 tentativas falhas
    - Bloqueio de 5 minutos após exceder limite
    """
    # Se rate limiting não estiver disponível, fazer login normal
    if not RATE_LIMITING_ENABLED or rate_limiter is None:
        try:
            login_data = svc.login(email=payload.email, password=payload.password)
            return TokenOut(**login_data)
        except Unauthorized:
            raise HTTPException(
                status_code=401,
                detail={"error": "invalid_credentials", "message": "Credenciais inválidas"}
            )
    
    # Com rate limiting ativado
    try:
        # Verificar rate limiting por email
        rate_status = rate_limiter.record_attempt(payload.email, success=False)
        
        if not rate_status.get("allowed", True):
            # Usuário está bloqueado
            raise HTTPException(
                status_code=status.HTTP_429_TOO_MANY_REQUESTS,
                detail={
                    "error": "too_many_attempts",
                    "message": rate_status["message"],
                    "retry_after": rate_status["retry_after"],
                    "attempts_used": rate_status.get("attempts_used", 0),
                    "max_attempts": rate_status.get("max_attempts", 10)
                },
                headers={"Retry-After": str(rate_status["retry_after"])}
            )
        
        # Tentar fazer login
        login_data = svc.login(email=payload.email, password=payload.password)
        
        # Login bem-sucedido - registrar sucesso no rate limiter
        rate_limiter.record_attempt(payload.email, success=True)
        
        return TokenOut(**login_data)
        
    except Unauthorized:
        # Login falhou - rate limiter já registrou a tentativa falha acima
        # Retornar mensagem informativa com tentativas restantes
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail={
                "error": "invalid_credentials",
                "message": rate_status.get("message", "Credenciais inválidas"),
                "attempts_remaining": rate_status.get("attempts_remaining", 0),
                "max_attempts": rate_status.get("max_attempts", 10)
            }
        )
    except HTTPException as he:
        # Se já for HTTPException (429), propagar
        if he.status_code == 429:
            raise he
        # Se for outro erro, tratar como Unauthorized
        raise HTTPException(
            status_code=401,
            detail={"error": "invalid_credentials", "message": "Credenciais inválidas"}
        )


@router.get("/login-status/{email}")
def get_login_status(email: str) -> dict:
    """
    Endpoint para verificar status de rate limiting de um email
    Útil para o frontend mostrar quantas tentativas restantes
    """
    if not RATE_LIMITING_ENABLED or rate_limiter is None:
        return {
            "error": "Rate limiting not available",
            "message": "Rate limiting temporariamente desativado",
            "attempts_remaining": 10,
            "max_attempts": 10,
            "blocked": False
        }
    
    return rate_limiter.get_status(email)


@router.get("/verify")
def verify(
    authorization: str | None = Header(default=None),
    svc: AuthService = Depends(get_auth_service),
) -> dict:
    if not authorization or not authorization.startswith("Bearer "):
        raise Unauthorized("missing bearer token")
    token = authorization.replace("Bearer ", "", 1).strip()
    payload = svc.verify(token)
    return {"sub": payload["sub"], "email": payload["email"], "role": payload["role"]}


@router.get("/me", response_model=UserMeResponse)
def get_current_user_profile(
    authorization: str | None = Header(default=None),
    svc: AuthService = Depends(get_auth_service),
) -> UserMeResponse:
    """Retorna dados completos do usuário logado para perfil"""
    if not authorization or not authorization.startswith("Bearer "):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Missing bearer token")
    
    token = authorization.replace("Bearer ", "", 1).strip()
    payload = svc.verify(token)
    
    # Buscar dados completos do usuário no banco
    user_data = svc.get_user_by_id(int(payload["sub"]))
    
    return UserMeResponse(
        id=user_data["id"],
        email=user_data["email"],
        full_name=user_data.get("full_name"),
        role=user_data["role"],
        profile_photo_url=user_data.get("profile_photo_url")
    )


@router.put("/change-password")
def change_password(
    payload: ChangePasswordRequest,
    authorization: str | None = Header(default=None),
    svc: AuthService = Depends(get_auth_service),
) -> dict:
    """Troca de senha segura do usuário logado"""
    if not authorization or not authorization.startswith("Bearer "):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Missing bearer token")
    
    token = authorization.replace("Bearer ", "", 1).strip()
    jwt_payload = svc.verify(token)
    user_id = int(jwt_payload["sub"])
    
    # Validações
    if payload.new_password != payload.confirm_password:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="New passwords do not match")
    
    if payload.current_password == payload.new_password:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="New password must be different from current")
    
    # Executar troca de senha
    svc.change_password(
        user_id=user_id,
        current_password=payload.current_password,
        new_password=payload.new_password
    )
    
    return {"message": "Password updated successfully"}


@router.post("/profile/photo", response_model=ProfilePhotoResponse)
def upload_profile_photo(
    file: UploadFile = File(...),
    authorization: str | None = Header(default=None),
    svc: AuthService = Depends(get_auth_service),
) -> ProfilePhotoResponse:
    """Upload de foto de perfil do usuário logado"""
    if not authorization or not authorization.startswith("Bearer "):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Missing bearer token")
    
    token = authorization.replace("Bearer ", "", 1).strip()
    jwt_payload = svc.verify(token)
    user_id = int(jwt_payload["sub"])
    
    # Validações de arquivo
    if not file.content_type or not file.content_type.startswith("image/"):
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="File must be an image")
    
    # Tipos permitidos
    allowed_types = ["image/jpeg", "image/jpg", "image/png", "image/webp"]
    if file.content_type not in allowed_types:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Unsupported image format")
    
    # Tamanho máximo (5MB)
    if hasattr(file, 'size') and file.size > 5 * 1024 * 1024:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="File too large (max 5MB)")
    
    # Upload e atualização
    photo_url = svc.upload_profile_photo(user_id=user_id, file=file)
    
    return ProfilePhotoResponse(
        message="Profile photo updated successfully",
        profile_photo_url=photo_url
    )


@router.post("/logout")
def logout(
    authorization: str | None = Header(default=None),
    svc: AuthService = Depends(get_auth_service),
) -> dict:
    """Logout simples (padronização futura)"""
    if not authorization or not authorization.startswith("Bearer "):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Missing bearer token")
    
    # Por enquanto, apenas confirmação
    # Futuro: implementar blacklist de tokens
    return {"message": "Logged out successfully"}
