from pydantic import BaseModel, Field, validator


class ChangePasswordRequest(BaseModel):
    """Request para troca de senha com validações robustas"""
    current_password: str = Field(
        min_length=1, 
        max_length=128,
        description="Senha atual obrigatória"
    )
    new_password: str = Field(
        min_length=8, 
        max_length=128,
        description="Nova senha (mínimo 8 caracteres)"
    )
    confirm_password: str = Field(
        min_length=8, 
        max_length=128,
        description="Confirmação da nova senha"
    )
    
    @validator('confirm_password')
    def passwords_match(cls, v, values):
        if 'new_password' in values and v != values['new_password']:
            raise ValueError('Password confirmation does not match')
        return v
    
    @validator('new_password')
    def validate_new_password(cls, v, values):
        if 'current_password' in values and v == values['current_password']:
            raise ValueError('New password must be different from current password')
        return v
