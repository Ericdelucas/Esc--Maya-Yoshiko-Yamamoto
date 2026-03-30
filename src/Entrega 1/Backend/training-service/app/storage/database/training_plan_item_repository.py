from sqlalchemy.orm import Session
from app.models.orm.training_plan_item_orm import TrainingPlanItemORM


class TrainingPlanItemRepository:
    def __init__(self, db: Session) -> None:
        self._db = db

    def add_item(self, plan_id: int, exercise_id: int, sets: int, reps: int, freq: int, notes: str | None) -> TrainingPlanItemORM:
        row = TrainingPlanItemORM(plan_id=plan_id, exercise_id=exercise_id, sets=sets, reps=reps, frequency_per_week=freq, notes=notes)
        self._db.add(row)
        self._db.commit()
        self._db.refresh(row)
        return row
