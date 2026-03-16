from pydantic import BaseModel, EmailStr, Field


class UserCreateIn(BaseModel):
    email: EmailStr
    password: str = Field(min_length=6, max_length=128)
    role: str = Field(default="Patient", description="User role: Patient, Professional, Admin")


class UserLoginIn(BaseModel):
    email: EmailStr
    password: str


class UserOut(BaseModel):
    id: int
    email: EmailStr
    role: str


class TokenOut(BaseModel):
    token: str
    type: str = "Bearer"


class UserMeResponse(BaseModel):
    """Dados do usuário logado para perfil"""
    id: int
    email: EmailStr
    full_name: str | None = None
    role: str
    profile_photo_url: str | None = None


class ChangePasswordRequest(BaseModel):
    """Request para troca de senha"""
    current_password: str = Field(min_length=1, description="Senha atual obrigatória")
    new_password: str = Field(min_length=8, max_length=128, description="Nova senha (mínimo 8 caracteres)")
    confirm_password: str = Field(min_length=8, description="Confirmação da nova senha")


class ProfilePhotoResponse(BaseModel):
    """Resposta após upload de foto de perfil"""
    message: str
    profile_photo_url: str
