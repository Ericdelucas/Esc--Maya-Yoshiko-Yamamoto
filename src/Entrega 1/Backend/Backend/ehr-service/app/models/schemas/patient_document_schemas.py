from datetime import datetime
from typing import List, Optional
from pydantic import BaseModel, Field


class PatientDocumentFolderCreate(BaseModel):
    name: str = Field(..., min_length=1, max_length=255)


class PatientDocumentFolderResponse(BaseModel):
    id: int
    name: str
    document_count: int = 0
    last_update: Optional[datetime] = None
    
    class Config:
        from_attributes = True


class PatientDocumentUpload(BaseModel):
    file_name: str = Field(..., min_length=1, max_length=255)
    folder_id: Optional[int] = None
    notes: Optional[str] = None


class PatientDocumentResponse(BaseModel):
    id: int
    file_name: str
    file_type: str
    file_url: str
    file_size: int
    uploaded_by: str
    uploaded_at: datetime
    folder_id: Optional[int] = None
    folder_name: Optional[str] = None
    notes: Optional[str] = None
    is_archived: bool = False
    
    class Config:
        from_attributes = True


class ProfessionalPatientResponse(BaseModel):
    id: int
    full_name: str
    profile_photo_url: Optional[str] = None
    document_count: int = 0
    last_update: Optional[datetime] = None
    
    class Config:
        from_attributes = True


class PatientDocumentListResponse(BaseModel):
    documents: List[PatientDocumentResponse]
    total: int
    page: int
    page_size: int


class PatientFolderListResponse(BaseModel):
    folders: List[PatientDocumentFolderResponse]
    total: int
