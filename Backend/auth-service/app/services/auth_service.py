from dataclasses import dataclass

from app.core.exceptions import BadRequest, Conflict, Unauthorized
from app.core.jwt_service import JwtService
from app.core.security import PasswordHasher
from app.storage.database.user_repository import UserRepository


@dataclass(frozen=True)
class AuthService:
    users: UserRepository
    hasher: PasswordHasher
    jwt: JwtService

    def register(self, email: str, password: str) -> int:
        if not email or not password:
            raise BadRequest("email and password required")

        password_hash = self.hasher.hash_password(password)
        try:
            user = self.users.create(email=email.lower(), password_hash=password_hash)
        except ValueError:
            raise Conflict("email already exists")
        return user.id

    def login(self, email: str, password: str) -> str:
        if not email or not password:
            raise BadRequest("email and password required")

        user = self.users.get_by_email(email.lower())
        if not user or not self.hasher.verify_password(password, user.password_hash):
            raise Unauthorized("invalid credentials")

        return self.jwt.sign(user_id=user.id, email=user.email)

    def verify(self, token: str) -> dict:
        if not token:
            raise Unauthorized("missing token")
        try:
            return self.jwt.verify(token)
        except Exception:
            raise Unauthorized("invalid token")
