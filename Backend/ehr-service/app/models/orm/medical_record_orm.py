from datetime import datetime
from sqlalchemy import String, Text, DateTime, LongText
from sqlalchemy.orm import Mapped, mapped_column
from app.storage.database.base import Base


class MedicalRecordORM(Base):
    __tablename__ = "medical_records"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    patient_id: Mapped[int] = mapped_column(nullable=False, index=True)
    professional_id: Mapped[int] = mapped_column(nullable=False, index=True)
    notes: Mapped[str] = mapped_column(Text, nullable=True)  # Legacy field for migration
    notes_encrypted: Mapped[str] = mapped_column(LongText, nullable=True)  # Encrypted medical notes
    created_at: Mapped[datetime] = mapped_column(DateTime, nullable=False, default=datetime.utcnow)
