from datetime import datetime
from sqlalchemy.orm import Session
from app.models.schemas.consent_schema import ConsentCreate
from app.models.orm.consent_record_orm import ConsentRecordORM


class ConsentService:
    def create_consent(self, payload: ConsentCreate, db: Session) -> ConsentRecordORM:
        now = datetime.utcnow()
        rec = ConsentRecordORM(
            user_id=payload.user_id,
            consent_type=payload.consent_type,
            granted=payload.granted,
            granted_at=now if payload.granted else None,
            revoked_at=None if payload.granted else now,
        )
        db.add(rec)
        db.commit()
        db.refresh(rec)
        return rec
