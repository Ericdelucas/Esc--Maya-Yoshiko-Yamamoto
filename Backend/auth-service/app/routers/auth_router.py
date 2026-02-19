from fastapi import APIRouter, Depends, Header

from app.core.dependencies import get_auth_service
from app.core.exceptions import Unauthorized
from app.models.schemas.user_schema import TokenOut, UserCreateIn, UserLoginIn
from app.services.auth_service import AuthService

router = APIRouter(prefix="/auth")


@router.get("/health")
def health() -> dict:
    return {"status": "ok", "service": "auth-service"}


@router.post("/register")
def register(payload: UserCreateIn, svc: AuthService = Depends(get_auth_service)) -> dict:
    user_id = svc.register(email=payload.email, password=payload.password)
    return {"user_id": user_id}


@router.post("/login", response_model=TokenOut)
def login(payload: UserLoginIn, svc: AuthService = Depends(get_auth_service)) -> TokenOut:
    token = svc.login(email=payload.email, password=payload.password)
    return TokenOut(token=token)


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
