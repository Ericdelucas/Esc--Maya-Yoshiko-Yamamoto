from fastapi import APIRouter, Depends

from app.core.dependencies import get_current_user
from app.models.schemas.user_schema import UserOut

router = APIRouter(prefix="/auth", tags=["auth"])


@router.get("/me", response_model=UserOut)
def me(current_user: UserOut = Depends(get_current_user)) -> UserOut:
    return current_user
