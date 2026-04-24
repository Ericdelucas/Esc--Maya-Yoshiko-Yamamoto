from sqlalchemy.orm import Session
from sqlalchemy import and_, or_, desc, func, extract
from datetime import date, datetime, timedelta
from typing import List, Optional

from app.models.orm.task_orm import TaskORM, TaskCompletionORM, UserPointsORM, PointsHistoryORM, GlobalChallengeORM, ChallengeParticipationORM
from app.models.schemas.task_schema import TaskCreate, TaskUpdate, TaskCompletionCreate


class TaskRepository:
    
    def create_task(self, task_data: TaskCreate, professional_id: int, db: Session) -> TaskORM:
        """Criar nova tarefa"""
        db_task = TaskORM(
            professional_id=professional_id,
            **task_data.model_dump()
        )
        db.add(db_task)
        db.commit()
        db.refresh(db_task)
        return db_task
    
    def get_task_by_id(self, task_id: int, db: Session) -> Optional[TaskORM]:
        """Obter tarefa por ID"""
        return db.query(TaskORM).filter(TaskORM.id == task_id).first()
    
    def get_tasks_by_patient(self, patient_id: int, db: Session, active_only: bool = True) -> List[TaskORM]:
        """Obter tarefas de um paciente"""
        query = db.query(TaskORM).filter(TaskORM.patient_id == patient_id)
        
        if active_only:
            query = query.filter(
                and_(
                    TaskORM.is_active == True,
                    or_(
                        TaskORM.end_date.is_(None),
                        TaskORM.end_date >= date.today()
                    )
                )
            )
        
        return query.order_by(TaskORM.created_at.desc()).all()
    
    def get_tasks_by_professional(self, professional_id: int, db: Session) -> List[TaskORM]:
        """Obter tarefas criadas por um profissional"""
        return db.query(TaskORM).filter(TaskORM.professional_id == professional_id).order_by(TaskORM.created_at.desc()).all()
    
    def update_task(self, task_id: int, task_data: TaskUpdate, db: Session) -> Optional[TaskORM]:
        """Atualizar tarefa"""
        db_task = self.get_task_by_id(task_id, db)
        if not db_task:
            return None
        
        update_data = task_data.model_dump(exclude_unset=True)
        for field, value in update_data.items():
            setattr(db_task, field, value)
        
        db_task.updated_at = datetime.utcnow()
        db.commit()
        db.refresh(db_task)
        return db_task
    
    def delete_task(self, task_id: int, db: Session) -> bool:
        """Excluir tarefa (soft delete - desativar)"""
        db_task = self.get_task_by_id(task_id, db)
        if not db_task:
            return False
        
        db_task.is_active = False
        db_task.updated_at = datetime.utcnow()
        db.commit()
        return True
    
    def complete_task(self, completion_data: TaskCompletionCreate, patient_id: int, db: Session) -> Optional[TaskCompletionORM]:
        """Marcar tarefa como concluída"""
        # Verificar se a tarefa existe e pertence ao paciente
        task = db.query(TaskORM).filter(
            and_(
                TaskORM.id == completion_data.task_id,
                TaskORM.patient_id == patient_id,
                TaskORM.is_active == True
            )
        ).first()
        
        if not task:
            return None
        
        # Verificar se já foi concluída hoje
        today = date.today()
        existing_completion = db.query(TaskCompletionORM).filter(
            and_(
                TaskCompletionORM.task_id == completion_data.task_id,
                TaskCompletionORM.patient_id == patient_id,
                func.date(TaskCompletionORM.completed_at) == today
            )
        ).first()
        
        if existing_completion:
            return None  # Já concluída hoje
        
        # Criar registro de conclusão
        db_completion = TaskCompletionORM(
            task_id=completion_data.task_id,
            patient_id=patient_id,
            points_earned=task.points_value,
            completion_notes=completion_data.completion_notes
        )
        
        db.add(db_completion)
        
        # Atualizar pontos do usuário
        self._update_user_points(patient_id, task.points_value, db)
        
        # Adicionar ao histórico
        self._add_points_history(
            patient_id, 
            task.points_value, 
            "task_completion", 
            task.id, 
            f"Tarefa concluída: {task.title}",
            db
        )
        
        db.commit()
        db.refresh(db_completion)
        return db_completion
    
    def _update_user_points(self, user_id: int, points_to_add: int, db: Session):
        """Atualizar pontos do usuário"""
        user_points = db.query(UserPointsORM).filter(UserPointsORM.user_id == user_id).first()
        
        if not user_points:
            # Criar registro se não existir
            user_points = UserPointsORM(
                user_id=user_id,
                total_points=points_to_add,
                weekly_points=points_to_add,
                monthly_points=points_to_add,
                current_streak=1,
                longest_streak=1,
                last_completion_date=date.today()
            )
            db.add(user_points)
        else:
            # Atualizar pontos existentes
            user_points.total_points += points_to_add
            user_points.weekly_points += points_to_add
            user_points.monthly_points += points_to_add
            
            # Atualizar streak
            today = date.today()
            if user_points.last_completion_date:
                yesterday = today - timedelta(days=1)
                if user_points.last_completion_date == yesterday:
                    user_points.current_streak += 1
                    if user_points.current_streak > user_points.longest_streak:
                        user_points.longest_streak = user_points.current_streak
                elif user_points.last_completion_date < yesterday:
                    user_points.current_streak = 1
            else:
                user_points.current_streak = 1
            
            user_points.last_completion_date = today
            user_points.updated_at = datetime.utcnow()
        
        # Atualizar ranking
        self._update_ranking(db)
    
    def _add_points_history(self, user_id: int, points_change: int, change_type: str, reference_id: Optional[int], description: str, db: Session):
        """Adicionar registro ao histórico de pontos"""
        history = PointsHistoryORM(
            user_id=user_id,
            points_change=points_change,
            change_type=change_type,
            reference_id=reference_id,
            description=description
        )
        db.add(history)
    
    def _update_ranking(self, db: Session):
        """Atualizar posições no ranking"""
        # Obter todos os usuários com pontos, ordenados por pontos totais
        users_with_points = db.query(UserPointsORM).filter(UserPointsORM.total_points > 0).order_by(desc(UserPointsORM.total_points)).all()
        
        for rank, user_points in enumerate(users_with_points, start=1):
            user_points.rank_position = rank
    
    def get_user_points(self, user_id: int, db: Session) -> Optional[UserPointsORM]:
        """Obter pontos do usuário"""
        return db.query(UserPointsORM).filter(UserPointsORM.user_id == user_id).first()
    
    def get_leaderboard(self, db: Session, limit: int = 50) -> List[UserPointsORM]:
        """Obter ranking de usuários"""
        return db.query(UserPointsORM).filter(UserPointsORM.total_points > 0).order_by(desc(UserPointsORM.total_points)).limit(limit).all()
    
    def get_task_completions_by_patient(self, patient_id: int, db: Session, days: int = 30) -> List[TaskCompletionORM]:
        """Obter conclusões de tarefas de um paciente"""
        since_date = datetime.utcnow() - timedelta(days=days)
        return db.query(TaskCompletionORM).filter(
            and_(
                TaskCompletionORM.patient_id == patient_id,
                TaskCompletionORM.completed_at >= since_date
            )
        ).order_by(desc(TaskCompletionORM.completed_at)).all()
    
    def get_points_history_by_user(self, user_id: int, db: Session, limit: int = 50) -> List[PointsHistoryORM]:
        """Obter histórico de pontos de um usuário"""
        return db.query(PointsHistoryORM).filter(
            PointsHistoryORM.user_id == user_id
        ).order_by(desc(PointsHistoryORM.created_at)).limit(limit).all()
    
    def get_global_challenges(self, db: Session, active_only: bool = True) -> List[GlobalChallengeORM]:
        """Obter desafios globais"""
        query = db.query(GlobalChallengeORM)
        
        if active_only:
            query = query.filter(
                and_(
                    GlobalChallengeORM.is_active == True,
                    GlobalChallengeORM.start_date <= date.today(),
                    GlobalChallengeORM.end_date >= date.today()
                )
            )
        
        return query.order_by(GlobalChallengeORM.created_at.desc()).all()
    
    def join_challenge(self, user_id: int, challenge_id: int, db: Session) -> Optional[ChallengeParticipationORM]:
        """Participar de um desafio global"""
        # Verificar se o desafio existe e está ativo
        challenge = db.query(GlobalChallengeORM).filter(
            and_(
                GlobalChallengeORM.id == challenge_id,
                GlobalChallengeORM.is_active == True,
                GlobalChallengeORM.start_date <= date.today(),
                GlobalChallengeORM.end_date >= date.today()
            )
        ).first()
        
        if not challenge:
            return None
        
        # Verificar se já participa
        existing = db.query(ChallengeParticipationORM).filter(
            and_(
                ChallengeParticipationORM.challenge_id == challenge_id,
                ChallengeParticipationORM.user_id == user_id
            )
        ).first()
        
        if existing:
            return existing
        
        # Criar participação
        participation = ChallengeParticipationORM(
            challenge_id=challenge_id,
            user_id=user_id
        )
        
        db.add(participation)
        db.commit()
        db.refresh(participation)
        return participation
    
    def get_user_challenge_participations(self, user_id: int, db: Session) -> List[ChallengeParticipationORM]:
        """Obter participações em desafios de um usuário"""
        return db.query(ChallengeParticipationORM).filter(
            ChallengeParticipationORM.user_id == user_id
        ).order_by(desc(ChallengeParticipationORM.joined_at)).all()
    
    def get_task_stats(self, db: Session, professional_id: Optional[int] = None, patient_id: Optional[int] = None) -> dict:
        """Obter estatísticas de tarefas"""
        query = db.query(TaskORM)
        
        if professional_id:
            query = query.filter(TaskORM.professional_id == professional_id)
        if patient_id:
            query = query.filter(TaskORM.patient_id == patient_id)
        
        total_tasks = query.count()
        active_tasks = query.filter(TaskORM.is_active == True).count()
        
        # Estatísticas de conclusão
        if patient_id:
            completions_query = db.query(TaskCompletionORM).filter(TaskCompletionORM.patient_id == patient_id)
            completed_this_week = completions_query.filter(
                TaskCompletionORM.completed_at >= datetime.utcnow() - timedelta(days=7)
            ).count()
            completed_this_month = completions_query.filter(
                TaskCompletionORM.completed_at >= datetime.utcnow() - timedelta(days=30)
            ).count()
        else:
            completed_this_week = 0
            completed_this_month = 0
        
        return {
            "total_tasks": total_tasks,
            "active_tasks": active_tasks,
            "completed_this_week": completed_this_week,
            "completed_this_month": completed_this_month
        }
