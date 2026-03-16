from fastapi import APIRouter, Header, Depends
from sqlalchemy.orm import Session
from app.core.rbac_client import get_current_user
from app.models.schemas.ehr_schema import MedicalRecordCreate, MedicalRecordOut
from app.services.ehr_service import EhrService
from app.storage.database.db import get_db
from shared.security.dependencies import require_permission
from shared.security.permissions import EHR_CREATE, EHR_READ_OWN, EHR_READ_ANY

router = APIRouter()
_service = EhrService()


@router.post("/records", response_model=MedicalRecordOut)
def create_record(
    payload: MedicalRecordCreate,
    current_user: dict = Depends(require_permission(EHR_CREATE)),
    db: Session = Depends(get_db),
):
    print(f"DEBUG: User {current_user.get('email')} creating EHR record")
    return _service.create_record(payload, db)
