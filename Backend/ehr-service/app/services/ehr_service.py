from sqlalchemy.orm import Session
from app.models.schemas.ehr_schema import MedicalRecordCreate, MedicalRecordOut
from app.services.consent_guard_service import ConsentGuardService
from app.storage.database.medical_record_repository import MedicalRecordRepository
from shared.security.utils import encrypt_medical_notes, decrypt_medical_notes


class EhrService:
    def create_record(self, payload: MedicalRecordCreate, db: Session) -> MedicalRecordOut:
        ConsentGuardService(db).assert_ehr_consent(patient_id=payload.patient_id)

        repo = MedicalRecordRepository(db)
        
        # Encrypt sensitive medical notes
        encrypted_notes = encrypt_medical_notes(payload.notes)
        
        record = repo.create(
            patient_id=payload.patient_id,
            professional_id=payload.professional_id,
            notes=None,  # Legacy field set to None
            notes_encrypted=encrypted_notes
        )

        return MedicalRecordOut(
            id=record.id,
            patient_id=record.patient_id,
            professional_id=record.professional_id,
            notes=decrypt_medical_notes(record.notes_encrypted) if record.notes_encrypted else (record.notes or ""),
            created_at_iso=record.created_at.isoformat(),
        )
