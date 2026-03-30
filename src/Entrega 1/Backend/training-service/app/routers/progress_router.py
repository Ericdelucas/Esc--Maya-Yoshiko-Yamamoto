from fastapi import APIRouter, Depends, HTTPException, status, Header
from typing import Dict

from app.core.auth_dependencies import get_current_user, require_permission
from app.services.progress_service import ProgressService
from app.models.schemas.progress_schema import ProgressResponse


router = APIRouter(prefix="/training/progress", tags=["training-progress"])


@router.get("/", response_model=ProgressResponse)
async def get_progress(
    authorization: str = Header(...)
) -> ProgressResponse:
    """
    Retorna progresso completo do paciente logado
    """
    try:
        current_user = await get_current_user(authorization)
        
        # Verificar se é paciente ou tem permissão
        if current_user.get("role") == "Patient":
            patient_id = int(current_user.get("sub"))
        elif current_user.get("role") in ["Professional", "Admin"]:
            # Profissionais/Admins podem visualizar progresso (implementar lógica específica)
            patient_id = int(current_user.get("sub"))  # Simplificado
        else:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Not authorized to access progress data"
            )
        
        progress_service = ProgressService()
        return progress_service.get_patient_progress(patient_id)
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error retrieving progress: {str(e)}"
        )


@router.post("/add-session", response_model=Dict)
async def add_session_points(
    authorization: str = Header(...)
) -> Dict:
    """
    Adiciona pontos por sessão completada (usado após treino)
    """
    try:
        current_user = await get_current_user(authorization)
        patient_id = int(current_user.get("sub"))
        
        progress_service = ProgressService()
        result = progress_service.add_session_points(patient_id)
        
        return {
            "message": "Session points added successfully",
            **result
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error adding session points: {str(e)}"
        )
