from datetime import date
from typing import List, Optional

from app.repositories.goals_repository import GoalsRepository
from app.models.schemas.goals_schema import GoalCreate, GoalUpdate, GoalResponse, GoalType, GoalStatus


class GoalsService:
    
    def __init__(self):
        self.goals_repo = GoalsRepository()
    
    def create_goal(self, patient_id: int, goal_data: GoalCreate) -> GoalResponse:
        """Cria nova meta para o paciente"""
        goal = self.goals_repo.create_goal(
            patient_id=patient_id,
            goal_type=goal_data.goal_type.value,
            target_value=goal_data.target_value,
            deadline=goal_data.deadline
        )
        
        return self._map_to_response(goal)
    
    def get_patient_goals(self, patient_id: int) -> List[GoalResponse]:
        """Retorna todas as metas do paciente"""
        goals = self.goals_repo.get_patient_goals(patient_id)
        
        # Atualizar status de metas expiradas
        self._update_expired_goals_status(goals)
        
        return [self._map_to_response(goal) for goal in goals]
    
    def get_goal(self, goal_id: int, patient_id: int) -> Optional[GoalResponse]:
        """Retorna meta específica do paciente"""
        goal = self.goals_repo.get_goal_by_id(goal_id, patient_id)
        if not goal:
            return None
        
        # Atualizar status se expirou
        self._update_goal_status_if_expired(goal)
        
        return self._map_to_response(goal)
    
    def update_goal(self, goal_id: int, patient_id: int, goal_data: GoalUpdate) -> Optional[GoalResponse]:
        """Atualiza meta do paciente"""
        update_data = {}
        
        if goal_data.target_value is not None:
            update_data["target_value"] = goal_data.target_value
        
        if goal_data.deadline is not None:
            update_data["deadline"] = goal_data.deadline
        
        if not update_data:
            return self.get_goal(goal_id, patient_id)
        
        goal = self.goals_repo.update_goal(goal_id, patient_id, **update_data)
        if not goal:
            return None
        
        return self._map_to_response(goal)
    
    def delete_goal(self, goal_id: int, patient_id: int) -> bool:
        """Remove meta do paciente"""
        return self.goals_repo.delete_goal(goal_id, patient_id)
    
    def update_goal_progress(self, patient_id: int, goal_type: GoalType, current_value: int) -> List[GoalResponse]:
        """Atualiza progresso de metas por tipo"""
        updated_goals = self.goals_repo.update_goal_progress(
            patient_id, goal_type.value, current_value
        )
        
        return [self._map_to_response(goal) for goal in updated_goals]
    
    def get_active_goals_count(self, patient_id: int) -> int:
        """Retorna quantidade de metas ativas"""
        goals = self.goals_repo.get_patient_goals(patient_id)
        active_count = sum(1 for goal in goals if goal.status == "active")
        return active_count
    
    def get_completed_goals_count(self, patient_id: int) -> int:
        """Retorna quantidade de metas completadas"""
        goals = self.goals_repo.get_patient_goals(patient_id)
        completed_count = sum(1 for goal in goals if goal.status == "completed")
        return completed_count
    
    def _map_to_response(self, goal) -> GoalResponse:
        """Converte ORM para response schema"""
        return GoalResponse(
            id=goal.id,
            goal_type=GoalType(goal.goal_type),
            target_value=goal.target_value,
            current_value=goal.current_value,
            deadline=goal.deadline,
            status=GoalStatus(goal.status)
        )
    
    def _update_expired_goals_status(self, goals) -> None:
        """Atualiza status de metas expiradas em lote"""
        today = date.today()
        for goal in goals:
            if goal.status == "active" and goal.deadline < today:
                goal.status = "expired"
    
    def _update_goal_status_if_expired(self, goal) -> None:
        """Atualiza status de uma meta se expirou"""
        if goal.status == "active" and goal.deadline < date.today():
            goal.status = "expired"
