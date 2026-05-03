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
    
    def get_pending_by_user(self, user_id: int, db: Session):
        """Buscar notificações pendentes de um usuário"""
        notifications = NotificationRepository(db).get_pending_by_user(user_id)
        
        result = []
        for notif in notifications:
            result.append({
                "id": notif.id,
                "title": notif.title,
                "message": notif.message,
                "created_at": notif.created_at.isoformat(),
                "status": notif.status
            })
        
        return result
