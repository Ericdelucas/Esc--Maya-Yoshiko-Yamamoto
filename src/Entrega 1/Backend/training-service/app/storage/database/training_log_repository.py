from sqlalchemy.orm import Session
from app.models.orm.training_log_orm import TrainingLogORM


class TrainingLogRepository:
    def __init__(self, db: Session) -> None:
        self._db = db

    def create(self, patient_id: int, plan_id: int, exercise_id: int, effort: int | None, pain: int | None, notes: str | None) -> TrainingLogORM:
        row = TrainingLogORM(patient_id=patient_id, plan_id=plan_id, exercise_id=exercise_id, perceived_effort=effort, pain_level=pain, notes=notes)
        self._db.add(row)
        self._db.commit()
        self._db.refresh(row)
        return row

    def list_by_patient(self, patient_id: int, limit: int = 100) -> list[TrainingLogORM]:
        return (
            self._db.query(TrainingLogORM)
            .filter(TrainingLogORM.patient_id == patient_id)
            .order_by(TrainingLogORM.id.desc())
            .limit(limit)
            .all()
        )
