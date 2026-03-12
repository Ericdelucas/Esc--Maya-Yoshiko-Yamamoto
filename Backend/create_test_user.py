#!/usr/bin/env python3
"""
Script para criar usuário de teste no banco SQLite
"""
import sqlite3
import hashlib
import os

def create_test_user():
    """Cria um usuário de teste para login"""
    
    # Conecta ao banco
    conn = sqlite3.connect('smartsaude.db')
    cursor = conn.cursor()
    
    # Senha: 123456 (hash simples para teste)
    password = "123456"
    password_hash = hashlib.sha256(password.encode()).hexdigest()
    
    # Dados do usuário de teste
    test_users = [
        ('patient@test.com', password_hash, 'Patient'),
        ('professional@test.com', password_hash, 'Professional'),
        ('admin@test.com', password_hash, 'Admin')
    ]
    
    # Insere os usuários
    for email, pwd_hash, role in test_users:
        try:
            cursor.execute('''
                INSERT INTO users (email, password_hash, role)
                VALUES (?, ?, ?)
            ''', (email, pwd_hash, role))
            print(f"Usuario criado: {email} (Role: {role})")
        except sqlite3.IntegrityError:
            print(f"Usuario ja existe: {email}")
    
    conn.commit()
    
    # Verifica usuários criados
    cursor.execute('SELECT email, role, created_at FROM users')
    users = cursor.fetchall()
    
    print("\nUsuarios no banco:")
    print("=" * 50)
    for email, role, created_at in users:
        print(f"Email: {email}")
        print(f"Role: {role}")
        print(f"Created: {created_at}")
        print("-" * 50)
    
    conn.close()
    
    print("\nCredenciais para teste:")
    print("=" * 30)
    print("Email: patient@test.com")
    print("Senha: 123456")
    print("\nEmail: professional@test.com") 
    print("Senha: 123456")
    print("\nEmail: admin@test.com")
    print("Senha: 123456")
    print("=" * 30)

if __name__ == "__main__":
    create_test_user()
