from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from app.models.schemas.notification_schema import NotificationCreate, NotificationOut
from app.services.notification_service import NotificationService
from app.storage.database.db import get_db

router = APIRouter()
_service = NotificationService()


@router.post("", response_model=NotificationOut)
def create_notification(payload: NotificationCreate, db: Session = Depends(get_db)) -> NotificationOut:
    return _service.create(payload, db)
