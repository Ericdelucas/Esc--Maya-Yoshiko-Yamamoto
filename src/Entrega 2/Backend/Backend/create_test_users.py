#!/usr/bin/env python3
"""
Script para criar usuários de teste no banco de dados
"""
import pymysql
import hashlib
import secrets

# Configuração do banco de dados (via Docker)
DB_CONFIG = {
    'host': 'localhost',
    'user': 'smartuser',
    'password': 'smartpass',
    'database': 'smartsaude',
    'port': 3306
}

def hash_password(password: str) -> str:
    """Gera hash da senha usando SHA-256 com salt"""
    salt = secrets.token_hex(16)
    password_hash = hashlib.sha256((password + salt).encode()).hexdigest()
    return f"{salt}:{password_hash}"

def create_test_users():
    """Cria usuários de teste"""
    usuarios = [
        {
            'email': 'admin@smartsaude.com',
            'password': 'admin123',
            'role': 'admin',
            'full_name': 'Administrador SmartSaúde'
        },
        {
            'email': 'profissional@smartsaude.com',
            'password': 'prof123',
            'role': 'professional',
            'full_name': 'Dr. João Silva'
        },
        {
            'email': 'paciente@smartsaude.com',
            'password': 'pac123',
            'role': 'patient',
            'full_name': 'Maria Paciente'
        },
        {
            'email': 'teste@teste.com',
            'password': 'teste123',
            'role': 'professional',
            'full_name': 'Profissional Teste'
        }
    ]
    
    try:
        connection = pymysql.connect(**DB_CONFIG)
        cursor = connection.cursor()
        
        for usuario in usuarios:
            email = usuario['email']
            password_hash = hash_password(usuario['password'])
            role = usuario['role']
            full_name = usuario['full_name']
            
            # Verificar se usuário já existe
            cursor.execute("SELECT id FROM users WHERE email = %s", (email,))
            if cursor.fetchone():
                print(f"Usuário {email} já existe. Atualizando senha...")
                cursor.execute(
                    "UPDATE users SET password_hash = %s, role = %s, full_name = %s WHERE email = %s",
                    (password_hash, role, full_name, email)
                )
            else:
                print(f"Criando usuário {email}...")
                cursor.execute(
                    "INSERT INTO users (email, password_hash, role, full_name) VALUES (%s, %s, %s, %s)",
                    (email, password_hash, role, full_name)
                )
        
        connection.commit()
        print("\n✅ Usuários criados/atualizados com sucesso!")
        
        print("\n📋 Credenciais para acesso:")
        print("=" * 50)
        for usuario in usuarios:
            print(f"📧 Email: {usuario['email']}")
            print(f"🔑 Senha: {usuario['password']}")
            print(f"👤 Função: {usuario['role']}")
            print(f"📝 Nome: {usuario['full_name']}")
            print("-" * 30)
        
        print(f"\n🌐 Endpoint de login: POST http://localhost:8080/auth/login")
        print("📄 Exemplo de requisição:")
        print("""{
  "email": "admin@smartsaude.com",
  "password": "admin123"
}""")
        
    except Exception as e:
        print(f"❌ Erro: {e}")
    finally:
        if 'connection' in locals():
            connection.close()

if __name__ == "__main__":
    create_test_users()
