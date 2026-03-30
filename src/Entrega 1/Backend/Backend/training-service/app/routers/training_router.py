from fastapi import APIRouter, Depends, Header
from sqlalchemy.orm import Session
from app.core.rbac_client import verify_token_and_role
from app.core.access_guard import assert_patient_scope
from app.storage.database.db import get_db
from app.services.training_service import TrainingService
from app.models.schemas.training_schema import (
    TrainingPlanCreate, TrainingPlanOut,
    TrainingPlanItemCreate,
    TrainingLogCreate, TrainingLogOut,
)

router = APIRouter()
_service = TrainingService()


@router.post("/plans", response_model=TrainingPlanOut)
def create_plan(payload: TrainingPlanCreate, authorization: str | None = Header(default=None), db: Session = Depends(get_db)):
    payload_auth = verify_token_and_role(authorization, ["Admin", "Professional"])
    return _service.create_plan(payload, db)


@router.post("/plans/{plan_id}/items")
def add_item(plan_id: int, payload: TrainingPlanItemCreate, authorization: str | None = Header(default=None), db: Session = Depends(get_db)):
    verify_token_and_role(authorization, ["Admin", "Professional"])
    return _service.add_item(plan_id, payload, db)


@router.get("/plans/patient/{patient_id}", response_model=list[TrainingPlanOut])
def list_plans(patient_id: int, authorization: str | None = Header(default=None), db: Session = Depends(get_db)):
    payload_auth = verify_token_and_role(authorization, ["Admin", "Professional", "Patient"])
    assert_patient_scope(payload_auth, patient_id)
    return _service.list_plans_by_patient(patient_id, db)


@router.post("/logs", response_model=TrainingLogOut)
def create_log(payload: TrainingLogCreate, authorization: str | None = Header(default=None), db: Session = Depends(get_db)):
    payload_auth = verify_token_and_role(authorization, ["Admin", "Patient"])
    assert_patient_scope(payload_auth, payload.patient_id)
    return _service.create_log(payload, db)


@router.get("/logs/patient/{patient_id}", response_model=list[TrainingLogOut])
def list_logs(patient_id: int, authorization: str | None = Header(default=None), db: Session = Depends(get_db)):
    payload_auth = verify_token_and_role(authorization, ["Admin", "Professional", "Patient"])
    assert_patient_scope(payload_auth, patient_id)
    return _service.list_logs_by_patient(patient_id, db)
