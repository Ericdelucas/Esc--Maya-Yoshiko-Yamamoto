from sqlalchemy.orm import Session
from sqlalchemy import and_, func
from typing import List, Optional
from datetime import date, datetime

from app.storage.database.task_repository import TaskRepository
from app.models.schemas.task_schema import (
    TaskCreate, TaskUpdate, TaskOut, TaskCompletionCreate, 
    TaskCompletionOut, UserPointsOut, LeaderboardEntry, 
    PointsHistoryOut, GlobalChallengeOut, ChallengeParticipationOut,
    PatientTaskList, TaskStats
)
from app.storage.database.task_repository import TaskORM, TaskCompletionORM, UserPointsORM, PointsHistoryORM, GlobalChallengeORM, ChallengeParticipationORM


class TaskService:
    
    def __init__(self):
        self.repository = TaskRepository()
    
    # ============= TAREFAS =============
    
    def create_task(self, task_data: TaskCreate, professional_id: int, db: Session) -> TaskOut:
        """Criar nova tarefa"""
        db_task = self.repository.create_task(task_data, professional_id, db)
        return TaskOut.model_validate(db_task)
    
    def get_task_by_id(self, task_id: int, db: Session) -> Optional[TaskOut]:
        """Obter tarefa por ID"""
        db_task = self.repository.get_task_by_id(task_id, db)
        return TaskOut.model_validate(db_task) if db_task else None
    
    def get_tasks_by_patient(self, patient_id: int, db: Session, active_only: bool = True) -> List[TaskOut]:
        """Obter tarefas de um paciente"""
        db_tasks = self.repository.get_tasks_by_patient(patient_id, db, active_only)
        return [TaskOut.model_validate(task) for task in db_tasks]
    
    def get_tasks_by_professional(self, professional_id: int, db: Session) -> List[TaskOut]:
        """Obter tarefas criadas por um profissional"""
        db_tasks = self.repository.get_tasks_by_professional(professional_id, db)
        return [TaskOut.model_validate(task) for task in db_tasks]
    
    def update_task(self, task_id: int, task_data: TaskUpdate, db: Session) -> Optional[TaskOut]:
        """Atualizar tarefa"""
        db_task = self.repository.update_task(task_id, task_data, db)
        return TaskOut.model_validate(db_task) if db_task else None
    
    def delete_task(self, task_id: int, db: Session) -> bool:
        """Excluir tarefa (soft delete)"""
        return self.repository.delete_task(task_id, db)
    
    def complete_task(self, completion_data: TaskCompletionCreate, patient_id: int, db: Session) -> Optional[TaskCompletionOut]:
        """Marcar tarefa como concluída"""
        db_completion = self.repository.complete_task(completion_data, patient_id, db)
        return TaskCompletionOut.model_validate(db_completion) if db_completion else None
    
    def get_patient_task_list(self, patient_id: int, db: Session) -> PatientTaskList:
        """Obter lista completa de tarefas do paciente com estatísticas"""
        tasks = self.get_tasks_by_patient(patient_id, db, active_only=True)
        
        # Calcular pontos disponíveis
        total_points_available = sum(task.points_value for task in tasks)
        
        # Obter conclusões de hoje
        today = date.today()
        completed_today = len([
            comp for comp in self.repository.get_task_completions_by_patient(patient_id, db, days=1)
            if comp.completed_at.date() == today
        ])
        
        # Obter progresso semanal e mensal
        week_completions = len(self.repository.get_task_completions_by_patient(patient_id, db, days=7))
        month_completions = len(self.repository.get_task_completions_by_patient(patient_id, db, days=30))
        
        return PatientTaskList(
            tasks=tasks,
            total_points_available=total_points_available,
            completed_today=completed_today,
            weekly_progress=week_completions,
            monthly_progress=month_completions
        )
    
    # ============= PONTOS E RANKING =============
    
    def get_user_points(self, user_id: int, db: Session) -> Optional[UserPointsOut]:
        """Obter pontos do usuário"""
        db_points = self.repository.get_user_points(user_id, db)
        return UserPointsOut.model_validate(db_points) if db_points else None
    
    def get_leaderboard(self, db: Session, limit: int = 50) -> List[LeaderboardEntry]:
        """Obter ranking de usuários"""
        # Obter dados do ranking com informações do usuário
        from app.storage.database.user_repository import UserRepository
        user_repo = UserRepository()
        
        leaderboard_data = []
        rank_entries = self.repository.get_leaderboard(db, limit)
        
        for rank, user_points in enumerate(rank_entries, start=1):
            # Obter informações do usuário
            user = user_repo.get_by_id(user_points.user_id, db)
            if user:
                entry = LeaderboardEntry(
                    user_id=user_points.user_id,
                    user_name=user.full_name or user.email.split("@")[0],
                    user_email=user.email,
                    total_points=user_points.total_points,
                    rank_position=rank,
                    current_streak=user_points.current_streak
                )
                leaderboard_data.append(entry)
        
        return leaderboard_data
    
    def get_points_history(self, user_id: int, db: Session, limit: int = 50) -> List[PointsHistoryOut]:
        """Obter histórico de pontos de um usuário"""
        db_history = self.repository.get_points_history_by_user(user_id, db, limit)
        return [PointsHistoryOut.model_validate(entry) for entry in db_history]
    
    def add_bonus_points(self, user_id: int, points: int, description: str, db: Session) -> bool:
        """Adicionar pontos de bônus (para admin)"""
        try:
            self.repository._update_user_points(user_id, points, db)
            self.repository._add_points_history(user_id, points, "bonus", None, description, db)
            db.commit()
            return True
        except Exception:
            db.rollback()
            return False
    
    # ============= DESAFIOS GLOBAIS =============
    
    def get_global_challenges(self, db: Session, active_only: bool = True) -> List[GlobalChallengeOut]:
        """Obter desafios globais"""
        db_challenges = self.repository.get_global_challenges(db, active_only)
        return [GlobalChallengeOut.model_validate(challenge) for challenge in db_challenges]
    
    def join_challenge(self, user_id: int, challenge_id: int, db: Session) -> Optional[ChallengeParticipationOut]:
        """Participar de um desafio global"""
        db_participation = self.repository.join_challenge(user_id, challenge_id, db)
        return ChallengeParticipationOut.model_validate(db_participation) if db_participation else None
    
    def get_user_challenges(self, user_id: int, db: Session) -> List[ChallengeParticipationOut]:
        """Obter participações em desafios do usuário"""
        db_participations = self.repository.get_user_challenge_participations(user_id, db)
        return [ChallengeParticipationOut.model_validate(participation) for participation in db_participations]
    
    # ============= ESTATÍSTICAS =============
    
    def get_task_stats(self, db: Session, professional_id: Optional[int] = None, patient_id: Optional[int] = None) -> TaskStats:
        """Obter estatísticas de tarefas"""
        stats = self.repository.get_task_stats(professional_id, patient_id, db)
        
        # Calcular taxas de conclusão
        if patient_id and stats["total_tasks"] > 0:
            weekly_rate = (stats["completed_this_week"] / stats["total_tasks"]) * 100
            monthly_rate = (stats["completed_this_month"] / stats["total_tasks"]) * 100
        else:
            weekly_rate = 0.0
            monthly_rate = 0.0
        
        return TaskStats(
            total_tasks=stats["total_tasks"],
            completed_tasks=stats["completed_this_month"],  # Usar mensal como proxy
            pending_tasks=stats["active_tasks"],
            total_points_earned=stats["completed_this_month"] * 10,  # Estimativa
            weekly_completion_rate=weekly_rate,
            monthly_completion_rate=monthly_rate
        )
    
    # ============= MÉTODOS AUXILIARES =============
    
    def can_complete_task_today(self, task_id: int, patient_id: int, db: Session) -> bool:
        """Verificar se o paciente pode completar a tarefa hoje"""
        task = self.repository.get_task_by_id(task_id, db)
        if not task or task.patient_id != patient_id or not task.is_active:
            return False
        
        # Verificar se já foi completada hoje
        today = date.today()
        existing = db.query(TaskCompletionORM).filter(
            and_(
                TaskCompletionORM.task_id == task_id,
                TaskCompletionORM.patient_id == patient_id,
                func.date(TaskCompletionORM.completed_at) == today
            )
        ).first()
        
        return existing is None
    
    def get_daily_tasks_for_patient(self, patient_id: int, db: Session) -> List[TaskOut]:
        """Obter tarefas que podem ser completadas hoje"""
        all_tasks = self.get_tasks_by_patient(patient_id, db, active_only=True)
        today = date.today()
        
        daily_tasks = []
        for task in all_tasks:
            # Verificar se a tarefa está vigente
            if task.start_date <= today and (task.end_date is None or task.end_date >= today):
                # Verificar se ainda não foi completada hoje
                if self.can_complete_task_today(task.id, patient_id, db):
                    daily_tasks.append(task)
        
        return daily_tasks
