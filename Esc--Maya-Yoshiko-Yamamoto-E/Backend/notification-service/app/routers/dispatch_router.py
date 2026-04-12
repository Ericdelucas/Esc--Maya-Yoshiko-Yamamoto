from fastapi import APIRouter, Depends, Header
from sqlalchemy.orm import Session
from app.storage.database.db import get_db
from app.services.dispatcher_service import DispatcherService

router = APIRouter()
_service = DispatcherService()


@router.post("/dispatch")
def dispatch(db: Session = Depends(get_db)):
    return _service.dispatch_once(db)
