from fastapi import APIRouter, Depends, HTTPException, Query, Header
from sqlalchemy.orm import Session

from app.core.rbac_client import verify_token_and_role
from app.services.ideal_angles_service import IdealAnglesService
from app.storage.database.db import get_db

router = APIRouter(prefix="/training/exercises", tags=["Training - Exercises"])


@router.get("/{exercise_id}/ideal-angles")
def get_ideal_angles(
    exercise_id: int,
    authorization: str | None = Header(default=None),
    db: Session = Depends(get_db)
) -> dict:
    verify_token_and_role(authorization, ["Admin", "Professional", "Patient"])
    
    service = IdealAnglesService()
    result = service.get_ideal_angles(exercise_id)
    
    if not result:
        raise HTTPException(status_code=404, detail="Exercise not found")
    
    return {
        "exercise_id": result.exercise_id,
        "ideal_angles": result.ideal_angles
    }
