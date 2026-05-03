"""Crypto utilities local to EHR service."""

import os
from cryptography.fernet import Fernet, MultiFernet
from typing import Optional, Dict, Any
import json


class CryptoService:
    """Local crypto service for EHR."""
    
    def __init__(self):
        self._fernet: Optional[MultiFernet] = None
        self._load_keys()
    
    def _load_keys(self) -> None:
        """Load Fernet keys from environment."""
        keys_str = os.getenv("APP_FERNET_KEYS")
        if not keys_str:
            raise ValueError("APP_FERNET_KEYS environment variable not set")
        
        keys = [key.strip() for key in keys_str.split(",") if key.strip()]
        if not keys:
            raise ValueError("No valid Fernet keys found in APP_FERNET_KEYS")
        
        fernet_keys = [Fernet(key.encode()) for key in keys]
        self._fernet = MultiFernet(fernet_keys)
    
    def encrypt(self, data: str) -> str:
        """Encrypt data."""
        if not data:
            return ""
        return self._fernet.encrypt(data.encode()).decode()
    
    def decrypt(self, encrypted_data: str) -> str:
        """Decrypt data."""
        if not encrypted_data:
            return ""
        return self._fernet.decrypt(encrypted_data.encode()).decode()


# Global instance
_crypto_service = CryptoService()


def encrypt_medical_notes(notes: str) -> str:
    """Encrypt medical notes."""
    return _crypto_service.encrypt(notes)


def decrypt_medical_notes(notes_encrypted: str) -> str:
    """Decrypt medical notes."""
    return _crypto_service.decrypt(notes_encrypted)
