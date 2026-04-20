from pydantic import BaseModel, Field
from datetime import datetime
from typing import Optional


class PatientEvaluationBase(BaseModel):
    patient_id: int = Field(..., description="ID do paciente")
    professional_id: int = Field(..., description="ID do profissional")
    evaluation_date: datetime = Field(..., description="Data da avaliação")
    
    # Dados de identificação
    full_name: str = Field(..., min_length=3, max_length=255, description="Nome completo do paciente")
    address: Optional[str] = Field(None, description="Endereço completo")
    phone: str = Field(..., min_length=10, max_length=20, description="Telefone com DDD")
    email: str = Field(..., description="E-mail do paciente")
    cpf: str = Field(..., min_length=11, max_length=14, description="CPF do paciente")
    birth_date: Optional[datetime] = Field(None, description="Data de nascimento")
    gender: Optional[str] = Field(None, max_length=10, description="Gênero")
    
    # Dados administrativos
    first_contact_date: Optional[datetime] = Field(None, description="Data do primeiro contato")
    profession: Optional[str] = Field(None, max_length=100, description="Profissão")
    health_plan: Optional[str] = Field(None, max_length=100, description="Convênio/plano de saúde")
    patient_origin: Optional[str] = Field(None, max_length=100, description="Como chegou ao consultório")
    session_fee: Optional[float] = Field(None, ge=0, description="Valor cobrado por sessão")
    medications: Optional[str] = Field(None, description="Lista de medicamentos em uso (JSON)")
    appointment_time: Optional[str] = Field(None, max_length=10, description="Horário de atendimento")
    
    # Queixa principal
    main_reason: str = Field(..., min_length=5, description="Motivo principal da consulta")
    complaint_description: str = Field(..., min_length=10, description="Descrição detalhada da queixa")
    pain_scale: Optional[int] = Field(None, ge=0, le=10, description="Escala de dor (0-10)")
    
    # Histórico de dor
    pain_location: Optional[str] = Field(None, description="Local da dor")
    duration: Optional[str] = Field(None, max_length=100, description="Duração dos sintomas")
    frequency_pattern: Optional[str] = Field(None, description="Frequência ou padrão temporal")
    
    # Histórico clínico e exames
    clinical_history: Optional[str] = Field(None, description="Histórico clínico relevante (JSON)")
    exams: Optional[str] = Field(None, description="Exames complementares (JSON)")
    
    # Avaliação física
    postural_assessment: Optional[str] = Field(None, description="Avaliação postural (JSON)")
    
    # Plano de tratamento
    treatment_plan: Optional[str] = Field(None, description="Plano de tratamento (JSON)")


class PatientEvaluationCreate(PatientEvaluationBase):
    pass


class PatientEvaluationUpdate(BaseModel):
    full_name: Optional[str] = Field(None, min_length=3, max_length=255)
    address: Optional[str] = Field(None)
    phone: Optional[str] = Field(None, min_length=10, max_length=20)
    email: Optional[str] = Field(None)
    main_reason: Optional[str] = Field(None, min_length=5)
    complaint_description: Optional[str] = Field(None, min_length=10)
    pain_scale: Optional[int] = Field(None, ge=0, le=10)
    pain_location: Optional[str] = Field(None)
    duration: Optional[str] = Field(None, max_length=100)
    frequency_pattern: Optional[str] = Field(None)
    clinical_history: Optional[str] = Field(None)
    exams: Optional[str] = Field(None)
    postural_assessment: Optional[str] = Field(None)
    treatment_plan: Optional[str] = Field(None)


class PatientEvaluationResponse(PatientEvaluationBase):
    id: int
    created_at: datetime
    updated_at: Optional[datetime] = None
    
    class Config:
        from_attributes = True


class PatientEvaluationList(BaseModel):
    evaluations: list[PatientEvaluationResponse]
    total: int
