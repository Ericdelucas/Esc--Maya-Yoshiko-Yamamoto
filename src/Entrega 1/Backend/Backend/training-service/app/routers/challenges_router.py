from fastapi import APIRouter, Depends, HTTPException, status, Header, Path
from typing import List, Dict

from app.core.auth_dependencies import get_current_user
from app.services.challenges_service import ChallengesService
from app.models.schemas.challenges_schema import ChallengeResponse


router = APIRouter(prefix="/training/challenges", tags=["training-challenges"])


@router.get("/", response_model=List[ChallengeResponse])
async def get_challenges(
    authorization: str = Header(...)
) -> List[ChallengeResponse]:
    """
    Retorna desafios disponíveis com progresso do paciente
    """
    try:
        current_user = await get_current_user(authorization)
        
        # Verificar se é paciente ou tem permissão
        if current_user.get("role") == "Patient":
            patient_id = int(current_user.get("sub"))
        elif current_user.get("role") in ["Professional", "Admin"]:
            # Simplificado - implementar lógica específica
            patient_id = int(current_user.get("sub"))
        else:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Not authorized to access challenges"
            )
        
        challenges_service = ChallengesService()
        return challenges_service.get_patient_challenges(patient_id)
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error retrieving challenges: {str(e)}"
        )


@router.get("/available", response_model=List[Dict])
async def get_available_challenges(
    authorization: str = Header(...)
) -> List[Dict]:
    """
    Retorna desafios disponíveis (sem progresso específico)
    """
    try:
        current_user = await get_current_user(authorization)
        challenges_service = ChallengesService()
        return challenges_service.get_available_challenges()
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error retrieving available challenges: {str(e)}"
        )


@router.post("/{challenge_id}/join", response_model=Dict)
async def join_challenge(
    challenge_id: int = Path(..., description="ID do desafio"),
    authorization: str = Header(...)
) -> Dict:
    """
    Paciente entra em um desafio
    """
    try:
        current_user = await get_current_user(authorization)
        patient_id = int(current_user.get("sub"))
        
        challenges_service = ChallengesService()
        result = challenges_service.join_challenge(patient_id, challenge_id)
        
        if result.get("joined", False):
            return result
        else:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=result.get("message", "Failed to join challenge")
            )
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error joining challenge: {str(e)}"
        )


@router.post("/update-progress", response_model=List[Dict])
async def update_challenge_progress(
    sessions_completed: int = 1,
    authorization: str = Header(...)
) -> List[Dict]:
    """
    Atualiza progresso em todos os desafios ativos do paciente
    (chamado após completar uma sessão)
    """
    try:
        current_user = await get_current_user(authorization)
        patient_id = int(current_user.get("sub"))
        
        challenges_service = ChallengesService()
        updated_challenges = challenges_service.update_challenge_progress(
            patient_id, sessions_completed
        )
        
        return {
            "message": f"Progress updated for {len(updated_challenges)} challenges",
            "updated_challenges": updated_challenges
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error updating challenge progress: {str(e)}"
        )
