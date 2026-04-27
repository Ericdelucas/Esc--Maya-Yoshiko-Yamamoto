from datetime import datetime
from sqlalchemy.orm import Session
from sqlalchemy import desc
from app.storage.database.health_tools_repository import HealthToolsRepository
from app.models.schemas.health_tools_schema import HealthToolsSummary
from app.models.orm.health_tools_orm import HealthToolsORM, HealthQuestionnaireORM
import json


class HealthToolsService:
    
    def __init__(self):
        pass

    def calculate_bmi(self, user_id: int, height: float, weight: float, db: Session) -> dict:
        """Calcular e salvar IMC"""
        repository = HealthToolsRepository(db)
        return repository.create_bmi_record(user_id, height, weight)

    def calculate_body_fat(self, user_id: int, height: float, weight: float, age: int, gender: str, db: Session) -> dict:
        """Calcular e salvar gordura corporal"""
        repository = HealthToolsRepository(db)
        return repository.create_body_fat_record(user_id, height, weight, age, gender)

    def save_questionnaire(self, user_id: int, answers: dict, db: Session) -> dict:
        """Salvar questionário de saúde"""
        repository = HealthToolsRepository(db)
        return repository.create_questionnaire_record(user_id, answers, db)

    def get_health_history(self, user_id: int, db: Session, limit: int = 50) -> list:
        """Buscar histórico completo de saúde"""
        repository = HealthToolsRepository(db)
        return repository.get_user_history(user_id, limit)

    def get_health_summary(self, user_id: int, db: Session) -> HealthToolsSummary:
        """Buscar resumo completo das ferramentas de saúde"""
        repository = HealthToolsRepository(db)
        summary_data = repository.get_health_summary(user_id)
        
        return HealthToolsSummary(
            latest_bmi=summary_data.get("latest_bmi"),
            latest_body_fat=summary_data.get("latest_body_fat"),
            latest_questionnaire=summary_data.get("latest_questionnaire"),
            total_records=summary_data.get("total_records", 0),
            last_updated=summary_data.get("last_updated", datetime.utcnow())
        )

    def get_bmi_history(self, user_id: int, db: Session, limit: int = 10) -> list:
        """Buscar histórico específico de IMC"""
        records = (
            db.query(HealthToolsORM)
            .filter(HealthToolsORM.user_id == user_id, HealthToolsORM.record_type == "bmi")
            .order_by(desc(HealthToolsORM.created_at))
            .limit(limit)
            .all()
        )
        
        history = []
        for record in records:
            value_data = json.loads(record.value) if isinstance(record.value, str) else record.value
            history.append({
                "id": record.id,
                **value_data,
                "created_at": record.created_at
            })
        
        return history

    def get_body_fat_history(self, user_id: int, db: Session, limit: int = 10) -> list:
        """Buscar histórico específico de gordura corporal"""
        records = (
            db.query(HealthToolsORM)
            .filter(HealthToolsORM.user_id == user_id, HealthToolsORM.record_type == "body_fat")
            .order_by(desc(HealthToolsORM.created_at))
            .limit(limit)
            .all()
        )
        
        history = []
        for record in records:
            value_data = json.loads(record.value) if isinstance(record.value, str) else record.value
            history.append({
                "id": record.id,
                **value_data,
                "created_at": record.created_at
            })
        
        return history

    def get_questionnaire_history(self, user_id: int, db: Session, limit: int = 10) -> list:
        """Buscar histórico específico de questionários"""
        records = (
            db.query(HealthQuestionnaireORM)
            .filter(HealthQuestionnaireORM.user_id == user_id)
            .order_by(desc(HealthQuestionnaireORM.created_at))
            .limit(limit)
            .all()
        )
        
        history = []
        for record in records:
            answers_data = json.loads(record.answers) if isinstance(record.answers, str) else record.answers
            history.append({
                "id": record.id,
                "total_score": record.total_score,
                "max_score": record.max_score,
                "risk_level": record.risk_level,
                "answers": answers_data,
                "created_at": record.created_at
            })
        
        return history
