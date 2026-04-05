from fastapi import APIRouter, Depends, Header, HTTPException, status
from fastapi import UploadFile, File

from app.core.dependencies import get_auth_service
from app.core.exceptions import Unauthorized
from app.models.schemas.user_schema import TokenOut, UserCreateIn, UserLoginIn
from app.models.schemas.user_me_response import UserMeResponse
from app.models.schemas.change_password_request import ChangePasswordRequest
from app.models.schemas.profile_photo_response import ProfilePhotoResponse
from app.services.auth_service import AuthService

router = APIRouter(prefix="/auth")


@router.get("/health")
def health() -> dict:
    return {"status": "ok", "service": "auth-service"}


@router.post("/register")
def register(payload: UserCreateIn, svc: AuthService = Depends(get_auth_service)) -> dict:
    user_id = svc.register(email=payload.email, password=payload.password, role=payload.role)
    return {"user_id": user_id}


@router.post("/login", response_model=TokenOut)
def login(payload: UserLoginIn, svc: AuthService = Depends(get_auth_service)) -> TokenOut:
    login_data = svc.login(email=payload.email, password=payload.password)
    return TokenOut(**login_data)


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
