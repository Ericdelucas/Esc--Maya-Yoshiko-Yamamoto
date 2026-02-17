import base64
import hashlib
import hmac
import os
from dataclasses import dataclass


@dataclass(frozen=True)
class PasswordHasher:
	pepper: str
	iterations: int = 120_000
	dklen: int = 32

	def hash_password(self, password: str) -> str:
		salt = os.urandom(16)
		key = hashlib.pbkdf2_hmac(
			"sha256",
			(password + self.pepper).encode("utf-8"),
			salt,
			self.iterations,
			dklen=self.dklen,
		)
		return f"{_b64(salt)}.{_b64(key)}"

	def verify_password(self, password: str, stored: str) -> bool:
		salt_b64, key_b64 = _split2(stored)
		salt = _b64d(salt_b64)
		key = _b64d(key_b64)
		test = hashlib.pbkdf2_hmac(
			"sha256",
			(password + self.pepper).encode("utf-8"),
			salt,
			self.iterations,
			dklen=len(key),
		)
		return hmac.compare_digest(test, key)


def _b64(b: bytes) -> str:
	return base64.urlsafe_b64encode(b).decode("utf-8").rstrip("=")


def _b64d(s: str) -> bytes:
	pad = "=" * (-len(s) % 4)
	return base64.urlsafe_b64decode((s + pad).encode("utf-8"))


def _split2(s: str) -> tuple[str, str]:
	if "." not in s:
		raise ValueError("invalid stored hash")
	a, b = s.split(".", 1)
	return a, b


from datetime import datetime, timedelta, timezone
from typing import Any, Dict

import jwt


def create_access_token(
    subject: str,
    email: str,
    secret_key: str,
    issuer: str,
    expires_minutes: int = 120,
    algorithm: str = "HS256",
) -> str:
    now = datetime.now(timezone.utc)
    payload: Dict[str, Any] = {
        "iss": issuer,
        "sub": str(subject),
        "email": email,
        "iat": int(now.timestamp()),
        "exp": int((now + timedelta(minutes=expires_minutes)).timestamp()),
    }
    return jwt.encode(payload, secret_key, algorithm=algorithm)


def decode_access_token(
    token: str,
    secret_key: str,
    issuer: str,
    algorithms: tuple[str, ...] = ("HS256",),
) -> Dict[str, Any]:
    return jwt.decode(
        token,
        secret_key,
        algorithms=list(algorithms),
        issuer=issuer,
        options={"require": ["exp", "iat", "sub", "iss"]},
    )
