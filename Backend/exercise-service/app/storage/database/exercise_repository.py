from sqlalchemy.orm import Session
from app.models.orm.exercise_orm import ExerciseORM


class ExerciseRepository:
    def __init__(self, db: Session) -> None:
        self._db = db

    def create(self, title: str, description: str, instructions: str = None, 
               tags_csv: str = "", image_path: str = None, video_path: str = None, 
               created_by: int = None) -> ExerciseORM:
        row = ExerciseORM(
            title=title,
            description=description,
            instructions=instructions,
            tags_csv=tags_csv,
            image_path=image_path,
            video_path=video_path,
            created_by=created_by
        )
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
