from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from app.core.config import settings
from pydantic import BaseModel
import hashlib

router = APIRouter()

class LoginRequest(BaseModel):
    email: str
    password: str

class RegisterRequest(BaseModel):
    email: str
    password: str
    role: str = "Patient"

def get_db():
    # TODO: Implement proper dependency injection
    pass

@router.post("/login")
async def login(request: LoginRequest):
    """Simple login endpoint for testing"""
    # For now, just check against our test users
    test_users = {
        "patient@test.com": "123456",
        "professional@test.com": "123456", 
        "admin@test.com": "123456"
    }
    
    if request.email in test_users and test_users[request.email] == request.password:
        return {
            "access_token": "mock_token_for_testing",
            "token_type": "bearer",
            "user": {
                "email": request.email,
                "role": "patient" if "patient" in request.email else "professional" if "professional" in request.email else "admin"
            }
        }
    
    raise HTTPException(status_code=401, detail="Invalid credentials")

@router.post("/register")
async def register(request: RegisterRequest):
    """Simple register endpoint for testing"""
    return {
        "message": "User registered successfully",
        "user": {
            "email": request.email,
            "role": request.role
        }
    }
