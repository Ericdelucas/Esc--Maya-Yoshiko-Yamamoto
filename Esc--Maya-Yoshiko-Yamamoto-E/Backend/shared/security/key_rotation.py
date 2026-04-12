"""Key rotation utilities for SmartSaúde AI encryption."""

import os
from typing import List, Dict, Any
from .crypto_service import CryptoService
from .utils import encrypt_sensitive_text, decrypt_sensitive_text


class KeyRotationManager:
    """Manages encryption key rotation operations."""
    
    def __init__(self, current_crypto: CryptoService) -> None:
        self.current_crypto = current_crypto
    
    @classmethod
    def from_env(cls) -> "KeyRotationManager":
        """Create rotation manager from environment configuration."""
        return cls(CryptoService.from_env())
    
    def generate_new_key(self) -> str:
        """Generate a new Fernet key."""
        from cryptography.fernet import Fernet
        return Fernet.generate_key().decode()
    
    def get_current_keys(self) -> List[str]:
        """Get current key ring from environment."""
        raw_value = os.getenv("APP_FERNET_KEYS", "").strip()
        if not raw_value:
            return []
        return [item.strip() for item in raw_value.split(",") if item.strip()]
    
    def prepare_rotation_config(self, new_key: str) -> str:
        """
        Prepare new environment configuration for key rotation.
        
        Args:
            new_key: New Fernet key to add
            
        Returns:
            str: New APP_FERNET_KEYS configuration string
        """
        current_keys = self.get_current_keys()
        
        # New key should be first (primary for encryption)
        new_config = [new_key] + current_keys
        
        return ",".join(new_config)
    
    def test_rotation_compatibility(self, new_key: str) -> Dict[str, Any]:
        """
        Test that new key can decrypt data encrypted with current keys.
        
        Args:
            new_key: New Fernet key to test
            
        Returns:
            Dict with test results
        """
        test_data = "rotation_test_data_12345"
        
        try:
            # Encrypt with current configuration
            encrypted_current = encrypt_sensitive_text(test_data)
            
            # Create new crypto service with new key + current keys
            new_config = self.prepare_rotation_config(new_key)
            os.environ["APP_FERNET_KEYS"] = new_config
            new_crypto = CryptoService.from_env()
            
            # Try to decrypt with new configuration
            decrypted = new_crypto.decrypt_text(encrypted_current)
            
            # Test encryption with new key
            encrypted_new = new_crypto.encrypt_text(test_data)
            decrypted_new = new_crypto.decrypt_text(encrypted_new)
            
            success = (decrypted == test_data and decrypted_new == test_data)
            
            return {
                "success": success,
                "message": "Key rotation compatibility test passed" if success else "Test failed",
                "test_data": test_data,
                "current_encrypted": encrypted_current[:50] + "...",
                "new_encrypted": encrypted_new[:50] + "...",
                "new_config": new_config
            }
            
        except Exception as e:
            return {
                "success": False,
                "message": f"Key rotation test failed: {str(e)}",
                "error": str(e)
            }
        finally:
            # Restore original environment
            original_keys = ",".join(self.get_current_keys())
            if original_keys:
                os.environ["APP_FERNET_KEYS"] = original_keys
            else:
                os.environ.pop("APP_FERNET_KEYS", None)
    
    def rotate_key_in_env(self, new_key: str) -> bool:
        """
        Update environment with new key configuration.
        
        Args:
            new_key: New primary key
            
        Returns:
            bool: True if successful
        """
        try:
            # Test compatibility first
            test_result = self.test_rotation_compatibility(new_key)
            if not test_result["success"]:
                print(f"❌ Key rotation failed: {test_result['message']}")
                return False
            
            # Update environment
            new_config = self.prepare_rotation_config(new_key)
            os.environ["APP_FERNET_KEYS"] = new_config
            
            print(f"✅ Key rotation successful")
            print(f"New configuration: {new_config}")
            return True
            
        except Exception as e:
            print(f"❌ Key rotation error: {e}")
            return False


def generate_rotation_instructions() -> None:
    """Generate instructions for performing key rotation."""
    print("🔐 SmartSaúde AI - Key Rotation Instructions")
    print("=" * 50)
    print()
    print("1. Generate a new key:")
    print("   python -c \"from cryptography.fernet import Fernet; print(Fernet.generate_key().decode())\"")
    print()
    print("2. Test the rotation:")
    print("   python -c \"")
    print("   from shared.security.key_rotation import KeyRotationManager")
    print("   manager = KeyRotationManager.from_env()")
    print("   result = manager.test_rotation_compatibility('YOUR_NEW_KEY_HERE')")
    print("   print(result)")
    print("   \"")
    print()
    print("3. Update your .env file:")
    print("   APP_FERNET_KEYS=NEW_KEY,OLD_KEY_1,OLD_KEY_2")
    print()
    print("4. Restart all services to apply the new configuration")
    print()
    print("5. After successful deployment, you can remove old keys:")
    print("   APP_FERNET_KEYS=NEW_KEY")
    print()
    print("⚠️  Always test rotation before applying to production!")
    print("⚠️  Keep backup of old keys until you confirm all data is accessible!")


if __name__ == "__main__":
    generate_rotation_instructions()
