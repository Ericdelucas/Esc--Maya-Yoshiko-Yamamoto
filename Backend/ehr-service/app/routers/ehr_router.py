from fastapi import APIRouter, Header, Depends
from sqlalchemy.orm import Session
from app.core.rbac_client import verify_token_and_role
from app.models.schemas.ehr_schema import MedicalRecordCreate, MedicalRecordOut
from app.services.ehr_service import EhrService
from app.storage.database.db import get_db

router = APIRouter()
_service = EhrService()


@router.post("/records", response_model=MedicalRecordOut)
def create_record(
    payload: MedicalRecordCreate,
    authorization: str | None = Header(default=None),
    db: Session = Depends(get_db),
):
    verify_token_and_role(authorization, allowed_roles=["Admin", "Professional"])
    return _service.create_record(payload, db)
