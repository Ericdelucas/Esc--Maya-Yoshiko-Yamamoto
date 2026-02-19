from sqlalchemy.orm import Session
from app.models.schemas.exercise_schema import ExerciseCreate, ExerciseOut
from app.storage.database.exercise_repository import ExerciseRepository
from app.services.media_service import MediaService


class ExerciseService:
    def create(self, payload: ExerciseCreate, db: Session) -> ExerciseOut:
        tags_csv = ",".join([t.strip() for t in payload.tags if t.strip()])
        row = ExerciseRepository(db).create(payload.title, payload.description, tags_csv)
        return self._to_out(row)

    def upload_media(self, exercise_id: int, filename: str, content: bytes, db: Session) -> ExerciseOut:
        row = ExerciseRepository(db).get_by_id(exercise_id)
        if not row:
            raise ValueError("Exercise not found")

        path = MediaService().save_exercise_media(filename, content)
        ExerciseRepository(db).set_media_path(exercise_id, path)
        row = ExerciseRepository(db).get_by_id(exercise_id)
        return self._to_out(row)

    def list_recent(self, db: Session, limit: int = 50) -> list[ExerciseOut]:
        rows = ExerciseRepository(db).list_recent(limit=limit)
        return [self._to_out(r) for r in rows]

    def _to_out(self, row) -> ExerciseOut:
        tags = [t for t in (row.tags_csv or "").split(",") if t]
        return ExerciseOut(
            id=row.id,
            title=row.title,
            description=row.description,
            tags=tags,
            media_path=row.media_path,
            created_at_iso=row.created_at.isoformat(),
        )
