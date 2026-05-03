from fastapi import APIRouter, Depends, Header
from sqlalchemy.orm import Session
from app.core.rbac_client import get_current_user
from app.models.schemas.consent_schema import ConsentCreate
from app.services.consent_service import ConsentService
from app.storage.database.db import get_db
from shared.security.dependencies import require_permission
from shared.security.permissions import CONSENT_MANAGE_OWN, CONSENT_MANAGE_ANY

router = APIRouter()
_service = ConsentService()


@router.post("/consents")
def create_consent(
    payload: ConsentCreate,
    current_user: dict = Depends(require_permission(CONSENT_MANAGE_OWN)),
    db: Session = Depends(get_db),
):
    rec = _service.create_consent(payload, db)
    return {"status": "ok", "consent_id": rec.id}
