from sqlalchemy import Column, Integer, String, Text, DateTime, JSON, ForeignKey, Float
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column
from datetime import datetime

class Base(DeclarativeBase):
    pass

class PatientReportORM(Base):
    __tablename__ = "patient_reports"
    
    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    patient_id: Mapped[int] = mapped_column(Integer, nullable=False)
    professional_id: Mapped[int] = mapped_column(Integer, nullable=False)
    report_date: Mapped[datetime] = mapped_column(DateTime, nullable=False)
    report_type: Mapped[str] = mapped_column(String(50), nullable=False)
    title: Mapped[str] = mapped_column(String(255), nullable=False)
    content: Mapped[str] = mapped_column(Text, nullable=True)
    
    # Seções
    clinical_evolution: Mapped[str] = mapped_column(Text, nullable=True)
    objective_data: Mapped[str] = mapped_column(Text, nullable=True)
    subjective_data: Mapped[str] = mapped_column(Text, nullable=True)
    treatment_plan: Mapped[str] = mapped_column(Text, nullable=True)
    recommendations: Mapped[str] = mapped_column(Text, nullable=True)
    next_steps: Mapped[str] = mapped_column(Text, nullable=True)
    
    # Avaliações
    pain_scale: Mapped[int] = mapped_column(Integer, nullable=True)
    functional_status: Mapped[str] = mapped_column(String(100), nullable=True)
    achievements: Mapped[dict] = mapped_column(JSON, nullable=True)
    limitations: Mapped[dict] = mapped_column(JSON, nullable=True)
    
    # Metadados
    created_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow)
    updated_at: Mapped[datetime] = mapped_column(DateTime, nullable=True, onupdate=datetime.utcnow)
    created_by: Mapped[str] = mapped_column(String(255), nullable=True)

class ReportSectionORM(Base):
    __tablename__ = "report_sections"
    
    id = Column(Integer, primary_key=True)
    report_id = Column(Integer, nullable=False)
    section_type = Column(String(50), nullable=False)
    title = Column(String(255), nullable=False)
    content = Column(Text)
    order_index = Column(Integer, default=0)
    section_data = Column(JSON)
    created_at = Column(DateTime)

class ReportAttachmentORM(Base):
    __tablename__ = "report_attachments"
    
    id = Column(Integer, primary_key=True)
    report_id = Column(Integer, nullable=False)
    attachment_type = Column(String(50), nullable=False)
    file_name = Column(String(255), nullable=False)
    file_path = Column(String(500))
    description = Column(Text)
    file_size = Column(Integer)
    uploaded_at = Column(DateTime)
