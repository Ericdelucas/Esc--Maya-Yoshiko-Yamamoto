from sqlalchemy import Column, Integer, String, DateTime, Text
from sqlalchemy.sql import func

from app.storage.database.base import Base


class UserORM(Base):
    __tablename__ = "users"
    
    id = Column(Integer, primary_key=True, index=True)
    email = Column(String(255), unique=True, nullable=False, index=True)
    password_hash = Column(String(255), nullable=False)
    role = Column(String(32), nullable=False, default="Patient")
    full_name = Column(String(255), nullable=True)
    profile_photo_url = Column(String(512), nullable=True)
    created_at = Column(DateTime, default=func.now())
    updated_at = Column(DateTime, default=func.now(), onupdate=func.now())
