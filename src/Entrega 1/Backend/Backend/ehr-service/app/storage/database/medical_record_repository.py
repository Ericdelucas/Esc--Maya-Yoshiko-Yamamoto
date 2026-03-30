from sqlalchemy.orm import Session
from app.models.orm.medical_record_orm import MedicalRecordORM
from app.storage.database.base_repository import BaseRepository


class MedicalRecordRepository(BaseRepository):
    def __init__(self, db: Session) -> None:
        self._db = db

    def create(self, patient_id: int, professional_id: int, notes: str = None, notes_encrypted: str = None) -> MedicalRecordORM:
        record = MedicalRecordORM(
            patient_id=patient_id,
            professional_id=professional_id,
            notes=notes,
            notes_encrypted=notes_encrypted
        )
        self._db.add(record)
        self._db.commit()
        self._db.refresh(record)
        return record
