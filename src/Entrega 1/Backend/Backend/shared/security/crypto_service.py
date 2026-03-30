from __future__ import annotations

import base64
import json
import os
from typing import Any

from cryptography.fernet import Fernet, InvalidToken, MultiFernet


class CryptoConfigError(RuntimeError):
    """Raised when encryption keys are missing or invalid."""


class CryptoService:
    """Application-level authenticated encryption service."""

    def __init__(self, key_ring: list[str]) -> None:
        if not key_ring:
            raise CryptoConfigError("No encryption keys configured.")

        fernet_instances: list[Fernet] = []

        for raw_key in key_ring:
            key = raw_key.strip().encode("utf-8")
            try:
                # Validate key format by instantiating Fernet
                fernet_instances.append(Fernet(key))
            except Exception as exc:
                raise CryptoConfigError("Invalid Fernet key provided.") from exc

        self._crypto = MultiFernet(fernet_instances)

    @classmethod
    def from_env(cls) -> "CryptoService":
        """
        Read keys from env.
        Format:
        APP_FERNET_KEYS=key_new,key_old_1,key_old_2
        """
        raw_value = os.getenv("APP_FERNET_KEYS", "").strip()
        if not raw_value:
            raise CryptoConfigError("APP_FERNET_KEYS is not configured.")

        key_ring = [item.strip() for item in raw_value.split(",") if item.strip()]
        return cls(key_ring=key_ring)

    def encrypt_text(self, value: str) -> str:
        """Encrypt a text string and return base64-encoded token."""
        token = self._crypto.encrypt(value.encode("utf-8"))
        return token.decode("utf-8")

    def decrypt_text(self, token: str) -> str:
        """Decrypt a base64-encoded token and return original text."""
        try:
            data = self._crypto.decrypt(token.encode("utf-8"))
            return data.decode("utf-8")
        except InvalidToken as exc:
            raise ValueError("Encrypted token is invalid or key does not match.") from exc

    def encrypt_json(self, value: dict[str, Any]) -> str:
        """Encrypt a JSON dictionary and return base64-encoded token."""
        payload = json.dumps(value, ensure_ascii=False, separators=(",", ":"))
        return self.encrypt_text(payload)

    def decrypt_json(self, token: str) -> dict[str, Any]:
        """Decrypt a token and return JSON dictionary."""
        raw = self.decrypt_text(token)
        return json.loads(raw)
