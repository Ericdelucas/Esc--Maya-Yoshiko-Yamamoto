from datetime import datetime
from typing import Optional

from sqlalchemy import Integer, String, DateTime, Text, Column, JSON
from sqlalchemy.orm import mapped_column, Mapped, DeclarativeBase


class Base(DeclarativeBase):
    pass


class PatientEvaluationORM(Base):
    __tablename__ = "patient_evaluations"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    patient_id: Mapped[int] = mapped_column(Integer, nullable=False)
    professional_id: Mapped[int] = mapped_column(Integer, nullable=False)
    evaluation_date: Mapped[datetime] = mapped_column(DateTime, nullable=False)
    
    # Dados de identificação
    full_name: Mapped[str] = mapped_column(String(255), nullable=False)
    address: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    phone: Mapped[str] = mapped_column(String(20), nullable=False)
    email: Mapped[str] = mapped_column(String(255), nullable=False)
    cpf: Mapped[str] = mapped_column(String(14), nullable=False)
    birth_date: Mapped[Optional[datetime]] = mapped_column(DateTime, nullable=True)
    gender: Mapped[Optional[str]] = mapped_column(String(10), nullable=True)
    
    # Dados administrativos
    first_contact_date: Mapped[Optional[datetime]] = mapped_column(DateTime, nullable=True)
    profession: Mapped[Optional[str]] = mapped_column(String(100), nullable=True)
    health_plan: Mapped[Optional[str]] = mapped_column(String(100), nullable=True)
    patient_origin: Mapped[Optional[str]] = mapped_column(String(100), nullable=True)
    session_fee: Mapped[Optional[float]] = mapped_column(Integer, nullable=True)
    medications: Mapped[Optional[str]] = mapped_column(JSON, nullable=True)
    appointment_time: Mapped[Optional[str]] = mapped_column(String(10), nullable=True)
    
    # Queixa principal
    main_reason: Mapped[str] = mapped_column(Text, nullable=False)
    complaint_description: Mapped[str] = mapped_column(Text, nullable=False)
    pain_scale: Mapped[Optional[int]] = mapped_column(Integer, nullable=True)
    
    # Histórico de dor
    pain_location: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    duration: Mapped[Optional[str]] = mapped_column(String(100), nullable=True)
    frequency_pattern: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    
    # Histórico clínico e exames
    clinical_history: Mapped[Optional[str]] = mapped_column(JSON, nullable=True)
    exams: Mapped[Optional[str]] = mapped_column(JSON, nullable=True)
    
    # Avaliação física
    postural_assessment: Mapped[Optional[str]] = mapped_column(JSON, nullable=True)
    
    # Plano de tratamento
    treatment_plan: Mapped[Optional[str]] = mapped_column(JSON, nullable=True)
    
    # Timestamps
    created_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow)
    updated_at: Mapped[Optional[datetime]] = mapped_column(DateTime, nullable=True)
