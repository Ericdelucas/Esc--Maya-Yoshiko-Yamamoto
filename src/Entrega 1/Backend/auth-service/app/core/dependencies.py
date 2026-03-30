from functools import lru_cache

from fastapi import Depends
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer

from app.core.config import get_settings
from app.core.jwt_service import JwtService
from app.core.security import PasswordHasher, decode_access_token
from app.models.schemas.user_schema import UserOut
from app.services.auth_service import AuthService
from app.storage.database.db import get_session
from app.storage.database.user_repository import UserRepository

bearer = HTTPBearer(auto_error=True)


@lru_cache(maxsize=1)
def get_auth_service() -> AuthService:
    settings = get_settings()

    return AuthService(
        users=UserRepository(),
        hasher=PasswordHasher(pepper=settings.pepper_key),
        jwt=JwtService(secret=settings.jwt_secret),
    )


def get_current_user(
    creds: HTTPAuthorizationCredentials = Depends(bearer),
    session=Depends(get_session),
) -> UserOut:
    settings = get_settings()
    payload = decode_access_token(
        token=creds.credentials,
        secret_key=settings.jwt_secret,
        issuer="smartsaude-auth"
    )
    user_id = int(payload["sub"])

    repo = UserRepository()
    user = repo.get_by_id(user_id, session)

    if user is None:
        raise ValueError("User not found")

    return UserOut(id=user.id, email=user.email, role=user.role)
