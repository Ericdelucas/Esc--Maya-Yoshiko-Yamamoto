from pydantic import BaseModel, EmailStr
from typing import Optional


class UserMeResponse(BaseModel):
    """Dados completos do usuário logado para tela de perfil"""
    id: int
    email: EmailStr
    full_name: Optional[str] = None
    role: str
    profile_photo_url: Optional[str] = None
    
    class Config:
        from_attributes = True
