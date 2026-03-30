from pydantic import BaseModel, Field


class MedicalRecordCreate(BaseModel):
    patient_id: int = Field(ge=1)
    professional_id: int = Field(ge=1)
    notes: str = Field(min_length=1, max_length=8000)


class MedicalRecordOut(BaseModel):
    id: int
    patient_id: int
    professional_id: int
    notes: str
    created_at_iso: str
