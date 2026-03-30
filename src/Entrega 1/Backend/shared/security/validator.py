"""Security configuration validator for service startup."""

import sys
from .crypto_service import CryptoService, CryptoConfigError


def validate_crypto_config() -> None:
    """
    Validate encryption configuration during service startup.
    
    Raises:
        SystemExit: If crypto configuration is invalid
    """
    try:
        crypto_service = CryptoService.from_env()
        # Test encryption/decryption to ensure everything works
        test_data = "validation_test_123"
        encrypted = crypto_service.encrypt_text(test_data)
        decrypted = crypto_service.decrypt_text(encrypted)
        
        if decrypted != test_data:
            raise CryptoConfigError("Encryption validation failed")
            
        print("✅ Encryption configuration validated successfully")
        
    except CryptoConfigError as e:
        print(f"❌ Security configuration error: {e}")
        print("Please check your APP_FERNET_KEYS environment variable")
        print("Generate a new key with: python shared/security/generate_key.py")
        sys.exit(1)
    except Exception as e:
        print(f"❌ Unexpected security validation error: {e}")
        sys.exit(1)
