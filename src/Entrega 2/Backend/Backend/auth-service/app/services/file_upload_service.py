import os
import uuid
from typing import List, Optional
from fastapi import UploadFile, HTTPException
from datetime import datetime
import mimetypes

class FileUploadService:
    
    def __init__(self, upload_dir: str = "uploads/reports"):
        self.upload_dir = upload_dir
        self.allowed_image_types = {
            'image/jpeg', 'image/jpg', 'image/png', 'image/gif', 
            'image/webp', 'image/bmp', 'image/tiff'
        }
        self.allowed_document_types = {
            'application/pdf', 'application/msword', 
            'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
            'application/vnd.ms-excel', 
            'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
            'text/plain', 'text/csv'
        }
        self.max_file_size = 10 * 1024 * 1024  # 10MB por arquivo
        
        # Criar diretório de upload se não existir
        os.makedirs(self.upload_dir, exist_ok=True)
    
    def validate_file(self, file: UploadFile, file_type: str = "image") -> dict:
        """Validar arquivo de upload"""
        
        # Verificar se o arquivo foi enviado
        if not file or not file.filename:
            raise HTTPException(status_code=400, detail="Nenhum arquivo enviado")
        
        # Verificar tamanho
        if hasattr(file, 'size') and file.size > self.max_file_size:
            raise HTTPException(
                status_code=400, 
                detail=f"Arquivo muito grande. Máximo permitido: {self.max_file_size // (1024*1024)}MB"
            )
        
        # Verificar tipo MIME
        content_type = mimetypes.guess_type(file.filename)[0]
        if not content_type:
            raise HTTPException(status_code=400, detail="Tipo de arquivo não reconhecido")
        
        # Validar tipos permitidos
        if file_type == "image":
            if content_type not in self.allowed_image_types:
                raise HTTPException(
                    status_code=400, 
                    detail=f"Tipo de imagem não permitida. Tipos aceitos: {', '.join(self.allowed_image_types)}"
                )
        elif file_type == "document":
            if content_type not in self.allowed_document_types:
                raise HTTPException(
                    status_code=400, 
                    detail=f"Tipo de documento não permitido. Tipos aceitos: {', '.join(self.allowed_document_types)}"
                )
        
        return {
            "content_type": content_type,
            "is_valid": True
        }
    
    def generate_filename(self, original_filename: str, report_id: int) -> str:
        """Gerar nome único para arquivo"""
        # Extrair extensão
        name, ext = os.path.splitext(original_filename)
        
        # Gerar nome único com timestamp e UUID
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        unique_id = str(uuid.uuid4())[:8]
        
        return f"report_{report_id}_{timestamp}_{unique_id}{ext}"
    
    async def save_file(self, file: UploadFile, report_id: int, file_type: str = "image") -> dict:
        """Salvar arquivo no disco"""
        
        # Validar arquivo
        validation = self.validate_file(file, file_type)
        
        # Gerar nome único
        filename = self.generate_filename(file.filename, report_id)
        file_path = os.path.join(self.upload_dir, filename)
        
        # Salvar arquivo
        try:
            with open(file_path, "wb") as buffer:
                content = await file.read()
                buffer.write(content)
            
            # Obter tamanho do arquivo
            file_size = os.path.getsize(file_path)
            
            return {
                "filename": filename,
                "original_filename": file.filename,
                "file_path": file_path,
                "file_size": file_size,
                "content_type": validation["content_type"],
                "attachment_type": file_type
            }
            
        except Exception as e:
            # Remover arquivo se algo deu errado
            if os.path.exists(file_path):
                os.remove(file_path)
            raise HTTPException(status_code=500, detail=f"Erro ao salvar arquivo: {str(e)}")
    
    async def save_multiple_files(self, files: List[UploadFile], report_id: int, file_type: str = "image") -> List[dict]:
        """Salvar múltiplos arquivos"""
        
        if not files:
            raise HTTPException(status_code=400, detail="Nenhum arquivo enviado")
        
        if len(files) > 10:  # Limite de 10 arquivos por upload
            raise HTTPException(status_code=400, detail="Máximo de 10 arquivos permitidos por upload")
        
        saved_files = []
        errors = []
        
        for i, file in enumerate(files):
            try:
                saved_file = await self.save_file(file, report_id, file_type)
                saved_files.append(saved_file)
            except Exception as e:
                errors.append(f"Arquivo {i+1} ({file.filename}): {str(e)}")
        
        if errors and not saved_files:
            raise HTTPException(status_code=400, detail={"errors": errors})
        
        return saved_files
    
    def delete_file(self, file_path: str) -> bool:
        """Excluir arquivo do disco"""
        try:
            if os.path.exists(file_path):
                os.remove(file_path)
                return True
            return False
        except Exception:
            return False
    
    def get_file_info(self, file_path: str) -> Optional[dict]:
        """Obter informações do arquivo"""
        try:
            if not os.path.exists(file_path):
                return None
            
            stat = os.stat(file_path)
            return {
                "file_path": file_path,
                "file_size": stat.st_size,
                "created_at": datetime.fromtimestamp(stat.st_ctime),
                "modified_at": datetime.fromtimestamp(stat.st_mtime)
            }
        except Exception:
            return None
