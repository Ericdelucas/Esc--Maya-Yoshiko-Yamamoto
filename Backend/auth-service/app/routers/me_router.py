from fastapi import APIRouter, Depends

from app.core.dependencies import get_current_user
from app.models.schemas.user_schema import UserOut, UserMeResponse

router = APIRouter(prefix="/auth", tags=["auth"])


@router.get("/me", response_model=UserMeResponse)
def me(current_user: UserOut = Depends(get_current_user)) -> UserMeResponse:
    """Retorna dados completos do usuário logado incluindo nome"""
    
    # 🔥 **PEGAR NOME REAL DO USUÁRIO DINAMICAMENTE**
    # Extrair nome do email (parte antes do @)
    email_username = current_user.email.split("@")[0]
    
    return UserMeResponse(
        id=current_user.id,
        email=current_user.email,
        full_name=email_username,  # Nome dinâmico baseado no login
        role=current_user.role,
        profile_photo_url=None
    )
