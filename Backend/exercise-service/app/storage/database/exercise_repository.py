from sqlalchemy.orm import Session
from app.models.orm.exercise_orm import ExerciseORM


class ExerciseRepository:
    def __init__(self, db: Session) -> None:
        self._db = db

    def create(self, title: str, description: str, tags_csv: str) -> ExerciseORM:
        row = ExerciseORM(title=title, description=description, tags_csv=tags_csv)
        self._db.add(row)
        self._db.commit()
        self._db.refresh(row)
        return row

    def get_by_id(self, exercise_id: int) -> ExerciseORM | None:
        return self._db.query(ExerciseORM).filter(ExerciseORM.id == exercise_id).first()

    def list_recent(self, limit: int = 50) -> list[ExerciseORM]:
        return (
            self._db.query(ExerciseORM)
            .order_by(ExerciseORM.id.desc())
            .limit(limit)
            .all()
        )

    def set_media_path(self, exercise_id: int, media_path: str) -> None:
        row = self.get_by_id(exercise_id)
        if not row:
            return
        row.media_path = media_path
        self._db.commit()
