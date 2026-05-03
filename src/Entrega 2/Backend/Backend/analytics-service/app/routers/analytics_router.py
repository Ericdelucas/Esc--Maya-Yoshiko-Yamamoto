from fastapi import APIRouter, Depends, Header, Query
from sqlalchemy.orm import Session
from app.storage.database.db import get_db
from app.services.analytics_service import AnalyticsService
from app.models.schemas.analytics_schema import GlobalEhrStatsOut, PatientEhrSummaryOut, PatientTrainingWeeklyOut, PatientTrainingRiskOut, GlobalTrainingAdherenceRow, GlobalTrainingRiskRow
from app.core.rbac_client import verify_token_and_role
from app.core.access_guard import assert_patient_scope

router = APIRouter()
_service = AnalyticsService()


@router.get("/ehr/global", response_model=GlobalEhrStatsOut)
def ehr_global(authorization: str | None = Header(default=None), db: Session = Depends(get_db)):
    verify_token_and_role(authorization, allowed_roles=["Admin", "Professional"])
    return _service.global_ehr_stats(db)


@router.get("/ehr/patient/{patient_id}", response_model=PatientEhrSummaryOut)
def ehr_patient(patient_id: int, authorization: str | None = Header(default=None), db: Session = Depends(get_db)):
    verify_token_and_role(authorization, allowed_roles=["Admin", "Professional"])
    return _service.patient_ehr_summary(patient_id, db)


@router.get("/training/patient/{patient_id}/weekly", response_model=list[PatientTrainingWeeklyOut])
def training_weekly(
    patient_id: int,
    weeks: int = 8,
    authorization: str | None = Header(default=None),
    db: Session = Depends(get_db),
):
    payload = verify_token_and_role(authorization, allowed_roles=["Admin", "Professional", "Patient"])
    assert_patient_scope(payload, patient_id)
    return _service.patient_training_weekly(patient_id=patient_id, weeks=weeks, db=db)


@router.get("/training/patient/{patient_id}/risk-7d", response_model=PatientTrainingRiskOut)
def training_risk(
    patient_id: int,
    authorization: str | None = Header(default=None),
    db: Session = Depends(get_db),
):
    payload = verify_token_and_role(authorization, allowed_roles=["Admin", "Professional", "Patient"])
    assert_patient_scope(payload, patient_id)
    return _service.patient_training_risk_7d(patient_id=patient_id, db=db)


@router.get("/training/global/adherence-7d", response_model=list[GlobalTrainingAdherenceRow])
def global_adherence(
    limit: int = 20,
    authorization: str | None = Header(default=None),
    db: Session = Depends(get_db),
):
    verify_token_and_role(authorization, allowed_roles=["Admin", "Professional"])
    return _service.global_training_adherence_7d(limit=limit, db=db)


@router.get("/training/global/risk-7d", response_model=list[GlobalTrainingRiskRow])
def global_risk(
    pain_threshold: float = 4.0,
    effort_threshold: float = 7.0,
    limit: int = 50,
    authorization: str | None = Header(default=None),
    db: Session = Depends(get_db),
):
    verify_token_and_role(authorization, allowed_roles=["Admin", "Professional"])
    return _service.global_training_risk_7d(
        pain_threshold=pain_threshold,
        effort_threshold=effort_threshold,
        limit=limit,
        db=db,
    )
