from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
import os
from app.models.orm import Base

# Ler DATABASE_URL do ambiente (Render/Supabase) ou usar fallback
DATABASE_URL = os.getenv("DATABASE_URL", os.getenv("DB_URL", "postgresql+psycopg2://user:password@localhost:5432/smartsaude"))

# Debug para garantir URL correta
print(f"DATABASE_URL: {DATABASE_URL}")

# Criar engine com configurações otimizadas para PostgreSQL
engine = create_engine(
    DATABASE_URL, 
    pool_pre_ping=True,
    pool_recycle=3600,  # Reciclar conexões a cada hora
    echo=False  # Mudar para True para debug SQL se necessário
)

# Debug: Mostrar todas as tabelas que serão criadas
print("Tabelas a serem criadas:")
for table_name in Base.metadata.tables.keys():
    print(f"  - {table_name}")

# Criar tabelas automaticamente se não existirem
try:
    Base.metadata.create_all(bind=engine)
    print("✅ Tabelas criadas com sucesso!")
except Exception as e:
    print(f"❌ Erro ao criar tabelas: {e}")
    raise

SessionLocal = sessionmaker(
    autocommit=False,
    autoflush=False,
    bind=engine,
)


def get_session():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


# Conexão será testada no primeiro uso
