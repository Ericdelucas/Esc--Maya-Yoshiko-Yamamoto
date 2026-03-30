from datetime import datetime, timedelta
from typing import Dict

from app.repositories.progress_repository import ProgressRepository
from app.models.schemas.progress_schema import ProgressResponse


class ProgressService:
    
    def __init__(self):
        self.progress_repo = ProgressRepository()
    
    def get_patient_progress(self, patient_id: int) -> ProgressResponse:
        """Retorna progresso completo do paciente"""
        # Buscar ou criar progresso
        progress = self.progress_repo.get_or_create_progress(patient_id)
        points = self.progress_repo.get_or_create_points(patient_id)
        
        # Calcular valores atualizados
        weekly_sessions = self.progress_repo.calculate_weekly_sessions(patient_id)
        streak_days = self.progress_repo.calculate_streak_days(patient_id)
        
        # Calcular nível baseado em pontos
        level = self._calculate_level(points.total_points)
        
        # Calcular percentual de progresso (simplificado)
        progress_percentage = self._calculate_progress_percentage(progress.total_sessions)
        
        # Atualizar valores se necessário
        if (progress.weekly_sessions != weekly_sessions or 
            progress.streak_days != streak_days or
            progress.total_points != points.total_points or
            progress.level != level):
            
            self.progress_repo.update_progress(
                patient_id,
                weekly_sessions=weekly_sessions,
                streak_days=streak_days,
                total_points=points.total_points,
                level=level,
                progress_percentage=progress_percentage,
                last_session_date=datetime.utcnow()
            )
        
        return ProgressResponse(
            total_sessions=progress.total_sessions,
            weekly_sessions=weekly_sessions,
            streak_days=streak_days,
            progress_percentage=progress_percentage,
            total_points=points.total_points,
            level=level
        )
    
    def _calculate_level(self, total_points: int) -> int:
        """Calcula nível baseado em pontos"""
        # Simplificado: 100 pontos por nível
        return (total_points // 100) + 1
    
    def _calculate_progress_percentage(self, total_sessions: int) -> float:
        """Calcula percentual de progresso (meta: 100 sessões)"""
        target_sessions = 100
        return min((total_sessions / target_sessions) * 100, 100.0)
    
    def add_session_points(self, patient_id: int, session_points: int = 10) -> Dict:
        """Adiciona pontos por sessão completada"""
        # Atualizar pontos
        points = self.progress_repo.update_points(patient_id, session_points)
        
        # Atualizar sessões
        progress = self.progress_repo.get_or_create_progress(patient_id)
        new_total_sessions = progress.total_sessions + 1
        new_weekly_sessions = progress.weekly_sessions + 1
        
        # Recalcular streak (simplificado - assume sessão hoje)
        new_streak_days = self._update_streak(progress.streak_days, progress.last_session_date)
        
        # Atualizar progresso
        self.progress_repo.update_progress(
            patient_id,
            total_sessions=new_total_sessions,
            weekly_sessions=new_weekly_sessions,
            streak_days=new_streak_days,
            last_session_date=datetime.utcnow()
        )
        
        return {
            "points_added": session_points,
            "total_points": points.total_points,
            "sessions_completed": new_total_sessions,
            "streak_days": new_streak_days
        }
    
    def _update_streak(self, current_streak: int, last_session_date: datetime) -> int:
        """Atualiza streak baseado na última sessão"""
        if not last_session_date:
            return 1
        
        today = datetime.now().date()
        last_date = last_session_date.date()
        
        # Se a última sessão foi ontem, mantém streak
        if today - last_date == timedelta(days=1):
            return current_streak + 1
        # Se foi hoje, mantém streak
        elif today == last_date:
            return current_streak
        # Se há mais de 1 dia, reseta streak
        else:
            return 1
