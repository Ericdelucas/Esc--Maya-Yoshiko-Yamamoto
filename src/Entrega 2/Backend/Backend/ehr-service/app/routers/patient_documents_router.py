from fastapi import APIRouter, Depends, HTTPException, status, UploadFile, File, Query
from fastapi.responses import FileResponse
from typing import Optional

from app.services.patient_documents_service import PatientDocumentsService
from app.models.schemas.patient_document_schemas import (
    PatientDocumentFolderCreate,
    PatientDocumentFolderResponse,
    PatientDocumentResponse,
    ProfessionalPatientResponse,
    PatientDocumentListResponse,
    PatientFolderListResponse
)
from app.core.auth_dependencies import get_current_user, require_professional_or_admin

router = APIRouter(prefix="/ehr", tags=["patient_documents"])


@router.get("/professionals/patients", response_model=list[ProfessionalPatientResponse])
async def get_professional_patients(
    current_user: dict = Depends(require_professional_or_admin)
) -> list[ProfessionalPatientResponse]:
    """
    Lista pacientes disponíveis para o profissional
    Apenas Professional e Admin podem acessar
    """
    try:
        professional_id = int(current_user.get("sub"))
        service = PatientDocumentsService()
        return service.get_professional_patients(professional_id)
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error retrieving patients: {str(e)}"
        )


@router.get("/patients/{patient_id}/document-folders", response_model=PatientFolderListResponse)
async def get_patient_folders(
    patient_id: int,
    current_user: dict = Depends(require_professional_or_admin)
) -> PatientFolderListResponse:
    """
    Lista pastas de documentos de um paciente
    Apenas Professional e Admin podem acessar
    """
    try:
        professional_id = int(current_user.get("sub"))
        service = PatientDocumentsService()
        return service.get_patient_folders(patient_id)
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error retrieving folders: {str(e)}"
        )


@router.post("/patients/{patient_id}/document-folders", response_model=PatientDocumentFolderResponse)
async def create_document_folder(
    patient_id: int,
    folder_data: PatientDocumentFolderCreate,
    current_user: dict = Depends(require_professional_or_admin)
) -> PatientDocumentFolderResponse:
    """
    Cria uma nova pasta de documentos para o paciente
    Apenas Professional e Admin podem acessar
    """
    try:
        created_by = int(current_user.get("sub"))
        service = PatientDocumentsService()
        return service.create_folder(patient_id, folder_data, created_by)
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error creating folder: {str(e)}"
        )


@router.get("/patients/{patient_id}/documents", response_model=PatientDocumentListResponse)
async def get_patient_documents(
    patient_id: int,
    folder_id: Optional[int] = Query(None, description="ID da pasta para filtrar"),
    page: int = Query(1, ge=1, description="Número da página"),
    page_size: int = Query(20, ge=1, le=100, description="Itens por página"),
    current_user: dict = Depends(require_professional_or_admin)
) -> PatientDocumentListResponse:
    """
    Lista documentos de um paciente com paginação
    Apenas Professional e Admin podem acessar
    """
    try:
        service = PatientDocumentsService()
        return service.get_patient_documents(
            patient_id=patient_id,
            folder_id=folder_id,
            page=page,
            page_size=page_size
        )
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error retrieving documents: {str(e)}"
        )


@router.post("/patients/{patient_id}/documents/upload", response_model=PatientDocumentResponse)
async def upload_patient_document(
    patient_id: int,
    file: UploadFile = File(...),
    folder_id: Optional[int] = Query(None, description="ID da pasta (opcional)"),
    notes: Optional[str] = Query(None, description="Notas sobre o documento"),
    current_user: dict = Depends(require_professional_or_admin)
) -> PatientDocumentResponse:
    """
    Faz upload de um documento para o paciente
    Apenas Professional e Admin podem acessar
    """
    try:
        uploaded_by = int(current_user.get("sub"))
        service = PatientDocumentsService()
        return service.upload_document(
            patient_id=patient_id,
            file=file,
            uploaded_by=uploaded_by,
            folder_id=folder_id,
            notes=notes
        )
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error uploading document: {str(e)}"
        )


@router.patch("/documents/{document_id}/archive")
async def archive_document(
    document_id: int,
    current_user: dict = Depends(require_professional_or_admin)
) -> dict:
    """
    Arquiva um documento
    Apenas Professional e Admin podem acessar
    """
    try:
        service = PatientDocumentsService()
        success = service.archive_document(document_id)
        
        if not success:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Document not found"
            )
        
        return {"message": "Document archived successfully"}
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error archiving document: {str(e)}"
        )


@router.get("/documents/{document_id}/download")
async def download_document(
    document_id: int,
    current_user: dict = Depends(require_professional_or_admin)
) -> FileResponse:
    """
    Download autenticado de documento
    Apenas Professional e Admin podem acessar
    """
    try:
        service = PatientDocumentsService()
        # TODO: Adicionar validação de vínculo aqui
        file_path = service.get_document_file_path(document_id)
        
        if not file_path or not os.path.exists(file_path):
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Document not found"
            )
        
        return FileResponse(
            path=file_path,
            filename=os.path.basename(file_path),
            media_type="application/octet-stream"
        )
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error downloading document: {str(e)}"
        )
