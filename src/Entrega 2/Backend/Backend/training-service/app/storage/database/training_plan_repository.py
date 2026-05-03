from datetime import date
from sqlalchemy.orm import Session
from app.models.orm.training_plan_orm import TrainingPlanORM


class TrainingPlanRepository:
    def __init__(self, db: Session) -> None:
        self._db = db

    def create(self, patient_id: int, professional_id: int, title: str, start: date, end: date | None) -> TrainingPlanORM:
        row = TrainingPlanORM(patient_id=patient_id, professional_id=professional_id, title=title, start_date=start, end_date=end)
        self._db.add(row)
        self._db.commit()
        self._db.refresh(row)
        return row

    def get_by_id(self, plan_id: int) -> TrainingPlanORM | None:
        return self._db.query(TrainingPlanORM).filter(TrainingPlanORM.id == plan_id).first()

    def list_by_patient(self, patient_id: int, limit: int = 50) -> list[TrainingPlanORM]:
        return (
            self._db.query(TrainingPlanORM)
            .filter(TrainingPlanORM.patient_id == patient_id)
            .order_by(TrainingPlanORM.id.desc())
            .limit(limit)
            .all()
        )
