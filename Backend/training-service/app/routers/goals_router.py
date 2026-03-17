from fastapi import APIRouter, Depends, HTTPException, status, Header, Path
from typing import List, Optional, Dict

from app.core.auth_dependencies import get_current_user
from app.services.goals_service import GoalsService
from app.models.schemas.goals_schema import GoalCreate, GoalUpdate, GoalResponse


router = APIRouter(prefix="/training/goals", tags=["training-goals"])


@router.get("/", response_model=List[GoalResponse])
async def get_goals(
    authorization: str = Header(...)
) -> List[GoalResponse]:
    """
    Retorna todas as metas do paciente logado
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
                detail="Not authorized to access goals"
            )
        
        goals_service = GoalsService()
        return goals_service.get_patient_goals(patient_id)
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error retrieving goals: {str(e)}"
        )


@router.post("/", response_model=GoalResponse)
async def create_goal(
    goal_data: GoalCreate,
    authorization: str = Header(...)
) -> GoalResponse:
    """
    Cria nova meta para o paciente
    """
    try:
        current_user = await get_current_user(authorization)
        patient_id = int(current_user.get("sub"))
        
        goals_service = GoalsService()
        return goals_service.create_goal(patient_id, goal_data)
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error creating goal: {str(e)}"
        )


@router.get("/{goal_id}", response_model=GoalResponse)
async def get_goal(
    goal_id: int = Path(..., description="ID da meta"),
    authorization: str = Header(...)
) -> GoalResponse:
    """
    Retorna meta específica do paciente
    """
    try:
        current_user = await get_current_user(authorization)
        patient_id = int(current_user.get("sub"))
        
        goals_service = GoalsService()
        goal = goals_service.get_goal(goal_id, patient_id)
        
        if not goal:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Goal not found"
            )
        
        return goal
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error retrieving goal: {str(e)}"
        )


@router.put("/{goal_id}", response_model=GoalResponse)
async def update_goal(
    goal_id: int = Path(..., description="ID da meta"),
    authorization: str = Header(...),
    goal_data: GoalUpdate = None
) -> GoalResponse:
    """
    Atualiza meta do paciente
    """
    try:
        current_user = await get_current_user(authorization)
        patient_id = int(current_user.get("sub"))
        
        goals_service = GoalsService()
        goal = goals_service.update_goal(goal_id, patient_id, goal_data)
        
        if not goal:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Goal not found"
            )
        
        return goal
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error updating goal: {str(e)}"
        )


@router.delete("/{goal_id}")
async def delete_goal(
    goal_id: int = Path(..., description="ID da meta"),
    authorization: str = Header(...)
) -> Dict:
    """
    Remove meta do paciente
    """
    try:
        current_user = await get_current_user(authorization)
        patient_id = int(current_user.get("sub"))
        
        goals_service = GoalsService()
        success = goals_service.delete_goal(goal_id, patient_id)
        
        if not success:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Goal not found"
            )
        
        return {"message": "Goal deleted successfully"}
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error deleting goal: {str(e)}"
        )


@router.get("/stats/summary")
async def get_goals_stats(
    authorization: str = Header(...)
) -> Dict:
    """
    Retorna estatísticas das metas do paciente
    """
    try:
        current_user = await get_current_user(authorization)
        patient_id = int(current_user.get("sub"))
        
        goals_service = GoalsService()
        active_count = goals_service.get_active_goals_count(patient_id)
        completed_count = goals_service.get_completed_goals_count(patient_id)
        
        return {
            "active_goals": active_count,
            "completed_goals": completed_count,
            "total_goals": active_count + completed_count
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error retrieving goals stats: {str(e)}"
        )
