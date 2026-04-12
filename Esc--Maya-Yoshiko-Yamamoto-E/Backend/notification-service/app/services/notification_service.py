from datetime import datetime
from sqlalchemy.orm import Session
from app.models.schemas.notification_schema import NotificationCreate, NotificationOut
from app.storage.database.notification_repository import NotificationRepository


def _parse_iso(value: str | None) -> datetime | None:
    if not value:
        return None
    return datetime.fromisoformat(value.replace("Z", "+00:00")).replace(tzinfo=None)


class NotificationService:
    def create(self, payload: NotificationCreate, db: Session) -> NotificationOut:
        schedule_at = _parse_iso(payload.schedule_at_iso)
        row = NotificationRepository(db).create(
            user_id=payload.user_id,
            channel=payload.channel,
            title=payload.title,
            message=payload.message,
            schedule_at=schedule_at,
        )
        return NotificationOut(status=row.status, notification_id=str(row.id))
