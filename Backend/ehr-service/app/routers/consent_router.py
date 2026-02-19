from fastapi import APIRouter, Depends, Header
from sqlalchemy.orm import Session
from app.core.rbac_client import verify_token_and_role
from app.models.schemas.consent_schema import ConsentCreate
from app.services.consent_service import ConsentService
from app.storage.database.db import get_db

router = APIRouter()
_service = ConsentService()


@router.post("/consents")
def create_consent(
    payload: ConsentCreate,
    authorization: str | None = Header(default=None),
    db: Session = Depends(get_db),
):
    verify_token_and_role(authorization, allowed_roles=["Admin", "Patient"])
    rec = _service.create_consent(payload, db)
    return {"status": "ok", "consent_id": rec.id}
