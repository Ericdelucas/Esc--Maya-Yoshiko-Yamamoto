from fastapi import APIRouter, Depends, File, Header, UploadFile, HTTPException, status
from sqlalchemy.orm import Session
from app.storage.database.db import get_db
from app.models.schemas.exercise_schema import ExerciseCreate, ExerciseOut, FileUploadResponse
from app.services.exercise_service import ExerciseService
from app.services.file_storage_service import file_storage_service
from app.core.rbac_client import get_current_user
from shared.security.dependencies import require_permission
from shared.security.permissions import EXERCISE_CREATE, EXERCISE_UPLOAD_MEDIA, USER_MANAGE, EXERCISE_READ

router = APIRouter()
_service = ExerciseService()


@router.post("/upload/image", response_model=FileUploadResponse)
async def upload_image(
    file: UploadFile = File(...),
    current_user: dict = Depends(require_permission(EXERCISE_UPLOAD_MEDIA))
):
    """Upload de imagem para exercício"""
    try:
        filename, file_url, content_type = await file_storage_service.save_image(file)
        return FileUploadResponse(
            file_name=filename,
            file_url=file_url,
            content_type=content_type
        )
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to upload image: {str(e)}"
        )


@router.post("/upload/video", response_model=FileUploadResponse)
async def upload_video(
    file: UploadFile = File(...),
    current_user: dict = Depends(require_permission(EXERCISE_UPLOAD_MEDIA))
):
    """Upload de vídeo para exercício"""
    try:
        filename, file_url, content_type = await file_storage_service.save_video(file)
        return FileUploadResponse(
            file_name=filename,
            file_url=file_url,
            content_type=content_type
        )
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to upload video: {str(e)}"
        )


@router.get("", response_model=list[ExerciseOut])
def list_exercises(
    current_user: dict = Depends(require_permission(EXERCISE_READ)),
    db: Session = Depends(get_db)
):
    return _service.list_recent(db=db)


@router.get("/{exercise_id}", response_model=ExerciseOut)
def get_exercise(
    exercise_id: int,
    current_user: dict = Depends(require_permission(EXERCISE_READ)),
    db: Session = Depends(get_db)
):
    try:
        return _service.get_by_id(exercise_id, db)
    except ValueError:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Exercise not found"
        )


@router.post("", response_model=ExerciseOut)
def create_exercise(
    payload: ExerciseCreate,
    current_user: dict = Depends(require_permission(EXERCISE_CREATE)),
    db: Session = Depends(get_db)
):
    """Cria exercício com mídia (paths de upload prévios)"""
    user_id = current_user.get("sub")  # JWT usa 'sub' para user_id
    return _service.create(payload, user_id, db)


@router.get("/admin/stats")
def get_admin_stats(
    current_user: dict = Depends(require_permission(USER_MANAGE)),
    db: Session = Depends(get_db)
):
    """Endpoint administrativo para estatísticas - apenas Admin"""
    exercises = _service.list_recent(db, limit=1000)
    return {
        "total_exercises": len(exercises),
        "service": "exercise-service",
        "admin_user": current_user.get("email")
    }
