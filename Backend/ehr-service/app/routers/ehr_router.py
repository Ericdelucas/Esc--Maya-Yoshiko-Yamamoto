from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import Optional
from datetime import datetime

router = APIRouter()

class MedicalRecordCreate(BaseModel):
    patient_id: int
    professional_id: int
    notes: Optional[str] = None

class MedicalRecordOut(BaseModel):
    id: int
    patient_id: int
    professional_id: int
    notes: str
    created_at_iso: str

@router.post("/records")
async def create_record(record: MedicalRecordCreate):
    """Create a new medical record"""
    return MedicalRecordOut(
        id=1,
        patient_id=record.patient_id,
        professional_id=record.professional_id,
        notes=record.notes or "Test medical record",
        created_at_iso=datetime.now().isoformat()
    )

@router.get("/records/{patient_id}")
async def get_patient_records(patient_id: int):
    """Get medical records for a patient"""
    return [
        MedicalRecordOut(
            id=1,
            patient_id=patient_id,
            professional_id=1,
            notes="Sample medical record",
            created_at_iso=datetime.now().isoformat()
        )
    ]
