import os
import uuid
import magic
from typing import Optional
from sqlalchemy import text
from sqlalchemy.orm import Session
from fastapi import UploadFile, HTTPException, status

from app.repositories.patient_documents_repository import PatientDocumentsRepository
from app.storage.database.db import SessionLocal
from app.models.schemas.patient_document_schemas import (
    PatientDocumentFolderCreate,
    PatientDocumentFolderResponse,
    PatientDocumentResponse,
    ProfessionalPatientResponse,
    PatientDocumentListResponse,
    PatientFolderListResponse
)


class PatientDocumentsService:
    
    ALLOWED_EXTENSIONS = {'.pdf', '.jpg', '.jpeg', '.png'}
    ALLOWED_MIME_TYPES = {
        'application/pdf',
        'image/jpeg',
        'image/jpg',
        'image/png'
    }
    MAX_FILE_SIZE = 10 * 1024 * 1024  # 10MB
    UPLOAD_DIR = '/app/storage/patient_documents'
    
    def __init__(self):
        self.repo = PatientDocumentsRepository()
        self._ensure_upload_dir()
    
    def _ensure_upload_dir(self):
        """Garante que o diretório de upload exista"""
        os.makedirs(self.UPLOAD_DIR, exist_ok=True)
    
    def _validate_file(self, file: UploadFile) -> None:
        """Valida o arquivo de upload"""
        if not file.filename:
            raise HTTPException(status_code=400, detail="Nome do arquivo é obrigatório")
        
        # Verificar extensão
        file_ext = os.path.splitext(file.filename)[1].lower()
        if file_ext not in self.ALLOWED_EXTENSIONS:
            raise HTTPException(
                status_code=400, 
                detail=f"Extensão não permitida. Permitidas: {', '.join(self.ALLOWED_EXTENSIONS)}"
            )
        
        # Verificar MIME type
        if file.content_type not in self.ALLOWED_MIME_TYPES:
            raise HTTPException(
                status_code=400,
                detail=f"Tipo de arquivo não permitido. Permitidos: {', '.join(self.ALLOWED_MIME_TYPES)}"
            )
    
    def _normalize_filename(self, filename: str) -> str:
        """Normaliza o nome do arquivo"""
        name, ext = os.path.splitext(filename)
        # Remover caracteres especiais e espaços
        normalized = "".join(c for c in name if c.isalnum() or c in (' ', '-', '_')).rstrip()
        normalized = normalized.replace(' ', '_')
        # Adicionar UUID para evitar conflitos
        return f"{normalized}_{uuid.uuid4().hex[:8]}{ext}"
    
    def get_professional_patients(self, professional_id: int) -> List[ProfessionalPatientResponse]:
        """Retorna lista de pacientes para o profissional"""
        patients_data = self.repo.get_professional_patients(professional_id)
        
        return [
            ProfessionalPatientResponse(
                id=patient["id"],
                full_name=patient["full_name"],
                profile_photo_url=patient["profile_photo_url"],
                document_count=patient["document_count"],
                last_update=patient["last_update"]
            )
            for patient in patients_data
        ]
    
    def get_patient_folders(self, patient_id: int) -> PatientFolderListResponse:
        """Retorna pastas de documentos de um paciente"""
        folders_data = self.repo.get_patient_folders(patient_id)
        
        folders = [
            PatientDocumentFolderResponse(
                id=folder["id"],
                name=folder["name"],
                document_count=folder["document_count"],
                last_update=folder["last_update"]
            )
            for folder in folders_data
        ]
        
        return PatientFolderListResponse(
            folders=folders,
            total=len(folders)
        )
    
    def create_folder(
        self, 
        patient_id: int, 
        folder_data: PatientDocumentFolderCreate, 
        created_by: int
    ) -> PatientDocumentFolderResponse:
        """Cria uma nova pasta de documentos"""
        folder_data_dict = self.repo.create_folder(
            patient_id=patient_id,
            name=folder_data.name,
            created_by=created_by
        )
        
        return PatientDocumentFolderResponse(**folder_data_dict)
    
    def get_patient_documents(
        self, 
        patient_id: int, 
        folder_id: Optional[int] = None,
        page: int = 1,
        page_size: int = 20
    ) -> PatientDocumentListResponse:
        """Retorna documentos de um paciente"""
        result = self.repo.get_patient_documents(
            patient_id=patient_id,
            folder_id=folder_id,
            page=page,
            page_size=page_size
        )
        
        documents = [
            PatientDocumentResponse(**doc)
            for doc in result["documents"]
        ]
        
        return PatientDocumentListResponse(
            documents=documents,
            total=result["total"],
            page=result["page"],
            page_size=result["page_size"]
        )
    
    def upload_document(
        self,
        patient_id: int,
        file: UploadFile,
        uploaded_by: int,
        folder_id: Optional[int] = None,
        notes: Optional[str] = None
    ) -> PatientDocumentResponse:
        """Faz upload de um documento"""
        # Validar arquivo
        self._validate_file(file)
        
        # Verificar tamanho
        file.file.seek(0, 2)  # Ir para o final
        file_size = file.file.tell()
        file.file.seek(0)  # Voltar ao início
        
        if file_size > self.MAX_FILE_SIZE:
            raise HTTPException(
                status_code=400,
                detail=f"Arquivo muito grande. Tamanho máximo: {self.MAX_FILE_SIZE // (1024*1024)}MB"
            )
        
        # Normalizar nome do arquivo
        normalized_filename = self._normalize_filename(file.filename)
        
        # Salvar arquivo no disco
        file_path = os.path.join(self.UPLOAD_DIR, normalized_filename)
        try:
            with open(file_path, "wb") as buffer:
                content = file.file.read()
                buffer.write(content)
        except Exception as e:
            raise HTTPException(
                status_code=500,
                detail=f"Erro ao salvar arquivo: {str(e)}"
            )
        
        # Salvar no banco
        document_data = self.repo.upload_document(
            patient_id=patient_id,
            file_name=normalized_filename,
            file_type=file.content_type,
            file_url=f"/media/patient_documents/{normalized_filename}",
            file_size=file_size,
            uploaded_by=uploaded_by,
            folder_id=folder_id,
            notes=notes
        )
        
        return PatientDocumentResponse(**document_data)
    
    def archive_document(self, document_id: int) -> bool:
        """Arquiva um documento"""
        return self.repo.archive_document(document_id)
    
    def get_document_file_path(self, document_id: int) -> Optional[str]:
        """Retorna o caminho físico do documento"""
        with SessionLocal() as session:
            # Buscar documento no banco
            query = text("""
                SELECT file_url FROM patient_documents 
                WHERE id = :document_id AND is_archived = FALSE
            """)
            result = session.execute(query, {"document_id": document_id}).fetchone()
            
            if not result:
                return None
            
            # Converter URL para caminho físico
            file_url = result.file_url
            if file_url.startswith("/media/patient_documents/"):
                filename = file_url.replace("/media/patient_documents/", "")
                return f"/app/storage/patient_documents/{filename}"
            
            return None
