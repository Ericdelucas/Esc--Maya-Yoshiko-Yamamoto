"""Unit tests for SmartSaúde AI encryption service."""

import os
import pytest
from unittest.mock import patch

from shared.security.crypto_service import CryptoService, CryptoConfigError
from shared.security.utils import (
    encrypt_sensitive_text, 
    decrypt_sensitive_text,
    encrypt_sensitive_json,
    decrypt_sensitive_json,
    encrypt_medical_notes,
    decrypt_medical_notes
)
from shared.security.key_rotation import KeyRotationManager


class TestCryptoService:
    """Test cases for CryptoService."""
    
    def test_create_service_with_valid_key(self):
        """Test creating service with valid Fernet key."""
        key = "hPThbQLqXC3VrbwLDvKCFP1EGOTGAxEkhpsFZzFAne0="
        service = CryptoService([key])
        assert service is not None
    
    def test_create_service_with_invalid_key(self):
        """Test creating service with invalid key raises error."""
        invalid_key = "invalid_key_format"
        with pytest.raises(CryptoConfigError):
            CryptoService([invalid_key])
    
    def test_create_service_with_empty_keys(self):
        """Test creating service with no keys raises error."""
        with pytest.raises(CryptoConfigError):
            CryptoService([])
    
    def test_encrypt_decrypt_text(self):
        """Test text encryption and decryption."""
        key = "hPThbQLqXC3VrbwLDvKCFP1EGOTGAxEkhpsFZzFAne0="
        service = CryptoService([key])
        
        original_text = "Patient has hypertension and diabetes type 2."
        encrypted = service.encrypt_text(original_text)
        decrypted = service.decrypt_text(encrypted)
        
        assert encrypted != original_text
        assert decrypted == original_text
    
    def test_encrypt_decrypt_json(self):
        """Test JSON encryption and decryption."""
        key = "hPThbQLqXC3VrbwLDvKCFP1EGOTGAxEkhpsFZzFAne0="
        service = CryptoService([key])
        
        original_data = {
            "patient_id": 123,
            "diagnosis": "Hypertension",
            "medications": ["Lisinopril", "Metformin"],
            "notes": "Patient responding well to treatment"
        }
        
        encrypted = service.encrypt_json(original_data)
        decrypted = service.decrypt_json(encrypted)
        
        assert encrypted != str(original_data)
        assert decrypted == original_data
    
    def test_decrypt_with_invalid_token(self):
        """Test decryption with invalid token raises error."""
        key = "hPThbQLqXC3VrbwLDvKCFP1EGOTGAxEkhpsFZzFAne0="
        service = CryptoService([key])
        
        with pytest.raises(ValueError):
            service.decrypt_text("invalid_token_123")
    
    def test_from_env_missing_config(self):
        """Test from_env with missing environment variable."""
        with patch.dict(os.environ, {}, clear=True):
            with pytest.raises(CryptoConfigError):
                CryptoService.from_env()
    
    def test_from_env_valid_config(self):
        """Test from_env with valid environment variable."""
        key = "hPThbQLqXC3VrbwLDvKCFP1EGOTGAxEkhpsFZzFAne0="
        with patch.dict(os.environ, {"APP_FERNET_KEYS": key}):
            service = CryptoService.from_env()
            assert service is not None


class TestEncryptionUtilities:
    """Test cases for encryption utility functions."""
    
    @patch.dict(os.environ, {"APP_FERNET_KEYS": "hPThbQLqXC3VrbwLDvKCFP1EGOTGAxEkhpsFZzFAne0="})
    def test_encrypt_decrypt_sensitive_text(self):
        """Test utility functions for text encryption."""
        original = "Sensitive medical information"
        
        encrypted = encrypt_sensitive_text(original)
        decrypted = decrypt_sensitive_text(encrypted)
        
        assert encrypted != original
        assert decrypted == original
    
    @patch.dict(os.environ, {"APP_FERNET_KEYS": "hPThbQLqXC3VrbwLDvKCFP1EGOTGAxEkhpsFZzFAne0="})
    def test_encrypt_decrypt_sensitive_json(self):
        """Test utility functions for JSON encryption."""
        original = {
            "patient_data": "confidential",
            "test_results": [1, 2, 3, 4, 5]
        }
        
        encrypted = encrypt_sensitive_json(original)
        decrypted = decrypt_sensitive_json(encrypted)
        
        assert encrypted != str(original)
        assert decrypted == original
    
    @patch.dict(os.environ, {"APP_FERNET_KEYS": "hPThbQLqXC3VrbwLDvKCFP1EGOTGAxEkhpsFZzFAne0="})
    def test_medical_notes_encryption(self):
        """Test medical notes specific encryption."""
        # Test with valid notes
        original_notes = "Patient shows improvement in mobility exercises."
        encrypted = encrypt_medical_notes(original_notes)
        decrypted = decrypt_medical_notes(encrypted)
        
        assert encrypted != original_notes
        assert decrypted == original_notes
        
        # Test with empty notes
        assert encrypt_medical_notes("") == ""
        assert decrypt_medical_notes("") == ""
        
        # Test with whitespace only
        assert encrypt_medical_notes("   ") == "   "
        assert decrypt_medical_notes("   ") == "   "


