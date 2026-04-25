from datetime import datetime
from sqlalchemy.orm import Session
from sqlalchemy import and_, asc
from app.models.orm.notification_orm import NotificationORM


class NotificationRepository:
    def __init__(self, db: Session) -> None:
        self._db = db

    def create(self, user_id: int, channel: str, title: str, message: str, schedule_at: datetime | None) -> NotificationORM:
        row = NotificationORM(
            user_id=user_id,
            channel=channel,
            title=title,
            message=message,
            schedule_at=schedule_at,
            status="queued",
        )
        self._db.add(row)
        self._db.commit()
        self._db.refresh(row)
        return row

    def next_due(self, limit: int = 20) -> list[NotificationORM]:
        now = datetime.utcnow()
        return (
            self._db.query(NotificationORM)
            .filter(
                and_(
                    NotificationORM.status == "queued",
                    (NotificationORM.schedule_at == None) | (NotificationORM.schedule_at <= now),
                )
            )
            .order_by(asc(NotificationORM.id))
            .limit(limit)
            .all()
        )

    def mark_sent(self, notif_id: int) -> None:
        row = self._db.query(NotificationORM).filter(NotificationORM.id == notif_id).first()
        if not row:
            return
        row.status = "sent"
        self._db.commit()

    def mark_failed(self, notif_id: int) -> None:
        row = self._db.query(NotificationORM).filter(NotificationORM.id == notif_id).first()
        if not row:
            return
        row.status = "failed"
        self._db.commit()
    
    def get_pending_by_user(self, user_id: int) -> list[NotificationORM]:
        """Buscar notificações pendentes de um usuário"""
        return (
            self._db.query(NotificationORM)
            .filter(
                and_(
                    NotificationORM.user_id == user_id,
                    NotificationORM.status == "queued"
                )
            )
            .order_by(asc(NotificationORM.created_at))
            .all()
        )
