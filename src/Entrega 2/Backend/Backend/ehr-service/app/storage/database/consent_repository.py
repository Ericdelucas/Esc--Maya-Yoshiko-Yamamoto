from sqlalchemy.orm import Session
from sqlalchemy import desc
from app.models.orm.consent_record_orm import ConsentRecordORM


class ConsentRepository:
    def __init__(self, db: Session) -> None:
        self._db = db

    def latest_for_user(self, user_id: int, consent_type: str) -> ConsentRecordORM | None:
        return (
            self._db.query(ConsentRecordORM)
            .filter(
                ConsentRecordORM.user_id == user_id,
                ConsentRecordORM.consent_type == consent_type,
            )
            .order_by(desc(ConsentRecordORM.id))
            .first()
        )
