from dataclasses import dataclass
import os
import uuid
from datetime import datetime
from typing import Dict

from fastapi import UploadFile

from app.core.exceptions import BadRequest, Conflict, Unauthorized
from app.core.jwt_service import JwtService
from app.core.security import PasswordHasher
from app.storage.database.user_repository import UserRepository


@dataclass(frozen=True)
class AuthService:
    users: UserRepository
    hasher: PasswordHasher
    jwt: JwtService

    def register(self, email: str, password: str, role: str = "Patient") -> int:
        if not email or not password:
            raise BadRequest("email and password required")

        password_hash = self.hasher.hash_password(password)
        try:
            user = self.users.create(email=email.lower(), password_hash=password_hash, role=role)
        except ValueError:
            raise Conflict("email already exists")
        return user.id

    def login(self, email: str, password: str) -> str:
        if not email or not password:
            raise BadRequest("email and password required")

        user = self.users.get_by_email(email.lower())
        if not user or not self.hasher.verify_password(password, user.password_hash):
            raise Unauthorized("invalid credentials")

        return self.jwt.sign(user_id=user.id, email=user.email, role=user.role)

    def verify(self, token: str) -> dict:
        if not token:
            raise Unauthorized("missing token")
        try:
            return self.jwt.verify(token)
        except Exception:
            raise Unauthorized("invalid token")

    def get_user_by_id(self, user_id: int) -> Dict:
        """Busca usuário completo por ID para perfil"""
        return self.users.get_user_complete_data(user_id)

    def change_password(self, user_id: int, current_password: str, new_password: str) -> None:
        """Troca senha do usuário validando a senha atual"""
        user = self.users.get_by_id(user_id)
        if not user:
            raise BadRequest("user not found")
        
        # Verificar senha atual
        if not self.hasher.verify_password(current_password, user.password_hash):
            raise Unauthorized("current password is incorrect")
        
        # Gerar novo hash e atualizar
        new_password_hash = self.hasher.hash_password(new_password)
        self.users.update_password(user_id, new_password_hash)

    def upload_profile_photo(self, user_id: int, file: UploadFile) -> str:
        """Salva foto de perfil com validações robustas e retorna URL"""
        user = self.users.get_by_id(user_id)
        if not user:
            raise BadRequest("user not found")
        
        # Validações de arquivo
        if not file.filename:
            raise BadRequest("No file provided")
        
        # Extensões permitidas
        allowed_extensions = {'.jpg', '.jpeg', '.png', '.webp'}
        file_extension = '.' + file.filename.split('.')[-1].lower() if '.' in file.filename else ''
        
        if file_extension not in allowed_extensions:
            raise BadRequest(f"Unsupported file extension. Allowed: {', '.join(allowed_extensions)}")
        
        # MIME types permitidos
        allowed_mime_types = {
            'image/jpeg': ['.jpg', '.jpeg'],
            'image/png': ['.png'],
            'image/webp': ['.webp']
        }
        
        if file.content_type not in allowed_mime_types:
            raise BadRequest(f"Unsupported MIME type: {file.content_type}")
        
        # Validar consistência entre extensão e MIME type
        expected_extensions = allowed_mime_types.get(file.content_type, [])
        if file_extension not in expected_extensions:
            raise BadRequest(f"File extension {file_extension} doesn't match MIME type {file.content_type}")
        
        # Ler conteúdo para validar tamanho
        file_content = file.file.read()
        file_size = len(file_content)
        
        # Tamanho máximo: 5MB
        max_size = 5 * 1024 * 1024
        if file_size > max_size:
            raise BadRequest(f"File too large. Maximum size: {max_size // (1024*1024)}MB")
        
        # Mínimo 1KB para evitar arquivos vazios
        if file_size < 1024:
            raise BadRequest("File too small. Minimum size: 1KB")
        
        # Reset file pointer
        file.file.seek(0)
        
        # Criar diretório se não existir
        upload_dir = "/app/storage/profile_photos"
        os.makedirs(upload_dir, exist_ok=True)
        
        # Gerar nome seguro
        import uuid
        safe_filename = f"user_{user_id}_{uuid.uuid4().hex[:12]}{file_extension}"
        file_path = os.path.join(upload_dir, safe_filename)
        
        # Salvar arquivo
        with open(file_path, "wb") as buffer:
            buffer.write(file_content)
        
        # Gerar URL relativa
        profile_url = f"/media/profiles/{safe_filename}"
        
        # Atualizar banco
        self.users.update_profile_photo(user_id, profile_url)
        
        return profile_url
