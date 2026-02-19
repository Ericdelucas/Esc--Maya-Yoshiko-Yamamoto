from fastapi import APIRouter, Depends, File, Header, UploadFile, HTTPException, status
from sqlalchemy.orm import Session
from app.storage.database.db import get_db
from app.models.schemas.exercise_schema import ExerciseCreate, ExerciseOut
from app.services.exercise_service import ExerciseService
from app.core.rbac_client import verify_token_and_role

router = APIRouter()
_service = ExerciseService()


@router.get("", response_model=list[ExerciseOut])
def list_exercises(authorization: str | None = Header(default=None), db: Session = Depends(get_db)):
    verify_token_and_role(authorization, allowed_roles=["Admin", "Professional", "Patient"])
    return _service.list_recent(db=db)


@router.post("", response_model=ExerciseOut)
def create_exercise(payload: ExerciseCreate, authorization: str | None = Header(default=None), db: Session = Depends(get_db)):
    verify_token_and_role(authorization, allowed_roles=["Admin", "Professional"])
    return _service.create(payload, db)


@router.post("/{exercise_id}/media", response_model=ExerciseOut)
async def upload_media(
    exercise_id: int,
    file: UploadFile = File(...),
    authorization: str | None = Header(default=None),
    db: Session = Depends(get_db),
):
    verify_token_and_role(authorization, allowed_roles=["Admin", "Professional"])

    content = await file.read()
    if not content:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Empty file")

    try:
        return _service.upload_media(exercise_id, file.filename or "file.bin", content, db)
    except ValueError:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Exercise not found")
