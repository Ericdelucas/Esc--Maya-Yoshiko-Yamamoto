from pydantic import BaseModel, EmailStr, Field


class UserCreateIn(BaseModel):
    email: EmailStr
    password: str = Field(min_length=6, max_length=128)


class UserLoginIn(BaseModel):
    email: EmailStr
    password: str


class UserOut(BaseModel):
    id: int
    email: EmailStr


class TokenOut(BaseModel):
    token: str
    type: str = "Bearer"
