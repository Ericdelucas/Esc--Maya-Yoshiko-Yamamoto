import time
from dataclasses import dataclass

import jwt


@dataclass(frozen=True)
class JwtService:
    secret: str
    issuer: str = "smartsaude-auth"
    expires_minutes: int = 120

    def sign(self, user_id: int, email: str) -> str:
        now = int(time.time())
        payload = {
            "iss": self.issuer,
            "sub": str(user_id),
            "email": email,
            "iat": now,
            "exp": now + (self.expires_minutes * 60),
        }
        return jwt.encode(payload, self.secret, algorithm="HS256")

    def verify(self, token: str) -> dict:
        return jwt.decode(
            token,
            self.secret,
            algorithms=["HS256"],
            issuer=self.issuer,
            options={"require": ["exp", "iat", "iss", "sub"]},
        )
