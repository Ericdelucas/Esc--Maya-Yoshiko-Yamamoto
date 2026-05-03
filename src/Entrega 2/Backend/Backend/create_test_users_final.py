#!/usr/bin/env python3
"""
Script para criar usuários de teste com senhas compatíveis
Execute: python3 create_test_users_final.py
"""

import mysql.connector
import hashlib
import secrets
import base64
import sys

def hash_password(password: str, pepper: str = "change_me") -> str:
    """Gera hash PBKDF2 compatível com o backend"""
    salt = secrets.token_bytes(16)
    salt_b64 = base64.urlsafe_b64encode(salt).decode('ascii').rstrip('=')
    
    # PBKDF2 com SHA256
    key = hashlib.pbkdf2_hmac('sha256', 
                            (password + pepper).encode('utf-8'), 
                            salt, 
                            100000)
    key_b64 = base64.urlsafe_b64encode(key).decode('ascii').rstrip('=')
    
    return f"{salt_b64}.{key_b64}"

def create_users():
    """Cria usuários de teste no banco"""
    
    # Configuração do banco
    config = {
        'host': 'localhost',
        'port': 3306,
        'user': 'smartuser',
        'password': 'smartpass',
        'database': 'smartsaude'
    }
    
    try:
        # Conectar ao banco
        conn = mysql.connector.connect(**config)
        cursor = conn.cursor()
        
        print("🔌 Conectado ao banco MySQL")
        
        # Limpar usuários existentes
        cursor.execute("DELETE FROM users WHERE email LIKE '%@smartsaude.com'")
        print("🗑️  Usuários antigos removidos")
        
        # Usuários para criar
        users = [
            ('novo.admin@smartsaude.com', 'admin123', 'admin', 'Administrador Teste'),
            ('profissional@smartsaude.com', 'prof123', 'professional', 'Profissional Saúde'),
            ('dr.silva@smartsaude.com', 'prof123', 'doctor', 'Dr. Silva Teste'),
            ('joao.paciente@smartsaude.com', 'pac123', 'patient', 'João Paciente'),
            ('usuario.teste.2026@smartsaude.com', 'teste123', 'patient', 'Usuário Teste'),
        ]
        
        # Inserir usuários
        for email, password, role, name in users:
            password_hash = hash_password(password)
            
            query = """
            INSERT INTO users (email, password_hash, role, full_name, created_at, updated_at)
            VALUES (%s, %s, %s, %s, NOW(), NOW())
            """
            
            cursor.execute(query, (email, password_hash, role, name))
            print(f"✅ Criado: {email} ({role})")
        
        # Commit das alterações
        conn.commit()
        
        # Verificar usuários criados
        cursor.execute("SELECT email, role, full_name FROM users ORDER BY role, email")
        users_created = cursor.fetchall()
        
        print("\n📋 USUÁRIOS CRIADOS:")
        print("=" * 50)
        for email, role, name in users_created:
            print(f"👤 {email}")
            print(f"   Role: {role}")
            print(f"   Nome: {name}")
            print(f"   Senha: {'admin123' if 'admin' in email else 'prof123' if 'prof' in email or 'dr.' in email else 'pac123' if 'joao' in email else 'teste123'}")
            print("-" * 50)
        
        print(f"\n🎯 Total de {len(users_created)} usuários criados com sucesso!")
        
    except mysql.connector.Error as err:
        print(f"❌ Erro no banco: {err}")
        return False
    except Exception as e:
        print(f"❌ Erro: {e}")
        return False
    finally:
        if 'conn' in locals() and conn.is_connected():
            cursor.close()
            conn.close()
            print("🔌 Conexão fechada")
    
    return True

if __name__ == "__main__":
    print("🚀 Criando usuários de teste para SmartSaude...")
    print("=" * 60)
    
    if create_users():
        print("\n✅ SUCESSO! Usuários criados.")
        print("\n📱 Para testar no app:")
        print("   Profissional: profissional@smartsaude.com / prof123")
        print("   Paciente: joao.paciente@smartsaude.com / pac123")
        print("   Admin: novo.admin@smartsaude.com / admin123")
    else:
        print("\n❌ FALHA! Verifique o erro acima.")
        sys.exit(1)
