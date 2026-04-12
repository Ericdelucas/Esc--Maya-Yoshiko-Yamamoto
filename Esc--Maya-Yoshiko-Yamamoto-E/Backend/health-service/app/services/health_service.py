from sqlalchemy.orm import Session
from datetime import datetime
from typing import List, Optional
from ..models.orm.health_models import HealthMetric, HealthQuestionnaire, HealthGoal, HealthProgress
from ..storage.database import get_db

class HealthService:
    
    def __init__(self, db: Session):
        self.db = db
    
    def create_health_metric(self, user_id: int, metric_type: str, value: float, unit: str, 
                           classification: str = None, notes: str = None) -> HealthMetric:
        """Create a new health metric"""
        metric = HealthMetric(
            user_id=user_id,
            metric_type=metric_type,
            value=value,
            unit=unit,
            classification=classification,
            measured_at=datetime.now(),
            notes=notes
        )
        
        self.db.add(metric)
        self.db.commit()
        self.db.refresh(metric)
        
        print(f"HEALTH: Metric saved to DB - User: {user_id}, Type: {metric_type}, Value: {value}")
        return metric
    
    def create_questionnaire(self, user_id: int, questionnaire_data: dict, version: str = "1.0") -> HealthQuestionnaire:
        """Create a new health questionnaire"""
        questionnaire = HealthQuestionnaire(
            user_id=user_id,
            questionnaire_data=questionnaire_data,
            version=version,
            completed_at=datetime.now()
        )
        
        self.db.add(questionnaire)
        self.db.commit()
        self.db.refresh(questionnaire)
        
        print(f"HEALTH: Questionnaire saved to DB - User: {user_id}, ID: {questionnaire.id}")
        return questionnaire
    
    def create_goal(self, user_id: int, goal_type: str, target_value: float, notes: str = "") -> HealthGoal:
        """Create a new health goal"""
        goal = HealthGoal(
            user_id=user_id,
            goal_type=goal_type,
            target_value=target_value,
            notes=notes
        )
        
        self.db.add(goal)
        self.db.commit()
        self.db.refresh(goal)
        
        print(f"HEALTH: Goal saved to DB - User: {user_id}, Type: {goal_type}, Target: {target_value}")
        return goal
    
    def get_health_metrics(self, user_id: int, metric_type: str = None, limit: int = 10) -> List[HealthMetric]:
        """Get health metrics for a user"""
        query = self.db.query(HealthMetric).filter(HealthMetric.user_id == user_id)
        
        if metric_type:
            query = query.filter(HealthMetric.metric_type == metric_type)
        
        return query.order_by(HealthMetric.measured_at.desc()).limit(limit).all()
    
    def get_questionnaires(self, user_id: int, limit: int = 5) -> List[HealthQuestionnaire]:
        """Get questionnaires for a user"""
        return (self.db.query(HealthQuestionnaire)
                .filter(HealthQuestionnaire.user_id == user_id)
                .order_by(HealthQuestionnaire.completed_at.desc())
                .limit(limit)
                .all())
    
    def get_active_goals(self, user_id: int) -> List[HealthGoal]:
        """Get active goals for a user"""
        return (self.db.query(HealthGoal)
                .filter(HealthGoal.user_id == user_id, HealthGoal.is_active == True)
                .order_by(HealthGoal.created_at.desc())
                .all())
    
    def update_goal_progress(self, goal_id: int, progress_value: float, notes: str = "") -> HealthProgress:
        """Update goal progress"""
        # First update the goal's current value
        goal = self.db.query(HealthGoal).filter(HealthGoal.id == goal_id).first()
        if goal:
            goal.current_value = progress_value
            goal.updated_at = datetime.now()
        
        # Create progress record
        progress = HealthProgress(
            user_id=goal.user_id if goal else 0,
            goal_id=goal_id,
            progress_value=progress_value,
            progress_date=datetime.now(),
            notes=notes
        )
        
        self.db.add(progress)
        self.db.commit()
        self.db.refresh(progress)
        
        print(f"HEALTH: Progress saved - Goal ID: {goal_id}, Value: {progress_value}")
        return progress
