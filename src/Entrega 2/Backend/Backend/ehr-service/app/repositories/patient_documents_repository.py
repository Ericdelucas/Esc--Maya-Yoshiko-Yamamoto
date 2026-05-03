from sqlalchemy.orm import Session
from sqlalchemy import and_, func, desc, or_, select, text
from typing import List, Optional, Dict
from datetime import datetime

from app.models.orm.patient_documents_orm import PatientDocumentFolderORM, PatientDocumentORM
from app.storage.database.db import SessionLocal


class PatientDocumentsRepository:
    
    def get_professional_patients(self, professional_id: int) -> List[Dict]:
        """Retorna lista de pacientes que o profissional pode acessar"""
        with SessionLocal() as session:
            # Simplificado - retorna todos os pacientes por enquanto
            # Em produção, filtrar por vínculo clínico
            patients_query = text("""
                SELECT id, email, full_name, profile_photo_url 
                FROM users 
                WHERE role = 'Patient'
            """)
            patients = session.execute(patients_query).fetchall()
            
            result = []
            for patient in patients:
                # Contar documentos do paciente
                doc_count_query = text("""
                    SELECT COUNT(*) 
                    FROM patient_documents 
                    WHERE patient_id = :patient_id AND is_archived = FALSE
                """)
                doc_count = session.execute(doc_count_query, {"patient_id": patient.id}).scalar()
                
                # Obter última atualização
                last_doc_query = text("""
                    SELECT created_at 
                    FROM patient_documents 
                    WHERE patient_id = :patient_id AND is_archived = FALSE 
                    ORDER BY created_at DESC 
                    LIMIT 1
                """)
                last_doc = session.execute(last_doc_query, {"patient_id": patient.id}).scalar()
                
                result.append({
                    "id": patient.id,
                    "full_name": patient.full_name or f"Paciente {patient.id}",
                    "profile_photo_url": patient.profile_photo_url,
                    "document_count": doc_count,
                    "last_update": last_doc
                })
            
            return result
    
    def get_patient_folders(self, patient_id: int) -> List[Dict]:
        """Retorna pastas de documentos de um paciente"""
        with SessionLocal() as session:
            folders = session.query(
                PatientDocumentFolderORM.id,
                PatientDocumentFolderORM.name,
                PatientDocumentFolderORM.created_at
            ).filter(PatientDocumentFolderORM.patient_id == patient_id).all()
            
            result = []
            for folder in folders:
                # Contar documentos na pasta
                doc_count = session.query(PatientDocumentORM).filter(
                    and_(
                        PatientDocumentORM.patient_id == patient_id,
                        PatientDocumentORM.folder_id == folder.id,
                        PatientDocumentORM.is_archived == False
                    )
                ).count()
                
                # Obter última atualização da pasta
                last_doc = session.query(PatientDocumentORM).filter(
                    and_(
                        PatientDocumentORM.patient_id == patient_id,
                        PatientDocumentORM.folder_id == folder.id,
                        PatientDocumentORM.is_archived == False
                    )
                ).order_by(desc(PatientDocumentORM.created_at)).first()
                
                result.append({
                    "id": folder.id,
                    "name": folder.name,
                    "document_count": doc_count,
                    "last_update": last_doc.created_at if last_doc else None
                })
            
            return result
    
    def create_folder(self, patient_id: int, name: str, created_by: int) -> Dict:
        """Cria uma nova pasta de documentos"""
        with SessionLocal() as session:
            # Usar SQL direto para evitar problemas de ORM
            insert_query = text("""
                INSERT INTO patient_document_folders (patient_id, name, created_by, created_at, updated_at)
                VALUES (:patient_id, :name, :created_by, NOW(), NOW())
            """)
            session.execute(insert_query, {
                "patient_id": patient_id,
                "name": name,
                "created_by": created_by
            })
            session.commit()
            
            # Buscar a pasta criada
            result_query = text("""
                SELECT id, name, created_at
                FROM patient_document_folders 
                WHERE patient_id = :patient_id AND name = :name
                ORDER BY id DESC LIMIT 1
            """)
            result = session.execute(result_query, {"patient_id": patient_id, "name": name}).fetchone()
            
            return {
                "id": result.id,
                "name": result.name,
                "document_count": 0,
                "last_update": result.created_at
            }
    
    def get_patient_documents(
        self, 
        patient_id: int, 
        folder_id: Optional[int] = None,
        page: int = 1,
        page_size: int = 20
    ) -> Dict:
        """Retorna documentos de um paciente com paginação"""
        with SessionLocal() as session:
            # Construir query base
            base_query = """
                SELECT 
                    pd.id, pd.file_name, pd.file_type, pd.file_url, pd.file_size, 
                    pd.notes, pd.created_at, pd.folder_id, pdf.name as folder_name,
                    u.full_name as uploaded_by
                FROM patient_documents pd
                JOIN users u ON pd.uploaded_by = u.id
                LEFT JOIN patient_document_folders pdf ON pd.folder_id = pdf.id
                WHERE pd.patient_id = :patient_id AND pd.is_archived = FALSE
            """
            
            params = {"patient_id": patient_id}
            
            if folder_id:
                base_query += " AND pd.folder_id = :folder_id"
                params["folder_id"] = folder_id
            
            # Contar total
            count_query = f"SELECT COUNT(*) FROM ({base_query}) as subq"
            total = session.execute(text(count_query), params).scalar()
            
            # Paginação
            offset = (page - 1) * page_size
            paginated_query = base_query + " ORDER BY pd.created_at DESC LIMIT :limit OFFSET :offset"
            params.update({"limit": page_size, "offset": offset})
            
            documents = session.execute(text(paginated_query), params).fetchall()
            
            return {
                "documents": [
                    {
                        "id": doc.id,
                        "file_name": doc.file_name,
                        "file_type": doc.file_type,
                        "file_url": doc.file_url,
                        "file_size": doc.file_size,
                        "uploaded_by": doc.uploaded_by or "Desconhecido",
                        "uploaded_at": doc.created_at,
                        "folder_id": doc.folder_id,
                        "folder_name": doc.folder_name,
                        "notes": doc.notes,
                        "is_archived": False
                    }
                    for doc in documents
                ],
                "total": total,
                "page": page,
                "page_size": page_size
            }
    
    def upload_document(
        self, 
        patient_id: int, 
        file_name: str, 
        file_type: str, 
        file_url: str, 
        file_size: int, 
        uploaded_by: int, 
        folder_id: Optional[int] = None, 
        notes: Optional[str] = None
    ) -> Dict:
        """Faz upload de um documento"""
        with SessionLocal() as session:
            # Usar SQL direto para evitar problemas de ORM
            insert_query = text("""
                INSERT INTO patient_documents (patient_id, folder_id, file_name, file_type, file_url, file_size, uploaded_by, notes, created_at, updated_at)
                VALUES (:patient_id, :folder_id, :file_name, :file_type, :file_url, :file_size, :uploaded_by, :notes, NOW(), NOW())
            """)
            session.execute(insert_query, {
                "patient_id": patient_id,
                "folder_id": folder_id,
                "file_name": file_name,
                "file_type": file_type,
                "file_url": file_url,
                "file_size": file_size,
                "uploaded_by": uploaded_by,
                "notes": notes
            })
            session.commit()
            
            # Obter informações do uploader e nome da pasta
            uploader_query = text("SELECT full_name FROM users WHERE id = :uploader_id")
            uploader = session.execute(uploader_query, {"uploader_id": uploaded_by}).scalar()
            
            folder_name = None
            if folder_id:
                folder_query = text("SELECT name FROM patient_document_folders WHERE id = :folder_id")
                folder = session.execute(folder_query, {"folder_id": folder_id}).scalar()
                folder_name = folder
            
            # Buscar o documento criado
            doc_query = text("""
                SELECT id, file_name, file_type, file_url, file_size, created_at, folder_id, notes
                FROM patient_documents 
                WHERE patient_id = :patient_id AND file_name = :file_name
                ORDER BY id DESC LIMIT 1
            """)
            result = session.execute(doc_query, {"patient_id": patient_id, "file_name": file_name}).fetchone()
            
            return {
                "id": result.id,
                "file_name": result.file_name,
                "file_type": result.file_type,
                "file_url": result.file_url,
                "file_size": result.file_size,
                "uploaded_by": uploader or "Desconhecido",
                "uploaded_at": result.created_at,
                "folder_id": result.folder_id,
                "folder_name": folder_name,
                "notes": result.notes,
                "is_archived": False
            }
    
    def archive_document(self, document_id: int) -> bool:
        """Arquiva um documento"""
        with SessionLocal() as session:
            # Usar SQL direto para evitar problemas de ORM
            update_query = text("""
                UPDATE patient_documents 
                SET is_archived = TRUE, updated_at = NOW()
                WHERE id = :document_id
            """)
            result = session.execute(update_query, {"document_id": document_id})
            session.commit()
            
            return result.rowcount > 0
