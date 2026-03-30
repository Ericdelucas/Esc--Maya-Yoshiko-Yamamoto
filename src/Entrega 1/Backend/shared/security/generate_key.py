#!/usr/bin/env python3
"""
Utility to generate Fernet encryption keys for SmartSaúde AI.

Usage:
    python generate_key.py
    
This will generate a new Fernet key that you can add to your .env file:
    APP_FERNET_KEYS=generated_key_here
"""

from cryptography.fernet import Fernet


def generate_key() -> str:
    """Generate a new Fernet encryption key."""
    return Fernet.generate_key().decode()


def main() -> None:
    """Generate and display a new encryption key."""
    key = generate_key()
    print("Generated Fernet key:")
    print(key)
    print("\nAdd this to your .env file:")
    print(f"APP_FERNET_KEYS={key}")
    print("\nFor key rotation, use comma-separated keys (newest first):")
    print(f"APP_FERNET_KEYS={key},old_key_here")


if __name__ == "__main__":
    main()
