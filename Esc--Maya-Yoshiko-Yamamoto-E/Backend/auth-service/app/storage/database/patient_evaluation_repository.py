from typing import List, Optional

from sqlalchemy.orm import Session
from sqlalchemy import select, and_

from app.models.orm.patient_evaluation_orm import PatientEvaluationORM
from app.storage.database.db import get_session


class PatientEvaluationRepository:
    
    def save(self, evaluation: PatientEvaluationORM, session: Session = None) -> PatientEvaluationORM:
        if session is None:
            with get_session() as session:
                return self._save(evaluation, session)
        else:
            return self._save(evaluation, session)
    
    def _save(self, evaluation: PatientEvaluationORM, session: Session) -> PatientEvaluationORM:
        try:
            if evaluation.id is None:
                session.add(evaluation)
            else:
                session.merge(evaluation)
            session.commit()
            session.refresh(evaluation)
            return evaluation
        except Exception as e:
            session.rollback()
            raise e
    
    def findById(self, evaluation_id: int, session: Session = None) -> Optional[PatientEvaluationORM]:
        if session is None:
            with get_session() as session:
                return self._findById(evaluation_id, session)
        else:
            return self._findById(evaluation_id, session)
    
    def _findById(self, evaluation_id: int, session: Session) -> Optional[PatientEvaluationORM]:
        stmt = select(PatientEvaluationORM).where(PatientEvaluationORM.id == evaluation_id)
        result = session.execute(stmt).scalar_one_or_none()
        return result
    
    def findByPatientId(self, patient_id: int, session: Session = None) -> List[PatientEvaluationORM]:
        if session is None:
            with get_session() as session:
                return self._findByPatientId(patient_id, session)
        else:
            return self._findByPatientId(patient_id, session)
    
    def _findByPatientId(self, patient_id: int, session: Session) -> List[PatientEvaluationORM]:
        stmt = (
            select(PatientEvaluationORM)
            .where(PatientEvaluationORM.patient_id == patient_id)
            .order_by(PatientEvaluationORM.evaluation_date.desc())
        )
        result = session.execute(stmt).scalars().all()
        return list(result)
    
    def findByProfessionalId(self, professional_id: int, session: Session = None) -> List[PatientEvaluationORM]:
        if session is None:
            with get_session() as session:
                return self._findByProfessionalId(professional_id, session)
        else:
            return self._findByProfessionalId(professional_id, session)
    
    def _findByProfessionalId(self, professional_id: int, session: Session) -> List[PatientEvaluationORM]:
        stmt = (
            select(PatientEvaluationORM)
            .where(PatientEvaluationORM.professional_id == professional_id)
            .order_by(PatientEvaluationORM.evaluation_date.desc())
        )
        result = session.execute(stmt).scalars().all()
        return list(result)
    
    def findAll(self, session: Session = None) -> List[PatientEvaluationORM]:
        if session is None:
            with get_session() as session:
                return self._findAll(session)
        else:
            return self._findAll(session)
    
    def _findAll(self, session: Session) -> List[PatientEvaluationORM]:
        stmt = select(PatientEvaluationORM).order_by(PatientEvaluationORM.evaluation_date.desc())
        result = session.execute(stmt).scalars().all()
        return list(result)
    
    def deleteById(self, evaluation_id: int, session: Session = None) -> bool:
        if session is None:
            with get_session() as session:
                return self._deleteById(evaluation_id, session)
        else:
            return self._deleteById(evaluation_id, session)
    
    def _deleteById(self, evaluation_id: int, session: Session) -> bool:
        try:
            evaluation = self._findById(evaluation_id, session)
            if evaluation:
                session.delete(evaluation)
                session.commit()
                return True
            return False
        except Exception as e:
            session.rollback()
            raise e
