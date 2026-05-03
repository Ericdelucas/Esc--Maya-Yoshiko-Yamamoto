from sqlalchemy.orm import Session
from sqlalchemy import func, and_
from datetime import datetime, timedelta
from typing import Optional

from app.models.orm.training_orm import PatientProgressORM, PatientPointsORM
from app.storage.database.base_repository import SessionLocal


class ProgressRepository:
    
    def get_or_create_progress(self, patient_id: int, session: Session = None) -> PatientProgressORM:
        """Busca ou cria progresso do paciente"""
        if session is None:
            with SessionLocal() as new_session:
                return self._get_or_create_progress_query(new_session, patient_id)
        else:
            return self._get_or_create_progress_query(session, patient_id)
    
    def _get_or_create_progress_query(self, session: Session, patient_id: int) -> PatientProgressORM:
        """Query interna para buscar ou criar progresso"""
        progress = session.query(PatientProgressORM).filter(
            PatientProgressORM.patient_id == patient_id
        ).first()
        
        if not progress:
            progress = PatientProgressORM(
                patient_id=patient_id,
                total_sessions=0,
                weekly_sessions=0,
                streak_days=0,
                progress_percentage=0.0,
                total_points=0,
                level=1
            )
            session.add(progress)
            session.commit()
            session.refresh(progress)
        
        return progress
    
    def get_or_create_points(self, patient_id: int, session: Session = None) -> PatientPointsORM:
        """Busca ou cria pontos do paciente"""
        if session is None:
            with SessionLocal() as new_session:
                return self._get_or_create_points_query(new_session, patient_id)
        else:
            return self._get_or_create_points_query(session, patient_id)
    
    def _get_or_create_points_query(self, session: Session, patient_id: int) -> PatientPointsORM:
        """Query interna para buscar ou criar pontos"""
        points = session.query(PatientPointsORM).filter(
            PatientPointsORM.patient_id == patient_id
        ).first()
        
        if not points:
            points = PatientPointsORM(
                patient_id=patient_id,
                total_points=0,
                weekly_points=0,
                monthly_points=0
            )
            session.add(points)
            session.commit()
            session.refresh(points)
        
        return points
    
    def update_progress(self, patient_id: int, **kwargs) -> PatientProgressORM:
        """Atualiza dados de progresso"""
        with SessionLocal() as session:
            progress = self.get_or_create_progress(patient_id, session)
            
            for key, value in kwargs.items():
                if hasattr(progress, key):
                    setattr(progress, key, value)
            
            progress.updated_at = datetime.utcnow()
            session.commit()
            session.refresh(progress)
            return progress
    
    def update_points(self, patient_id: int, points_to_add: int) -> PatientPointsORM:
        """Adiciona pontos ao paciente"""
        with SessionLocal() as session:
            points = self.get_or_create_points(patient_id, session)
            points.total_points += points_to_add
            points.weekly_points += points_to_add
            points.monthly_points += points_to_add
            points.last_updated = datetime.utcnow()
            session.commit()
            session.refresh(points)
            return points
    
    def calculate_weekly_sessions(self, patient_id: int) -> int:
        """Calcula sessões na última semana (baseado em dados existentes)"""
        # Simplificado - na implementação real, buscaria da tabela de sessões
        with SessionLocal() as session:
            progress = self.get_or_create_progress(patient_id, session)
            return progress.weekly_sessions
    
    def calculate_streak_days(self, patient_id: int) -> int:
        """Calcula streak de dias consecutivos"""
        with SessionLocal() as session:
            progress = self.get_or_create_progress(patient_id, session)
            return progress.streak_days
