#!/usr/bin/env python3
"""
Script para configurar SQLite e iniciar o backend rapidamente
"""
import sqlite3
import os
import sys
from pathlib import Path

def create_sqlite_database():
    """Cria banco de dados SQLite com as tabelas necessárias"""
    db_path = "smartsaude.db"
    
    # Remove banco existente para começar do zero
    if os.path.exists(db_path):
        os.remove(db_path)
        print(f"Banco antigo {db_path} removido")
    
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    # Lê o arquivo init.sql e adapta para SQLite
    init_sql_path = Path("database/init.sql")
    if not init_sql_path.exists():
        print("Arquivo database/init.sql não encontrado!")
        return False
    
    with open(init_sql_path, 'r', encoding='utf-8') as f:
        sql_content = f.read()
    
    # Converte sintaxe MySQL para SQLite
    sqlite_sql = sql_content.replace(
        "INT AUTO_INCREMENT PRIMARY KEY", 
        "INTEGER PRIMARY KEY AUTOINCREMENT"
    ).replace(
        "TINYINT(1)",
        "INTEGER"
    ).replace(
        "LONGTEXT",
        "TEXT"
    ).replace(
        "DATETIME",
        "TEXT"
    )
    
    # Remove índices que causam problemas no SQLite
    lines = sqlite_sql.split('\n')
    cleaned_lines = []
    skip_next = False
    
    for line in lines:
        if line.strip().startswith('INDEX'):
            skip_next = False  # Pula linhas de INDEX
            continue
        if skip_next and line.strip().endswith(');'):
            skip_next = False
            continue
        if skip_next:
            continue
        cleaned_lines.append(line)
    
    sqlite_sql = '\n'.join(cleaned_lines)
    
    try:
        cursor.executescript(sqlite_sql)
        conn.commit()
        print(f"Banco SQLite criado: {db_path}")
        print(f"Tabelas criadas com sucesso")
        return True
    except Exception as e:
        print(f"Erro ao criar tabelas: {e}")
        return False
    finally:
        conn.close()

def update_config_files():
    """Atualiza arquivos de configuração para usar SQLite"""
    services = ['auth-service', 'notification-service', 'ehr-service', 
                'analytics-service', 'exercise-service', 'training-service']
    
    for service in services:
        config_path = Path(f"{service}/app/core/config.py")
        if config_path.exists():
            with open(config_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Atualiza URL do banco para SQLite
            new_content = content.replace(
                'db_url: str = "mysql+pymysql://smartuser:smartpass@localhost:3306/smartsaude"',
                'db_url: str = "sqlite:///smartsaude.db"'
            )
            
            with open(config_path, 'w', encoding='utf-8') as f:
                f.write(new_content)
            
            print(f"Config atualizado: {service}")

def install_dependencies():
    """Instala dependências adicionais para SQLite"""
    import subprocess
    try:
        subprocess.run([sys.executable, "-m", "pip", "install", "aiosqlite"], check=True)
        print("Dependência aiosqlite instalada com sucesso")
    except subprocess.CalledProcessError as e:
        print(f"Erro ao instalar dependências: {e}")

if __name__ == "__main__":
    print("Configurando backend com SQLite...")
    
    # Instala dependência SQLite
    print("\n1. Instalando dependências...")
    install_dependencies()
    
    # Cria banco de dados
    print("\n2. Criando banco de dados...")
    if not create_sqlite_database():
        sys.exit(1)
    
    # Atualiza configurações
    print("\n3. Atualizando arquivos de configuração...")
    update_config_files()
    
    print("\nBackend configurado com sucesso!")
    print("\nPara iniciar os serviços:")
    print("   cd auth-service && python main.py")
    print("   cd notification-service && python main.py")
    print("   cd ehr-service && python main.py")
    print("   cd analytics-service && python main.py")
    print("   cd exercise-service && python main.py")
    print("   cd training-service && python main.py")
