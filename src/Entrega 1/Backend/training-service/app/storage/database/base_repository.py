from sqlalchemy.orm import Session
from sqlalchemy import create_engine
from app.core.config import settings

engine = create_engine(settings.db_url)

def SessionLocal():
    return Session(bind=engine)
