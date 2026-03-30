from fastapi import APIRouter, Depends, HTTPException, status, Header, Query
from typing import Optional, Dict

from app.core.auth_dependencies import get_current_user
from app.services.leaderboard_service import LeaderboardService
from app.models.schemas.leaderboard_schema import LeaderboardResponse, LeaderboardEntry


router = APIRouter(prefix="/training/leaderboard", tags=["training-leaderboard"])


@router.get("/", response_model=LeaderboardResponse)
async def get_leaderboard(
    limit: int = Query(50, ge=1, le=100, description="Maximum number of rankings to return"),
    authorization: str = Header(...)
) -> LeaderboardResponse:
    """
    Retorna ranking de pacientes por pontos
    """
    try:
        current_user = await get_current_user(authorization)
        patient_id = int(current_user.get("sub"))
        
        leaderboard_service = LeaderboardService()
        return leaderboard_service.get_leaderboard(patient_id, limit)
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error retrieving leaderboard: {str(e)}"
        )


@router.get("/my-position", response_model=Dict)
async def get_my_position(
    authorization: str = Header(...)
) -> Dict:
    """
    Retorna apenas a posição do usuário atual no ranking
    """
    try:
        current_user = await get_current_user(authorization)
        patient_id = int(current_user.get("sub"))
        
        leaderboard_service = LeaderboardService()
        position_data = leaderboard_service.get_user_position(patient_id)
        
        return {
            "position": position_data["position"],
            "points": position_data["points"],
            "total_users": leaderboard_service.leaderboard_repo.get_total_users_count()
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error retrieving user position: {str(e)}"
        )


@router.get("/top", response_model=list[LeaderboardEntry])
async def get_top_users(
    limit: int = Query(10, ge=1, le=20, description="Number of top users to return"),
    authorization: str = Header(...)
) -> list[LeaderboardEntry]:
    """
    Retorna apenas os top usuários (para widgets e dashboards)
    """
    try:
        current_user = await get_current_user(authorization)
        leaderboard_service = LeaderboardService()
        return leaderboard_service.get_top_users(limit)
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error retrieving top users: {str(e)}"
        )
