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

@router.get("/user/{user_id}")
def get_user_notifications(user_id: int, db: Session = Depends(get_db)):
    """Buscar notificações pendentes de um usuário"""
    try:
        service = NotificationService()
        notifications = service.get_pending_by_user(user_id, db)
        return {"notifications": notifications}
    except Exception as e:
        print(f"❌ Erro ao buscar notificações: {e}")
        return {"notifications": []}
