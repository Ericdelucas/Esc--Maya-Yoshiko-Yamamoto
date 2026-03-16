from pydantic import BaseModel


class ProfilePhotoResponse(BaseModel):
    """Resposta após upload de foto de perfil"""
    message: str
    profile_photo_url: str
