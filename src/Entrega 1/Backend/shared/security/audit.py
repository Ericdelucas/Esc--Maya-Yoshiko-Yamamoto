"""Security audit utilities for SmartSaúde AI encryption."""

import os
import sys
from typing import Dict, List, Any, Optional
from datetime import datetime

from .crypto_service import CryptoService, CryptoConfigError
from .validator import validate_crypto_config


class SecurityAuditor:
    """Audits security configuration and implementation."""
    
    def __init__(self) -> None:
        self.audit_results: List[Dict[str, Any]] = []
    
    def add_result(self, category: str, check: str, status: str, message: str, details: Dict[str, Any] = None) -> None:
        """Add an audit result."""
        result = {
            "timestamp": datetime.utcnow().isoformat(),
            "category": category,
            "check": check,
            "status": status,  # "PASS", "FAIL", "WARN"
            "message": message,
            "details": details or {}
        }
        self.audit_results.append(result)
    
    def audit_encryption_configuration(self) -> None:
        """Audit encryption configuration."""
        try:
            # Check if environment variable is set
            keys_env = os.getenv("APP_FERNET_KEYS", "")
            if not keys_env.strip():
                self.add_result(
                    category="Configuration",
                    check="Environment Variable",
                    status="FAIL",
                    message="APP_FERNET_KEYS not set or empty"
                )
                return
            
            # Check key format
            keys = [k.strip() for k in keys_env.split(",") if k.strip()]
            if not keys:
                self.add_result(
                    category="Configuration", 
                    check="Key Format",
                    status="FAIL",
                    message="No valid keys found in APP_FERNET_KEYS"
                )
                return
            
            # Validate each key
            valid_keys = 0
            for i, key in enumerate(keys):
                try:
                    CryptoService([key])
                    valid_keys += 1
                except CryptoConfigError:
                    self.add_result(
                        category="Configuration",
                        check=f"Key {i+1} Validation",
                        status="FAIL",
                        message=f"Invalid Fernet key format at position {i+1}"
                    )
            
            if valid_keys == len(keys):
                self.add_result(
                    category="Configuration",
                    check="Key Validation",
                    status="PASS",
                    message=f"All {len(keys)} keys are valid Fernet keys",
                    details={"key_count": len(keys)}
                )
            
            # Check for key rotation (multiple keys)
            if len(keys) == 1:
                self.add_result(
                    category="Configuration",
                    check="Key Rotation",
                    status="WARN",
                    message="Only one encryption key configured - consider key rotation",
                    details={"recommendation": "Add old keys during rotation: NEW_KEY,OLD_KEY"}
                )
            else:
                self.add_result(
                    category="Configuration",
                    check="Key Rotation",
                    status="PASS",
                    message=f"Key rotation configured with {len(keys)} keys",
                    details={"key_count": len(keys)}
                )
                
        except Exception as e:
            self.add_result(
                category="Configuration",
                check="Environment Variable",
                status="FAIL",
                message=f"Error checking configuration: {str(e)}"
            )
    
    def audit_encryption_functionality(self) -> None:
        """Audit encryption/decryption functionality."""
        try:
            # Test basic encryption
            service = CryptoService.from_env()
            test_data = "Audit test data - patient information"
            
            # Test text encryption
            encrypted = service.encrypt_text(test_data)
            decrypted = service.decrypt_text(encrypted)
            
            if decrypted == test_data:
                self.add_result(
                    category="Functionality",
                    check="Text Encryption/Decryption",
                    status="PASS",
                    message="Text encryption and decryption working correctly"
                )
            else:
                self.add_result(
                    category="Functionality",
                    check="Text Encryption/Decryption",
                    status="FAIL",
                    message="Text encryption/decryption failed - data mismatch"
                )
            
            # Test JSON encryption
            test_json = {"patient_id": 123, "notes": "Test medical notes"}
            encrypted_json = service.encrypt_json(test_json)
            decrypted_json = service.decrypt_json(encrypted_json)
            
            if decrypted_json == test_json:
                self.add_result(
                    category="Functionality",
                    check="JSON Encryption/Decryption",
                    status="PASS",
                    message="JSON encryption and decryption working correctly"
                )
            else:
                self.add_result(
                    category="Functionality",
                    check="JSON Encryption/Decryption",
                    status="FAIL",
                    message="JSON encryption/decryption failed - data mismatch"
                )
                
        except Exception as e:
            self.add_result(
                category="Functionality",
                check="Encryption Operations",
                status="FAIL",
                message=f"Encryption functionality error: {str(e)}"
            )
    
    def audit_key_rotation(self) -> None:
        """Audit key rotation capabilities."""
        try:
            from .key_rotation import KeyRotationManager
            
            manager = KeyRotationManager.from_env()
            current_keys = manager.get_current_keys()
            
            if len(current_keys) < 2:
                self.add_result(
                    category="Key Rotation",
                    check="Multiple Keys",
                    status="WARN",
                    message="Key rotation not configured (only one key)",
                    details={"current_key_count": len(current_keys)}
                )
                return
            
            # Test key rotation compatibility
            new_key = manager.generate_new_key()
            test_result = manager.test_rotation_compatibility(new_key)
            
            if test_result["success"]:
                self.add_result(
                    category="Key Rotation",
                    check="Rotation Compatibility",
                    status="PASS",
                    message="Key rotation compatibility test passed",
                    details=test_result
                )
            else:
                self.add_result(
                    category="Key Rotation",
                    check="Rotation Compatibility",
                    status="FAIL",
                    message=f"Key rotation test failed: {test_result['message']}",
                    details=test_result
                )
                
        except Exception as e:
            self.add_result(
                category="Key Rotation",
                check="Rotation Test",
                status="FAIL",
                message=f"Key rotation audit error: {str(e)}"
            )
    
    def audit_database_schema(self) -> None:
        """Audit database schema for encrypted fields."""
        # This would typically check the actual database
        # For now, we'll check if the schema files contain encrypted fields
        
        schema_file = "/home/eric-de-lucas/Documentos/GitHub/FECAP/PI/PI_3/Esc--Maya-Yoshiko-Yamamoto/Backend/database/init.sql"
        
        try:
            with open(schema_file, 'r') as f:
                schema_content = f.read()
            
            required_encrypted_fields = [
                "notes_encrypted LONGTEXT",
                "consent_data_encrypted LONGTEXT"
            ]
            
            missing_fields = []
            for field in required_encrypted_fields:
                if field not in schema_content:
                    missing_fields.append(field)
            
            if not missing_fields:
                self.add_result(
                    category="Database Schema",
                    check="Encrypted Fields",
                    status="PASS",
                    message="All required encrypted fields are present in schema"
                )
            else:
                self.add_result(
                    category="Database Schema",
                    check="Encrypted Fields",
                    status="FAIL",
                    message=f"Missing encrypted fields: {', '.join(missing_fields)}",
                    details={"missing_fields": missing_fields}
                )
                
        except FileNotFoundError:
            self.add_result(
                category="Database Schema",
                check="Schema File",
                status="WARN",
                message="Database schema file not found for audit"
            )
        except Exception as e:
            self.add_result(
                category="Database Schema",
                check="Schema Audit",
                status="FAIL",
                message=f"Schema audit error: {str(e)}"
            )
    
    def audit_service_integration(self) -> None:
        """Audit service integration with encryption."""
        services_to_check = [
            "/home/eric-de-lucas/Documentos/GitHub/FECAP/PI/PI_3/Esc--Maya-Yoshiko-Yamamoto/Backend/ehr-service/main.py"
        ]
        
        for service_file in services_to_check:
            try:
                with open(service_file, 'r') as f:
                    content = f.read()
                
                if "validate_crypto_config" in content:
                    self.add_result(
                        category="Service Integration",
                        check=f"Security Validation - {os.path.basename(service_file)}",
                        status="PASS",
                        message="Service includes security validation on startup"
                    )
                else:
                    self.add_result(
                        category="Service Integration",
                        check=f"Security Validation - {os.path.basename(service_file)}",
                        status="WARN",
                        message="Service missing security validation on startup"
                    )
                
                if "encrypt_medical_notes" in content or "encrypt_sensitive_text" in content:
                    self.add_result(
                        category="Service Integration",
                        check=f"Encryption Usage - {os.path.basename(service_file)}",
                        status="PASS",
                        message="Service uses encryption utilities"
                    )
                else:
                    self.add_result(
                        category="Service Integration",
                        check=f"Encryption Usage - {os.path.basename(service_file)}",
                        status="WARN",
                        message="Service may not be using encryption"
                    )
                    
            except FileNotFoundError:
                self.add_result(
                    category="Service Integration",
                    check=f"Service File - {os.path.basename(service_file)}",
                    status="WARN",
                    message="Service file not found"
                )
            except Exception as e:
                self.add_result(
                    category="Service Integration",
                    check=f"Service Audit - {os.path.basename(service_file)}",
                    status="FAIL",
                    message=f"Service audit error: {str(e)}"
                )
    
    def run_full_audit(self) -> Dict[str, Any]:
        """Run complete security audit."""
        print("🔐 Starting SmartSaúde AI Security Audit...")
        print("=" * 50)
        
        self.audit_encryption_configuration()
        self.audit_encryption_functionality()
        self.audit_key_rotation()
        self.audit_database_schema()
        self.audit_service_integration()
        
        # Calculate summary
        total_checks = len(self.audit_results)
        passed = len([r for r in self.audit_results if r["status"] == "PASS"])
        failed = len([r for r in self.audit_results if r["status"] == "FAIL"])
        warnings = len([r for r in self.audit_results if r["status"] == "WARN"])
        
        summary = {
            "timestamp": datetime.utcnow().isoformat(),
            "total_checks": total_checks,
            "passed": passed,
            "failed": failed,
            "warnings": warnings,
            "status": "PASS" if failed == 0 else "FAIL" if failed > 0 else "WARN",
            "results": self.audit_results
        }
        
        # Print results
        print(f"\n📊 Audit Summary:")
        print(f"   Total Checks: {total_checks}")
        print(f"   ✅ Passed: {passed}")
        print(f"   ❌ Failed: {failed}")
        print(f"   ⚠️  Warnings: {warnings}")
        print(f"   Overall Status: {summary['status']}")
        
        if failed > 0:
            print(f"\n❌ Failed Checks:")
            for result in self.audit_results:
                if result["status"] == "FAIL":
                    print(f"   • {result['category']}: {result['message']}")
        
        if warnings > 0:
            print(f"\n⚠️  Warnings:")
            for result in self.audit_results:
                if result["status"] == "WARN":
                    print(f"   • {result['category']}: {result['message']}")
        
        return summary


def main():
    """Run security audit from command line."""
    auditor = SecurityAuditor()
    results = auditor.run_full_audit()
    
    # Exit with appropriate code
    if results["failed"] > 0:
        sys.exit(1)
    elif results["warnings"] > 0:
        sys.exit(2)
    else:
        sys.exit(0)


if __name__ == "__main__":
    main()
