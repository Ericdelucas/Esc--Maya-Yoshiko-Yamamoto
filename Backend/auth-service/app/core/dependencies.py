from functools import lru_cache

from app.core.config import get_settings
from app.core.jwt_service import JwtService
from app.core.security import PasswordHasher
from app.services.auth_service import AuthService
from app.storage.database.user_repository import UserRepository


@lru_cache(maxsize=1)
def get_auth_service() -> AuthService:
    settings = get_settings()

    return AuthService(
        users=UserRepository(),
        hasher=PasswordHasher(pepper=settings.pepper_key),
        jwt=JwtService(secret=settings.jwt_secret),
    )
