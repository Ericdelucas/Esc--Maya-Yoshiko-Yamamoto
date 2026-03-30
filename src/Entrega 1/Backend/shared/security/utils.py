"""Convenience utilities for encryption operations across services."""

from functools import lru_cache
from typing import Any, Dict

from .crypto_service import CryptoService, CryptoConfigError


@lru_cache
def get_crypto_service() -> CryptoService:
    """
    Get cached crypto service instance.
    
    Returns:
        CryptoService: Configured encryption service
        
    Raises:
        CryptoConfigError: If encryption is not properly configured
    """
    return CryptoService.from_env()


def encrypt_sensitive_text(value: str) -> str:
    """
    Encrypt sensitive text data.
    
    Args:
        value: Plain text to encrypt
        
    Returns:
        str: Encrypted base64 token
        
    Raises:
        CryptoConfigError: If encryption is not configured
    """
    return get_crypto_service().encrypt_text(value)


def decrypt_sensitive_text(token: str) -> str:
    """
    Decrypt sensitive text data.
    
    Args:
        token: Encrypted base64 token
        
    Returns:
        str: Decrypted plain text
        
    Raises:
        CryptoConfigError: If encryption is not configured
        ValueError: If token is invalid or key doesn't match
    """
    return get_crypto_service().decrypt_text(token)


def encrypt_sensitive_json(data: Dict[str, Any]) -> str:
    """
    Encrypt sensitive JSON data.
    
    Args:
        data: Dictionary to encrypt
        
    Returns:
        str: Encrypted base64 token
        
    Raises:
        CryptoConfigError: If encryption is not configured
    """
    return get_crypto_service().encrypt_json(data)


def decrypt_sensitive_json(token: str) -> Dict[str, Any]:
    """
    Decrypt sensitive JSON data.
    
    Args:
        token: Encrypted base64 token
        
    Returns:
        Dict[str, Any]: Decrypted dictionary
        
    Raises:
        CryptoConfigError: If encryption is not configured
        ValueError: If token is invalid or key doesn't match
    """
    return get_crypto_service().decrypt_json(token)


# Field-level encryption helpers for common use cases
def encrypt_medical_notes(notes: str) -> str:
    """Encrypt medical notes with proper validation."""
    if not notes or not notes.strip():
        return notes  # Return empty/whitespace as-is
    return encrypt_sensitive_text(notes.strip())


def decrypt_medical_notes(encrypted_notes: str) -> str:
    """Decrypt medical notes with proper validation."""
    if not encrypted_notes or not encrypted_notes.strip():
        return encrypted_notes  # Return empty/whitespace as-is
    return decrypt_sensitive_text(encrypted_notes)


def encrypt_consent_data(consent_data: Dict[str, Any]) -> str:
    """Encrypt LGPD consent data."""
    return encrypt_sensitive_json(consent_data)


def decrypt_consent_data(encrypted_consent: str) -> Dict[str, Any]:
    """Decrypt LGPD consent data."""
    return decrypt_sensitive_json(encrypted_consent)
