from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy.orm import Session
from typing import List, Optional
from datetime import datetime, timedelta

from app.storage.database.patient_report_repository import PatientReportRepository
from app.models.schemas.patient_report_schema import (
    PatientReportCreate, PatientReportUpdate, PatientReportResponse,
    PatientReportList, ReportStatistics
)
from app.storage.database.db import get_session
from app.models.orm.patient_report_orm import PatientReportORM

router = APIRouter(prefix="/reports", tags=["patient-reports"])

def get_repository(db: Session = Depends(get_session)):
    return PatientReportRepository(db)

@router.post("/", response_model=PatientReportResponse, status_code=status.HTTP_201_CREATED)
async def create_report(
    report: PatientReportCreate,
    repo: PatientReportRepository = Depends(get_repository)
):
    """Criar novo relatório de paciente"""
    # Criar relatório principal
    report_orm = PatientReportORM(
        patient_id=report.patient_id,
        professional_id=report.professional_id,
        report_date=report.report_date,
        report_type=report.report_type,
        title=report.title,
        content=report.content,
        clinical_evolution=report.clinical_evolution,
        objective_data=report.objective_data,
        subjective_data=report.subjective_data,
        treatment_plan=report.treatment_plan,
        recommendations=report.recommendations,
        next_steps=report.next_steps,
        pain_scale=report.pain_scale,
        functional_status=report.functional_status,
        achievements=report.achievements,
        limitations=report.limitations,
        metadata=report.metadata,
        created_by="professional"  # Temporário
    )
    
    # Salvar relatório
    saved_report = repo.save(report_orm)
    
    return PatientReportResponse.model_validate(saved_report)

@router.get("/", response_model=PatientReportList)
async def get_reports(
    professional_id: Optional[int] = Query(None),
    patient_id: Optional[int] = Query(None),
    report_type: Optional[str] = Query(None),
    page: int = Query(1, ge=1),
    per_page: int = Query(20, ge=1, le=100),
    repo: PatientReportRepository = Depends(get_repository)
):
    """Listar relatórios com filtros e paginação"""
    
    # Buscar relatórios
    if professional_id and patient_id:
        reports = repo.findByPatientAndProfessional(patient_id, professional_id)
    elif professional_id:
        reports = repo.findByProfessionalId(professional_id, per_page)
    elif patient_id:
        reports = repo.findByPatientId(patient_id, per_page)
    elif report_type:
        reports = repo.findByType(report_type, professional_id)
    else:
        reports = repo.findAll(professional_id, per_page)
    
    # Paginação
    start = (page - 1) * per_page
    end = start + per_page
    paginated_reports = reports[start:end]
    
    return PatientReportList(
        reports=[PatientReportResponse.model_validate(r) for r in paginated_reports],
        total=len(reports),
        page=page,
        per_page=per_page
    )

@router.get("/patient/{patient_id}", response_model=List[PatientReportResponse])
async def get_patient_reports(
    patient_id: int,
    limit: int = Query(50, ge=1, le=100),
    repo: PatientReportRepository = Depends(get_repository)
):
    """Listar todos os relatórios de um paciente"""
    reports = repo.findByPatientId(patient_id, limit)
    return [PatientReportResponse.model_validate(r) for r in reports]

@router.get("/professional/{professional_id}", response_model=List[PatientReportResponse])
async def get_professional_reports(
    professional_id: int,
    limit: int = Query(100, ge=1, le=200),
    repo: PatientReportRepository = Depends(get_repository)
):
    """Listar todos os relatórios de um profissional"""
    reports = repo.findByProfessionalId(professional_id, limit)
    return [PatientReportResponse.model_validate(r) for r in reports]

@router.get("/{report_id}", response_model=PatientReportResponse)
async def get_report(
    report_id: int,
    repo: PatientReportRepository = Depends(get_repository)
):
    """Buscar relatório por ID"""
    report = repo.findById(report_id)
    if not report:
        raise HTTPException(status_code=404, detail="Relatório não encontrado")
    
    return PatientReportResponse.model_validate(report)

@router.put("/{report_id}", response_model=PatientReportResponse)
async def update_report(
    report_id: int,
    report_update: PatientReportUpdate,
    repo: PatientReportRepository = Depends(get_repository)
):
    """Atualizar relatório existente"""
    report = repo.findById(report_id)
    if not report:
        raise HTTPException(status_code=404, detail="Relatório não encontrado")
    
    # Atualizar campos
    update_data = report_update.model_dump(exclude_unset=True)
    for field, value in update_data.items():
        setattr(report, field, value)
    
    report.updated_at = datetime.now()
    
    updated_report = repo.save(report)
    return PatientReportResponse.model_validate(updated_report)

@router.delete("/{report_id}")
async def delete_report(
    report_id: int,
    repo: PatientReportRepository = Depends(get_repository)
):
    """Excluir relatório"""
    report = repo.findById(report_id)
    if not report:
        raise HTTPException(status_code=404, detail="Relatório não encontrado")
    
    success = repo.deleteById(report_id)
    if not success:
        raise HTTPException(status_code=500, detail="Erro ao excluir relatório")
    
    return {"message": "Relatório excluído com sucesso"}

@router.get("/search/patient", response_model=List[PatientReportResponse])
async def search_reports_by_patient(
    name: str = Query(..., min_length=2),
    professional_id: Optional[int] = Query(None),
    repo: PatientReportRepository = Depends(get_repository)
):
    """Buscar relatórios por nome do paciente"""
    reports = repo.searchReports(name, professional_id)
    return [PatientReportResponse.model_validate(r) for r in reports]

@router.get("/statistics", response_model=ReportStatistics)
async def get_report_statistics(
    professional_id: Optional[int] = Query(None),
    repo: PatientReportRepository = Depends(get_repository)
):
    """Estatísticas dos relatórios do profissional"""
    stats = repo.getReportStatistics(professional_id)
    recent_reports = repo.getRecentReports(professional_id) if professional_id else []
    
    return ReportStatistics(
        report_types=stats,
        total_reports=sum(s['count'] for s in stats.values()),
        recent_reports=[PatientReportResponse.model_validate(r) for r in recent_reports]
    )
