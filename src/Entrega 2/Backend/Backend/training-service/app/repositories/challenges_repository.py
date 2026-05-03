from sqlalchemy.orm import Session
from sqlalchemy import and_, or_
from datetime import date
from typing import List, Optional, Dict

from app.models.orm.training_orm import ChallengeORM, PatientChallengeORM
from app.storage.database.base_repository import SessionLocal


class ChallengesRepository:
    
    def get_active_challenges(self, current_date: date = None) -> List[ChallengeORM]:
        """Retorna desafios ativos na data atual"""
        if current_date is None:
            current_date = date.today()
            
        with SessionLocal() as session:
            return session.query(ChallengeORM).filter(
                and_(
                    ChallengeORM.is_active == True,
                    ChallengeORM.start_date <= current_date,
                    ChallengeORM.end_date >= current_date
                )
            ).all()
    
    def get_patient_challenges(self, patient_id: int, current_date: date = None) -> List[Dict]:
        """Retorna desafios com progresso do paciente"""
        if current_date is None:
            current_date = date.today()
            
        with SessionLocal() as session:
            # Buscar desafios ativos
            active_challenges = self.get_active_challenges(current_date)
            
            result = []
            for challenge in active_challenges:
                # Buscar progresso do paciente neste desafio
                patient_challenge = session.query(PatientChallengeORM).filter(
                    and_(
                        PatientChallengeORM.patient_id == patient_id,
                        PatientChallengeORM.challenge_id == challenge.id
                    )
                ).first()
                
                # Determinar status
                if patient_challenge and patient_challenge.completed:
                    status = "completed"
                elif current_date > challenge.end_date:
                    status = "expired"
                elif patient_challenge and patient_challenge.joined:
                    status = "in_progress"
                else:
                    status = "active"
                
                result.append({
                    "id": challenge.id,
                    "title": challenge.title,
                    "description": challenge.description,
                    "reward_points": challenge.reward_points,
                    "start_date": challenge.start_date,
                    "end_date": challenge.end_date,
                    "joined": patient_challenge.joined if patient_challenge else False,
                    "progress": patient_challenge.progress_sessions if patient_challenge else 0,
                    "target_sessions": challenge.target_sessions,
                    "status": status
                })
            
            return result
    
    def join_challenge(self, patient_id: int, challenge_id: int) -> PatientChallengeORM:
        """Paciente entra em um desafio"""
        with SessionLocal() as session:
            # Verificar se já existe
            existing = session.query(PatientChallengeORM).filter(
                and_(
                    PatientChallengeORM.patient_id == patient_id,
                    PatientChallengeORM.challenge_id == challenge_id
                )
            ).first()
            
            if existing:
                existing.joined = True
                existing.updated_at = session.execute("SELECT NOW()").scalar()
                session.commit()
                session.refresh(existing)
                return existing
            
            # Criar novo registro
            patient_challenge = PatientChallengeORM(
                patient_id=patient_id,
                challenge_id=challenge_id,
                joined=True,
                progress_sessions=0,
                completed=False
            )
            session.add(patient_challenge)
            session.commit()
            session.refresh(patient_challenge)
            return patient_challenge
    
    def update_challenge_progress(self, patient_id: int, challenge_id: int, sessions_to_add: int = 1) -> PatientChallengeORM:
        """Atualiza progresso do paciente em um desafio"""
        with SessionLocal() as session:
            patient_challenge = session.query(PatientChallengeORM).filter(
                and_(
                    PatientChallengeORM.patient_id == patient_id,
                    PatientChallengeORM.challenge_id == challenge_id
                )
            ).first()
            
            if not patient_challenge:
                raise ValueError("Patient not joined this challenge")
            
            patient_challenge.progress_sessions += sessions_to_add
            
            # Verificar se completou
            challenge = session.query(ChallengeORM).filter(
                ChallengeORM.id == challenge_id
            ).first()
            
            if patient_challenge.progress_sessions >= challenge.target_sessions:
                patient_challenge.completed = True
                patient_challenge.completed_at = session.execute("SELECT NOW()").scalar()
            
            patient_challenge.updated_at = session.execute("SELECT NOW()").scalar()
            session.commit()
            session.refresh(patient_challenge)
            return patient_challenge
