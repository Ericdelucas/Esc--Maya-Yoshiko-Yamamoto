from sqlalchemy.orm import Session
from sqlalchemy import and_, or_
from datetime import date, datetime
from typing import List, Optional

from app.models.orm.training_orm import PatientGoalORM
from app.storage.database.base_repository import SessionLocal


class GoalsRepository:
    
    def create_goal(self, patient_id: int, goal_type: str, target_value: int, deadline: date) -> PatientGoalORM:
        """Cria nova meta para o paciente"""
        with SessionLocal() as session:
            goal = PatientGoalORM(
                patient_id=patient_id,
                goal_type=goal_type,
                target_value=target_value,
                deadline=deadline,
                status="active"
            )
            session.add(goal)
            session.commit()
            session.refresh(goal)
            return goal
    
    def get_patient_goals(self, patient_id: int) -> List[PatientGoalORM]:
        """Retorna todas as metas do paciente"""
        with SessionLocal() as session:
            return session.query(PatientGoalORM).filter(
                PatientGoalORM.patient_id == patient_id
            ).order_by(PatientGoalORM.deadline).all()
    
    def get_goal_by_id(self, goal_id: int, patient_id: int) -> Optional[PatientGoalORM]:
        """Retorna meta específica do paciente"""
        with SessionLocal() as session:
            return session.query(PatientGoalORM).filter(
                and_(
                    PatientGoalORM.id == goal_id,
                    PatientGoalORM.patient_id == patient_id
                )
            ).first()
    
    def update_goal(self, goal_id: int, patient_id: int, **kwargs) -> Optional[PatientGoalORM]:
        """Atualiza meta do paciente"""
        with SessionLocal() as session:
            goal = self.get_goal_by_id(goal_id, patient_id)
            if not goal:
                return None
            
            for key, value in kwargs.items():
                if hasattr(goal, key) and value is not None:
                    setattr(goal, key, value)
            
            goal.updated_at = datetime.utcnow()
            session.commit()
            session.refresh(goal)
            return goal
    
    def delete_goal(self, goal_id: int, patient_id: int) -> bool:
        """Remove meta do paciente"""
        with SessionLocal() as session:
            goal = self.get_goal_by_id(goal_id, patient_id)
            if not goal:
                return False
            
            session.delete(goal)
            session.commit()
            return True
    
    def update_goal_progress(self, patient_id: int, goal_type: str, current_value: int) -> List[PatientGoalORM]:
        """Atualiza progresso de metas baseado no tipo"""
        with SessionLocal() as session:
            goals = session.query(PatientGoalORM).filter(
                and_(
                    PatientGoalORM.patient_id == patient_id,
                    PatientGoalORM.goal_type == goal_type,
                    PatientGoalORM.status == "active"
                )
            ).all()
            
            updated_goals = []
            for goal in goals:
                goal.current_value = current_value
                
                # Verificar se completou
                if current_value >= goal.target_value:
                    goal.status = "completed"
                # Verificar se expirou
                elif date.today() > goal.deadline:
                    goal.status = "expired"
                
                goal.updated_at = datetime.utcnow()
                updated_goals.append(goal)
            
            session.commit()
            return updated_goals
    
    def get_expired_goals(self) -> List[PatientGoalORM]:
        """Retorna metas expiradas para atualização em batch"""
        with SessionLocal() as session:
            return session.query(PatientGoalORM).filter(
                and_(
                    PatientGoalORM.status == "active",
                    PatientGoalORM.deadline < date.today()
                )
            ).all()
    
    def batch_update_expired_goals(self) -> int:
        """Atualiza status de metas expiradas"""
        with SessionLocal() as session:
            expired_goals = self.get_expired_goals()
            count = 0
            
            for goal in expired_goals:
                goal.status = "expired"
                goal.updated_at = datetime.utcnow()
                count += 1
            
            session.commit()
            return count
