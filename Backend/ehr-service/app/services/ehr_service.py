from sqlalchemy.orm import Session
from app.models.schemas.ehr_schema import MedicalRecordCreate, MedicalRecordOut
from app.services.consent_guard_service import ConsentGuardService
from app.storage.database.medical_record_repository import MedicalRecordRepository


class EhrService:
    def create_record(self, payload: MedicalRecordCreate, db: Session) -> MedicalRecordOut:
        ConsentGuardService(db).assert_ehr_consent(patient_id=payload.patient_id)

        repo = MedicalRecordRepository(db)
        record = repo.create(
            patient_id=payload.patient_id,
            professional_id=payload.professional_id,
            notes=payload.notes
        )

        return MedicalRecordOut(
            id=record.id,
            patient_id=record.patient_id,
            professional_id=record.professional_id,
            notes=record.notes,
            created_at_iso=record.created_at.isoformat(),
        )
