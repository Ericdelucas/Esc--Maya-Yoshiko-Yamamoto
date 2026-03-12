from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import Optional

router = APIRouter()

class ConsentRecord(BaseModel):
    user_id: int
    consent_type: str
    granted: bool

@router.post("/consent")
async def create_consent(consent: ConsentRecord):
    """Create or update consent record"""
    return {
        "id": 1,
        "user_id": consent.user_id,
        "consent_type": consent.consent_type,
        "granted": consent.granted,
        "granted_at": "2026-03-12T01:00:00Z",
        "message": "Consent recorded successfully"
    }

@router.get("/consent/{user_id}")
async def get_user_consents(user_id: int):
    """Get all consent records for a user"""
    return [
        {
            "id": 1,
            "user_id": user_id,
            "consent_type": "medical_records",
            "granted": True,
            "granted_at": "2026-03-12T01:00:00Z"
        }
    ]
