from fastapi import APIRouter, Depends, Header
from sqlalchemy.orm import Session
from app.core.rbac_client import verify_token_and_role
from app.storage.database.db import get_db
from app.services.training_service import TrainingService
from app.models.schemas.training_schema import TrainingPlanOut, TrainingLogOut

router = APIRouter()
_service = TrainingService()


@router.get("/me/plans", response_model=list[TrainingPlanOut])
def my_plans(authorization: str | None = Header(default=None), db: Session = Depends(get_db)):
    payload = verify_token_and_role(authorization, allowed_roles=["Admin", "Professional", "Patient"])
    patient_id = int(payload["sub"])
    return _service.list_plans_by_patient(patient_id, db)


@router.get("/me/logs", response_model=list[TrainingLogOut])
def my_logs(authorization: str | None = Header(default=None), db: Session = Depends(get_db)):
    payload = verify_token_and_role(authorization, allowed_roles=["Admin", "Professional", "Patient"])
    patient_id = int(payload["sub"])
    return _service.list_logs_by_patient(patient_id, db)
