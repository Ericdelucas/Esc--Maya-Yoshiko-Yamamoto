from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.orm import Session
from typing import List

from app.storage.database.patient_evaluation_repository import PatientEvaluationRepository
from app.storage.database.db import get_session
from app.models.schemas.patient_evaluation_schema import (
    PatientEvaluationCreate, PatientEvaluationResponse, PatientEvaluationUpdate, 
    PatientEvaluationList
)

router = APIRouter(prefix="/evaluations", tags=["patient_evaluations"])


@router.post("/", response_model=PatientEvaluationResponse, status_code=201)
def create_evaluation(
    evaluation: PatientEvaluationCreate,
    session: Session = Depends(get_session)
):
    """
    Criar nova ficha de avaliação fisioterapêutica
    """
    repository = PatientEvaluationRepository()
    try:
        from app.models.orm.patient_evaluation_orm import PatientEvaluationORM
        new_evaluation = PatientEvaluationORM(**evaluation.dict())
        saved_evaluation = repository.save(new_evaluation, session)
        return saved_evaluation
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao criar avaliação: {str(e)}")


@router.get("/patient/{patient_id}", response_model=List[PatientEvaluationResponse])
def get_evaluations_by_patient(
    patient_id: int,
    session: Session = Depends(get_session)
):
    """
    Listar todas as avaliações de um paciente específico
    """
    repository = PatientEvaluationRepository()
    try:
        evaluations = repository.findByPatientId(patient_id, session)
        return evaluations
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao buscar avaliações: {str(e)}")


@router.get("/professional/{professional_id}", response_model=List[PatientEvaluationResponse])
def get_evaluations_by_professional(
    professional_id: int,
    session: Session = Depends(get_session)
):
    """
    Listar todas as avaliações de um profissional específico
    """
    repository = PatientEvaluationRepository()
    try:
        evaluations = repository.findByProfessionalId(professional_id, session)
        return evaluations
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao buscar avaliações: {str(e)}")


@router.get("/", response_model=PatientEvaluationList)
def list_evaluations(
    skip: int = Query(0, ge=0, description="Número de registros para pular"),
    limit: int = Query(100, ge=1, le=1000, description="Número máximo de registros"),
    session: Session = Depends(get_session)
):
    """
    Listar todas as avaliações com paginação
    """
    repository = PatientEvaluationRepository()
    try:
        all_evaluations = repository.findAll(session)
        total = len(all_evaluations)
        evaluations = all_evaluations[skip:skip + limit]
        
        return PatientEvaluationList(
            evaluations=evaluations,
            total=total
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao listar avaliações: {str(e)}")


@router.get("/{evaluation_id}", response_model=PatientEvaluationResponse)
def get_evaluation(
    evaluation_id: int,
    session: Session = Depends(get_session)
):
    """
    Buscar avaliação por ID específico
    """
    repository = PatientEvaluationRepository()
    evaluation = repository.findById(evaluation_id, session)
    if not evaluation:
        raise HTTPException(status_code=404, detail="Avaliação não encontrada")
    return evaluation


@router.put("/{evaluation_id}", response_model=PatientEvaluationResponse)
def update_evaluation(
    evaluation_id: int,
    evaluation_update: PatientEvaluationUpdate,
    session: Session = Depends(get_session)
):
    """
    Atualizar avaliação existente
    """
    repository = PatientEvaluationRepository()
    existing_evaluation = repository.findById(evaluation_id, session)
    if not existing_evaluation:
        raise HTTPException(status_code=404, detail="Avaliação não encontrada")
    
    try:
        # Atualizar apenas os campos fornecidos
        update_data = evaluation_update.dict(exclude_unset=True)
        for field, value in update_data.items():
            setattr(existing_evaluation, field, value)
        
        updated_evaluation = repository.save(existing_evaluation, session)
        return updated_evaluation
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao atualizar avaliação: {str(e)}")


@router.delete("/{evaluation_id}")
def delete_evaluation(
    evaluation_id: int,
    session: Session = Depends(get_session)
):
    """
    Excluir avaliação por ID
    """
    repository = PatientEvaluationRepository()
    try:
        success = repository.deleteById(evaluation_id, session)
        if not success:
            raise HTTPException(status_code=404, detail="Avaliação não encontrada")
        return {"message": "Avaliação excluída com sucesso"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao excluir avaliação: {str(e)}")


@router.get("/search/patient", response_model=List[PatientEvaluationResponse])
def search_evaluations_by_patient_name(
    name: str = Query(..., min_length=3, description="Nome do paciente para buscar"),
    session: Session = Depends(get_session)
):
    """
    Buscar avaliações por nome do paciente
    """
    repository = PatientEvaluationRepository()
    try:
        all_evaluations = repository.findAll(session)
        # Filtrar por nome (case insensitive)
        filtered_evaluations = [
            eval for eval in all_evaluations 
            if name.lower() in eval.full_name.lower()
        ]
        return filtered_evaluations
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao buscar avaliações: {str(e)}")
