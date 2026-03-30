from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session
from datetime import datetime

from app.models.orm.user_orm import UserORM
from app.models.domain.user import User
from app.storage.database.base_repository import SessionLocal


class UserRepository:
    def create(self, email: str, password_hash: str, role: str = "Patient") -> User:
        with SessionLocal() as session:
            db_user = UserORM(email=email, password_hash=password_hash, role=role)
            session.add(db_user)
            try:
                session.commit()
            except IntegrityError:
                session.rollback()
                raise ValueError("email already exists")

            session.refresh(db_user)
            return User(
                id=db_user.id,
                email=db_user.email,
                password_hash=db_user.password_hash,
                role=db_user.role,
                full_name=getattr(db_user, 'full_name', None),
                profile_photo_url=getattr(db_user, 'profile_photo_url', None),
            )

    def get_by_email(self, email: str) -> User | None:
        with SessionLocal() as session:
            db_user: UserORM | None = (
                session.query(UserORM)
                .filter(UserORM.email == email)
                .first()
            )

            if not db_user:
                return None

            return User(
                id=db_user.id,
                email=db_user.email,
                password_hash=db_user.password_hash,
                role=db_user.role,
                full_name=getattr(db_user, 'full_name', None),
                profile_photo_url=getattr(db_user, 'profile_photo_url', None),
            )

    def get_by_id(self, user_id: int, session: Session = None) -> User | None:
        """Get user by ID using provided session or creating new one."""
        if session is None:
            with SessionLocal() as new_session:
                return self._get_by_id_query(new_session, user_id)
        else:
            return self._get_by_id_query(session, user_id)
    
    def _get_by_id_query(self, session: Session, user_id: int) -> User | None:
        """Internal method to query user by ID."""
        db_user: UserORM | None = (
            session.query(UserORM)
            .filter(UserORM.id == user_id)
            .first()
        )

        if not db_user:
            return None

        return User(
            id=db_user.id,
            email=db_user.email,
            password_hash=db_user.password_hash,
            role=db_user.role,
            full_name=getattr(db_user, 'full_name', None),
            profile_photo_url=getattr(db_user, 'profile_photo_url', None),
        )

    def update_password(self, user_id: int, new_password_hash: str) -> None:
        """Atualiza senha do usuário com timestamp"""
        with SessionLocal() as session:
            db_user = session.query(UserORM).filter(UserORM.id == user_id).first()
            if not db_user:
                raise ValueError("user not found")
            
            db_user.password_hash = new_password_hash
            db_user.updated_at = datetime.utcnow()
            session.commit()

    def update_profile_photo(self, user_id: int, profile_photo_url: str) -> None:
        """Atualiza URL da foto de perfil do usuário com timestamp"""
        with SessionLocal() as session:
            db_user = session.query(UserORM).filter(UserORM.id == user_id).first()
            if not db_user:
                raise ValueError("user not found")
            
            db_user.profile_photo_url = profile_photo_url
            db_user.updated_at = datetime.utcnow()
            session.commit()

    def update_full_name(self, user_id: int, full_name: str) -> None:
        """Atualiza nome completo do usuário com timestamp"""
        with SessionLocal() as session:
            db_user = session.query(UserORM).filter(UserORM.id == user_id).first()
            if not db_user:
                raise ValueError("user not found")
            
            db_user.full_name = full_name
            db_user.updated_at = datetime.utcnow()
            session.commit()

    def get_user_complete_data(self, user_id: int) -> dict:
        """Retorna dados completos do usuário para perfil"""
        user = self.get_by_id(user_id)
        if not user:
            raise ValueError("user not found")
        
        return {
            "id": user.id,
            "email": user.email,
            "full_name": user.full_name,
            "role": user.role,
            "profile_photo_url": user.profile_photo_url
        }
