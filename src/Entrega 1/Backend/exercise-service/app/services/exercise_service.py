from sqlalchemy.orm import Session
from app.models.schemas.exercise_schema import ExerciseCreate, ExerciseOut
from app.storage.database.exercise_repository import ExerciseRepository


class ExerciseService:
    def create(self, payload: ExerciseCreate, user_id: int, db: Session) -> ExerciseOut:
        tags_csv = ",".join([t.strip() for t in payload.tags if t.strip()])
        row = ExerciseRepository(db).create(
            title=payload.title,
            description=payload.description,
            instructions=payload.instructions,
            tags_csv=tags_csv,
            image_path=payload.image_path,
            video_path=payload.video_path,
            created_by=user_id
        )
        return self._to_out(row)

    def list_recent(self, db: Session, limit: int = 50) -> list[ExerciseOut]:
        rows = ExerciseRepository(db).list_recent(limit=limit)
        return [self._to_out(r) for r in rows]

    def get_by_id(self, exercise_id: int, db: Session) -> ExerciseOut:
        row = ExerciseRepository(db).get_by_id(exercise_id)
        if not row:
            raise ValueError("Exercise not found")
        return self._to_out(row)

    def _to_out(self, row) -> ExerciseOut:
        tags = [t for t in (row.tags_csv or "").split(",") if t]
        return ExerciseOut(
            id=row.id,
            title=row.title,
            description=row.description,
            instructions=row.instructions,
            tags=tags,
            image_url=f"/media/images/{row.image_path.split('/')[-1]}" if row.image_path else None,
            video_url=f"/media/videos/{row.video_path.split('/')[-1]}" if row.video_path else None,
            created_by=row.created_by,
            created_at_iso=row.created_at.isoformat(),
        )
