from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from app.storage.database.db import get_db
from app.storage.database.notification_repository import NotificationRepository

router = APIRouter()


@router.get("/queued")
def list_queued(db: Session = Depends(get_db)):
    rows = NotificationRepository(db).next_due(limit=50)
    return [
        {
            "id": r.id,
            "user_id": r.user_id,
            "channel": r.channel,
            "title": r.title,
            "status": r.status,
            "schedule_at": r.schedule_at.isoformat() if r.schedule_at else None,
        }
        for r in rows
    ]
