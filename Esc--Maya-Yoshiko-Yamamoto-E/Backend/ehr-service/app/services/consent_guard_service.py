from fastapi import HTTPException, status
from sqlalchemy.orm import Session
from app.core.consent_types import EHR_PROCESSING
from app.storage.database.consent_repository import ConsentRepository


class ConsentGuardService:
    def __init__(self, db: Session) -> None:
        self._repo = ConsentRepository(db)

    def assert_ehr_consent(self, patient_id: int) -> None:
        latest = self._repo.latest_for_user(user_id=patient_id, consent_type=EHR_PROCESSING)

        if not latest or latest.granted is not True:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Missing active consent for EHR processing",
            )
