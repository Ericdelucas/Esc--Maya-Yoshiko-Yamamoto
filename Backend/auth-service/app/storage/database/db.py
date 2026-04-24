from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
import os

# Forçar DATABASE_URL para mysql (sobrescrever qualquer problema)
DATABASE_URL = "mysql+pymysql://smartuser:smartpass@mysql:3306/smartsaude"

# Debug para garantir URL correta
print(f"DATABASE_URL FORÇADO: {DATABASE_URL}")

# Limpar qualquer cache e criar engine novo
engine = create_engine(
    DATABASE_URL, 
    pool_pre_ping=True,
    pool_recycle=3600,  # Reciclar conexões a cada hora
    echo=False  # Mudar para True para debug SQL se necessário
)

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
