from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any
from datetime import datetime

class PatientReportBase(BaseModel):
    patient_id: int = Field(..., description="ID do paciente")
    professional_id: int = Field(..., description="ID do profissional")
    report_date: datetime = Field(..., description="Data do relatório")
    report_type: str = Field(..., description="Tipo do relatório")
    title: str = Field(..., description="Título do relatório")
    content: Optional[str] = Field(None, description="Conteúdo principal")
    
    # Seções
    clinical_evolution: Optional[str] = Field(None, description="Evolução clínica")
    objective_data: Optional[str] = Field(None, description="Dados objetivos")
    subjective_data: Optional[str] = Field(None, description="Dados subjetivos")
    treatment_plan: Optional[str] = Field(None, description="Plano de tratamento")
    recommendations: Optional[str] = Field(None, description="Recomendações")
    next_steps: Optional[str] = Field(None, description="Próximos passos")
    
    # Avaliações
    pain_scale: Optional[int] = Field(None, ge=0, le=10, description="Escala de dor")
    functional_status: Optional[str] = Field(None, description="Status funcional")
    achievements: Optional[List[str]] = Field(default_factory=list, description="Conquistas")
    limitations: Optional[List[str]] = Field(default_factory=list, description="Limitações")

class PatientReportCreate(PatientReportBase):
    pass

class PatientReportUpdate(BaseModel):
    title: Optional[str] = None
    content: Optional[str] = None
    clinical_evolution: Optional[str] = None
    objective_data: Optional[str] = None
    subjective_data: Optional[str] = None
    treatment_plan: Optional[str] = None
    recommendations: Optional[str] = None
    next_steps: Optional[str] = None
    pain_scale: Optional[int] = Field(None, ge=0, le=10)
    functional_status: Optional[str] = None
    achievements: Optional[List[str]] = None
    limitations: Optional[List[str]] = None

class PatientReportResponse(PatientReportBase):
    id: int
    created_at: datetime
    updated_at: Optional[datetime] = None
    created_by: Optional[str] = None
    
    class Config:
        from_attributes = True

class PatientReportList(BaseModel):
    reports: List[PatientReportResponse]
    total: int
    page: int
    per_page: int

class ReportStatistics(BaseModel):
    report_types: Dict[str, Dict[str, Any]]
    total_reports: int
    recent_reports: List[PatientReportResponse]

# # Schemas para anexos
class ReportAttachmentBase(BaseModel):
    attachment_type: str = Field(..., description="Tipo do anexo (image, document, etc)")
    file_name: str = Field(..., description="Nome do arquivo")
    description: Optional[str] = Field(None, description="Descrição do anexo")

class ReportAttachmentCreate(ReportAttachmentBase):
    pass

class ReportAttachmentResponse(ReportAttachmentBase):
    id: int
    report_id: int
    file_path: Optional[str] = None
    file_size: Optional[int] = None
    uploaded_at: Optional[datetime] = None
    
    class Config:
        from_attributes = True

class ReportAttachmentList(BaseModel):
    attachments: List[ReportAttachmentResponse]
    total: int

# # Atualizar response do relatório para incluir anexos
class PatientReportWithAttachments(PatientReportResponse):
    attachments: List[ReportAttachmentResponse] = Field(default_factory=list)