class TestKeyRotation:
    """Test cases for key rotation functionality."""
    
    def test_generate_new_key(self):
        """Test new key generation."""
        manager = KeyRotationManager(CryptoService(["hPThbQLqXC3VrbwLDvKCFP1EGOTGAxEkhpsFZzFAne0="]))
        new_key = manager.generate_new_key()
        
        assert isinstance(new_key, str)
        assert len(new_key) == 44  # Fernet keys are 44 base64 characters
        
        # Test that the key is valid
        from cryptography.fernet import Fernet
        Fernet(new_key.encode())  # Should not raise exception
    
    @patch.dict(os.environ, {"APP_FERNET_KEYS": "hPThbQLqXC3VrbwLDvKCFP1EGOTGAxEkhpsFZzFAne0="})
    def test_key_rotation_compatibility(self):
        """Test key rotation compatibility check."""
        manager = KeyRotationManager.from_env()
        new_key = manager.generate_new_key()
        
        result = manager.test_rotation_compatibility(new_key)
        
        assert result["success"] is True
        assert "new_config" in result
        assert new_key in result["new_config"]
    
    @patch.dict(os.environ, {"APP_FERNET_KEYS": "hPThbQLqXC3VrbwLDvKCFP1EGOTGAxEkhpsFZzFAne0="})
    def test_prepare_rotation_config(self):
        """Test preparation of rotation configuration."""
        manager = KeyRotationManager.from_env()
        new_key = "gUThbQLqXC3VrbwLDvKCFP1EGOTGAxEkhpsFZzFAne1="
        
        new_config = manager.prepare_rotation_config(new_key)
        
        assert new_config.startswith(new_key + ",")
        assert "hPThbQLqXC3VrbwLDvKCFP1EGOTGAxEkhpsFZzFAne0=" in new_config
    
    def test_rotation_with_multiple_keys(self):
        """Test encryption/decryption with multiple keys."""
        from cryptography.fernet import Fernet
        
        # Generate multiple keys
        key1 = Fernet.generate_key().decode()
        key2 = Fernet.generate_key().decode()
        key3 = Fernet.generate_key().decode()
        
        # Create service with multiple keys (newest first)
        service = CryptoService([key3, key2, key1])
        
        test_data = "Multi-key test data"
        
        # Encrypt with current (newest) key
        encrypted = service.encrypt_text(test_data)
        decrypted = service.decrypt_text(encrypted)
        assert decrypted == test_data
        
        # Test that we can decrypt data encrypted with older keys
        old_service = CryptoService([key1])
        old_encrypted = old_service.encrypt_text(test_data)
        
        # New service should still decrypt old data
        assert service.decrypt_text(old_encrypted) == test_data


class TestSecurityValidation:
    """Test cases for security validation."""
    
    def test_validator_with_valid_config(self):
        """Test validator with valid configuration."""
        with patch.dict(os.environ, {"APP_FERNET_KEYS": "hPThbQLqXC3VrbwLDvKCFP1EGOTGAxEkhpsFZzFAne0="}):
            # Should not raise exception
            from shared.security.validator import validate_crypto_config
            validate_crypto_config()
    
    def test_validator_with_invalid_config(self):
        """Test validator with invalid configuration."""
        with patch.dict(os.environ, {}, clear=True):
            from shared.security.validator import validate_crypto_config
            with pytest.raises(SystemExit):
                validate_crypto_config()


if __name__ == "__main__":
    # Run tests if executed directly
    pytest.main([__file__, "-v"])
